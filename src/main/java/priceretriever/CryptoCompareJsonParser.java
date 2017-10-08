package priceretriever;

import common.Crypto;
import common.CryptoPrice;
import common.Exchange;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Streams.stream;

@Singleton
class CryptoCompareJsonParser {

    private final Clock clock;

    @Inject
    CryptoCompareJsonParser(Clock clock) {
        this.clock = clock;
    }

    List<CryptoPrice> fromMultiPriceResponse(JSONObject jsonObject, Exchange exchange) {
        LocalDateTime updatedAt = LocalDateTime.now(clock);
        return stream(jsonObject.keys())
                .map(key -> {
                    JSONObject priceJson = jsonObject.getJSONObject(key);
                    return new CryptoPrice(Crypto.valueOf(key), exchange, updatedAt,
                            priceJson.getBigDecimal("GBP"),
                            priceJson.getBigDecimal("EUR"),
                            priceJson.getBigDecimal("USD"));
                })
                .collect(Collectors.toList());
    }

    CryptoPrice fromSinglePriceResponse(Crypto crypto, JSONObject jsonObject, Exchange exchange) {
        LocalDateTime updatedAt = LocalDateTime.now(clock);

        return new CryptoPrice(crypto, exchange, updatedAt,
                jsonObject.getBigDecimal("GBP"),
                jsonObject.getBigDecimal("EUR"),
                jsonObject.getBigDecimal("USD"));
    }
}
