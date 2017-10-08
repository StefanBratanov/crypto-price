package common;

import com.google.common.util.concurrent.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

import static java.lang.String.format;

@Singleton
class ServiceManager {

    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

    private Set<Service> services;

    @Inject
    ServiceManager(Set<Service> services) {
        this.services = services;
    }

    void startAll() {
        services.forEach(service -> {
            service.startAsync();
            log.info(format("[%s] is starting...", service.getClass().getTypeName()));
        });
    }

    void stopAll() {
        services.forEach(service -> {
            service.stopAsync();
            service.awaitTerminated();
            log.info(format("[%s] has been stopped", service.getClass().getCanonicalName()));
        });

    }
}
