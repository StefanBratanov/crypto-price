package scheduled;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class ScheduledModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), Service.class)
                .addBinding().to(ScheduledPriceProcessorService.class);

        Multibinder.newSetBinder(binder(), Service.class)
                .addBinding().to(ScheduledPriceStoreService.class);
    }
}
