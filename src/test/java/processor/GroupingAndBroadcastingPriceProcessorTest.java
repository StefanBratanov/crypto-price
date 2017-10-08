package processor;

import com.google.gson.*;
import common.Crypto;
import common.CryptoPrice;
import common.WebSocketPriceBroadcaster;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static common.Crypto.BTC;
import static common.Exchange.Coinfloor;
import static common.Exchange.Kraken;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.time.LocalDateTime.of;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupingAndBroadcastingPriceProcessorTest {

    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final LocalDateTime TIME_1 =
            of(2017, 7,
                    28, 12,
                    30, 25);

    private static final LocalDateTime TIME_2 =
            of(2017, 7,
                    28, 13,
                    30, 25);

    private static final LocalDateTime TIME_3 =
            of(2017, 7,
                    28, 13,
                    30, 30);

    @Mock
    private WebSocketPriceBroadcaster broadcaster;

    @Mock
    private GroupedPricesJsonTransformer groupedPricesJsonTransformer;

    private GroupingAndBroadcastingPriceProcessor underTest;


    @Before
    public void init() {
        underTest = new GroupingAndBroadcastingPriceProcessor
                (broadcaster, asList(Kraken, Coinfloor), gson(), groupedPricesJsonTransformer);
    }

    @Test
    public void groupsPricesAndBroadcasts() {
        when(groupedPricesJsonTransformer.transform(any()))
                .thenReturn("transformedPrices");

        //first grouping
        CryptoPrice btcGroup1price1 = group1price1(BTC);
        CryptoPrice btcGroup1price2 = group1price2(BTC);

        //second grouping
        CryptoPrice btcGroup2price1 = group2price1(BTC);
        CryptoPrice btcGroup2price2 = group2price2(BTC);

        //only one price for one exchange for group 3
        CryptoPrice btcGroup3price1 = group3price1(BTC);

        underTest.process(btcGroup1price1);
        underTest.process(btcGroup1price2);
        underTest.process(btcGroup2price1);
        underTest.process(btcGroup2price2);
        underTest.process(btcGroup3price1);

        verify(broadcaster, times(2))
                .broadcast(anyString());

    }

    private CryptoPrice group1price1(Crypto crypto) {
        return new CryptoPrice(crypto, Kraken, TIME_1,
                randomBigDecimal(), randomBigDecimal(), randomBigDecimal());
    }

    private CryptoPrice group1price2(Crypto crypto) {
        return new CryptoPrice(crypto, Coinfloor, TIME_1,
                randomBigDecimal(), randomBigDecimal(), randomBigDecimal());
    }

    private CryptoPrice group2price1(Crypto crypto) {
        return new CryptoPrice(crypto, Kraken, TIME_2,
                randomBigDecimal(), randomBigDecimal(), randomBigDecimal());
    }

    private CryptoPrice group2price2(Crypto crypto) {
        return new CryptoPrice(crypto, Coinfloor, TIME_2,
                randomBigDecimal(), randomBigDecimal(), randomBigDecimal());
    }

    private CryptoPrice group3price1(Crypto crypto) {
        return new CryptoPrice(crypto, Kraken, TIME_3,
                randomBigDecimal(), randomBigDecimal(), randomBigDecimal());
    }

    private BigDecimal randomBigDecimal() {
        Random random = new Random();
        double number = (random.nextDouble() * 100) + 1;
        return BigDecimal.valueOf(number).setScale(2, ROUND_HALF_UP);
    }

    private Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (date, typeOfT, context) ->
                        new JsonPrimitive(date.format(DateTimeFormatter.ofPattern(TIME_FORMAT))))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                    String date = json.getAsJsonPrimitive().getAsString();
                    return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(TIME_FORMAT));
                })
                .create();
    }


}