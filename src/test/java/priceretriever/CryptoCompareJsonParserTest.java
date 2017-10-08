package priceretriever;

import com.google.common.io.CharStreams;
import common.CryptoPrice;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static common.Crypto.BTC;
import static common.Exchange.Kraken;
import static org.assertj.core.api.Assertions.assertThat;

public class CryptoCompareJsonParserTest {

    private static final LocalDateTime TIME = LocalDateTime.of(2017, 7, 28, 13, 45, 30);

    private CryptoCompareJsonParser underTest;

    @Before
    public void init() {
        Clock clock = Clock.fixed(
                TIME.toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC"));
        underTest = new CryptoCompareJsonParser(clock);
    }

    @Test
    public void parsesJsonResponseForMultiPrice() throws Exception {
        String json = CharStreams.toString(new InputStreamReader
                (this.getClass().getResourceAsStream("/sample-multi-price-response.json")));
        JSONObject response = new JSONObject(json);

        List<CryptoPrice> cryptoPrices = underTest.fromMultiPriceResponse(response, Kraken);

        assertThat(cryptoPrices).contains(new CryptoPrice(BTC, Kraken, TIME,
                new BigDecimal("3170.45"),
                new BigDecimal("3527.99"),
                new BigDecimal("4174.82")));

        assertThat(cryptoPrices).hasSize(1);
    }

    @Test
    public void parsesJsonResponseForSinglePrice() throws Exception {
        String json = CharStreams.toString(new InputStreamReader
                (this.getClass().getResourceAsStream("/sample-single-price-response.json")));

        JSONObject response = new JSONObject(json);

        CryptoPrice actual = underTest.fromSinglePriceResponse(BTC, response,Kraken);

        assertThat(actual).isEqualTo(new CryptoPrice(BTC, Kraken, TIME,
                new BigDecimal("3160.45"),
                new BigDecimal("3523.5"),
                new BigDecimal("4160.12")));

    }
}