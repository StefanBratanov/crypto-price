import com.google.inject.AbstractModule;
import common.CommonModule;
import common.GuiceInitialiser;
import priceretriever.PriceRetrieverModule;
import processor.ProcessorModule;
import scheduled.ScheduledModule;
import store.StoreModule;
import websocket.WebsocketModule;

public final class Application extends AbstractModule {

    @Override
    protected void configure() {
        install(new CommonModule());
        install(new PriceRetrieverModule());
        install(new ProcessorModule());
        install(new WebsocketModule());
        install(new StoreModule());
        install(new ScheduledModule());
    }

    public static void main(String[] args) {
        GuiceInitialiser.createAndStartServices(new Application());
    }
}
