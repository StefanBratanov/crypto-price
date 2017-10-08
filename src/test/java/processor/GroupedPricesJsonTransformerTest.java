package processor;

import com.google.common.io.CharStreams;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.io.InputStreamReader;

public class GroupedPricesJsonTransformerTest {

    private GroupedPricesJsonTransformer underTest;

    @Before
    public void init() {
        underTest = new GroupedPricesJsonTransformer();
    }

    @Test
    public void transforms() throws IOException {
        String json = CharStreams.toString(new InputStreamReader
                (this.getClass().getResourceAsStream("/sample-grouped-price.json")));

        JSONArray input = new JSONArray(json);

        String actual = underTest.transform(input);

        String expected = CharStreams.toString(new InputStreamReader
                (this.getClass().getResourceAsStream(
                        "/grouped-price-transformed.json")));

        JSONAssert.assertEquals(expected, actual, false);
    }

}