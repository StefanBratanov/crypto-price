package priceretriever;

import com.google.inject.AbstractModule;
import common.PriceRetriever;

import javax.inject.Singleton;
import java.time.Clock;

public class PriceRetrieverModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PriceRetriever.class).to(CryptoComparePriceRetriever.class)
                .in(Singleton.class);
    }
}
