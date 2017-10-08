package processor;

import com.google.common.collect.Maps;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
class GroupedPricesJsonTransformer {

    String transform(JSONArray jsonArray) {
        JSONObject output = new JSONObject();
        JSONArray exchanges = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            output.put("crypto", json.getString("crypto"));
            output.put("updatedAt", json.getString("updatedAt"));
            Map<String, Object> pricesAndExchange = Maps.newHashMap();
            pricesAndExchange.put("exchange", json.getString("exchange"));
            pricesAndExchange.put("priceInGbp", json.getBigDecimal("priceInGbp"));
            pricesAndExchange.put("priceInEur", json.getBigDecimal("priceInEur"));
            pricesAndExchange.put("priceInUsd", json.getBigDecimal("priceInUsd"));
            exchanges.put(pricesAndExchange);
        }

        output.put("pricesByExchange", exchanges);

        return output.toString();
    }
}
