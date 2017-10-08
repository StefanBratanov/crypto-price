package websocket;

import com.google.common.util.concurrent.AbstractService;
import org.glassfish.tyrus.server.Server;

import javax.inject.Inject;
import javax.websocket.DeploymentException;

class WebsocketService extends AbstractService {

    private final Server server;

    @Inject
    WebsocketService(Server server) {
        this.server = server;
    }

    @Override
    protected void doStart() {
        try {
            server.start();
        } catch (DeploymentException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void doStop() {
        server.stop();
    }
}
