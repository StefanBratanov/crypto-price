package common;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.mashape.unirest.http.Unirest;
import org.hibernate.Session;

import java.io.IOException;

public class GuiceInitialiser {

    private static Injector injector;

    public static void createAndStartServices(Module... modules) {

        injector = Guice.createInjector(modules);

        ServiceManager serviceManager = injector.getInstance(ServiceManager.class);

        serviceManager.startAll();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serviceManager.stopAll();
            //closing hibernate session
            Session session = injector.getInstance(Session.class);
            session.close();
            try {
                Unirest.shutdown();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }));
    }

    public static Injector getInjector() {
        return injector;
    }
}
