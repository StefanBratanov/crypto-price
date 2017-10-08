package priceretriever;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import common.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

class CryptoComparePriceRetriever implements PriceRetriever {

    private static final Logger log = LoggerFactory.getLogger(CryptoComparePriceRetriever.class);

    private final CryptoCompareJsonParser cryptoCompareJsonParser;

    @Inject
    CryptoComparePriceRetriever(CryptoCompareJsonParser cryptoCompareJsonParser) {
        this.cryptoCompareJsonParser = cryptoCompareJsonParser;
    }

    @Override
    public synchronized CryptoPrice retrieve(Crypto crypto, Exchange exchange) {
        String apiEndpoint = String.format("https://min-api.cryptocompare.com/data/price?fsym=%s&tsyms=%s&e=%s",
                crypto, Arrays.stream(Currency.values())
                        .map(String::valueOf).collect(Collectors.joining(",")), exchange);

        log.info("Going to connect to CryptoCompare using endpoint: {}", apiEndpoint);
        Future<HttpResponse<JsonNode>> futureResponse = Unirest.get(apiEndpoint)
                .asJsonAsync();

        HttpResponse<JsonNode> response;
        try {
            response = futureResponse.get(5, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new IllegalStateException(String.format("Error retrieving price for: %s", crypto), ex);
        }

        if (response.getStatus() != 200) {
            throw new IllegalStateException(format("There was an error while retrieving price. Status: %s , Status Text: %s ",
                    response.getStatus(), response.getStatusText()));
        }

        JSONObject responseBody = response.getBody().getObject();

        log.info("Received single price response at {} : {} ", LocalDateTime.now(), responseBody);

        return cryptoCompareJsonParser.fromSinglePriceResponse(crypto, responseBody, exchange);

    }

    @Override
    public synchronized List<CryptoPrice> retrieveMultiple(List<Crypto> cryptos, Exchange exchange) {
        String apiEndpoint = String.format("https://min-api.cryptocompare.com/data/pricemulti?fsyms=%s&tsyms=%s&e=%s",
                cryptos.stream().map(String::valueOf).collect(joining(",")),
                stream(Currency.values())
                        .map(String::valueOf).collect(Collectors.joining(",")), exchange);

        log.info("Going to connect to CryptoCompare using endpoint: {}", apiEndpoint);
        Future<HttpResponse<JsonNode>> futureResponse = Unirest.get(apiEndpoint)
                .asJsonAsync();

        HttpResponse<JsonNode> response;
        try {
            response = futureResponse.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException tex) {
            log.error("The request to CryptoCompare timeouted", apiEndpoint);
            return emptyList();
        } catch (Exception e) {
            log.error("Error retrieving price from: " + apiEndpoint, e);
            return emptyList();
        }

        if (response.getStatus() != 200) {
            log.error(format("There was an error while retrieving price. Status: %s , Status Text: %s ",
                    response.getStatus(), response.getStatusText()));
            return emptyList();
        }

        JSONObject responseBody = response.getBody().getObject();

        log.info("Received multi price response at {} : {} ", LocalDateTime.now(), responseBody);

        return cryptoCompareJsonParser.fromMultiPriceResponse(responseBody, exchange);
    }
}
