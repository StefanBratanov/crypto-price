package common;

import java.util.List;

public interface PriceRetriever {

    CryptoPrice retrieve(Crypto crypto, Exchange exchange);

    List<CryptoPrice> retrieveMultiple(List<Crypto> cryptos, Exchange exchange);

}
