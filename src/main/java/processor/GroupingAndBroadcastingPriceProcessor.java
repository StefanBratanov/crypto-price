package processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import common.CryptoPrice;
import common.Exchange;
import common.PriceProcessor;
import common.WebSocketPriceBroadcaster;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

class GroupingAndBroadcastingPriceProcessor implements PriceProcessor {

    private static final Logger log = LoggerFactory.getLogger(GroupingAndBroadcastingPriceProcessor.class);

    private final WebSocketPriceBroadcaster webSocketPriceBroadcaster;
    private final List<Exchange> exchanges;
    private final Gson gson;
    private final GroupedPricesJsonTransformer groupedPricesJsonTransformer;

    private final Map<LocalDateTime, List<CryptoPrice>> cryptoPricesByTime
            = Maps.newConcurrentMap();

    @Inject
    GroupingAndBroadcastingPriceProcessor(WebSocketPriceBroadcaster webSocketPriceBroadcaster,
                                          List<Exchange> exchanges,
                                          Gson gson,
                                          GroupedPricesJsonTransformer groupedPricesJsonTransformer) {
        this.webSocketPriceBroadcaster = webSocketPriceBroadcaster;
        this.exchanges = exchanges;
        this.gson = gson;
        this.groupedPricesJsonTransformer = groupedPricesJsonTransformer;
    }

    @Override
    public void process(CryptoPrice cryptoPrice) {
        log.info("Starting to process: {}", cryptoPrice);
        LocalDateTime updatedAt = cryptoPrice.getUpdatedAt();
        //initialise new empty list of prices for a given date
        // and adds to the newly created or to the existing
        cryptoPricesByTime.computeIfAbsent(updatedAt, (uAt) ->
                Lists.newArrayList()).add(cryptoPrice);

        //group by crypto and check that the prices for all the
        //exchanges have been received
        List<CryptoPrice> cryptoPrices = cryptoPricesByTime.get(updatedAt);

        cryptoPrices
                .stream()
                .collect(groupingBy(CryptoPrice::getCrypto))
                .values()
                .stream()
                .filter(prices -> {
                    List<Exchange> storedExchanges = prices
                            .stream()
                            .map(CryptoPrice::getExchange)
                            .collect(Collectors.toList());
                    return storedExchanges.containsAll(exchanges);
                })
                .forEach(prices -> {
                    JSONArray jsonPrices =
                            new JSONArray(gson.toJson(prices));
                    String message =
                            groupedPricesJsonTransformer.transform(jsonPrices);
                    log.info("Broadcasting message: {} ",
                            message);
                    webSocketPriceBroadcaster.broadcast(message);
                    //remove all prices from the map which are
                    // already broadcasted
                    cryptoPricesByTime.get(updatedAt)
                            .removeAll(prices);
                });
    }
}
