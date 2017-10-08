package websocket;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import common.WebSocketPriceBroadcaster;
import org.glassfish.tyrus.server.Server;

import javax.inject.Named;

public class WebsocketModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), Service.class)
                .addBinding().to(WebsocketService.class);

        bind(WebSocketPriceBroadcaster.class)
                .to(PriceEndpoint.class);
    }

    @Provides
    @Singleton
    Server server(@Named("websocket.port") int port) {
        return new Server("localhost", port,
                "/crypto", PriceEndpoint.class);
    }

}
