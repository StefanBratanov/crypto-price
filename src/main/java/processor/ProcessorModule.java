package processor;

import com.google.inject.AbstractModule;
import common.PriceProcessor;

public class ProcessorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PriceProcessor.class).to(GroupingAndBroadcastingPriceProcessor.class);
    }

}
