package websocket;

import com.google.inject.Injector;
import common.GuiceInitialiser;

import javax.websocket.server.ServerEndpointConfig;

import static java.util.Objects.isNull;

public class PriceEndpointConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        Injector injector = GuiceInitialiser.getInjector();
        if (isNull(injector)) {
            return getEndpointInstance(endpointClass);
        }
        return injector.getInstance(endpointClass);
    }
}
