package store;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import common.CryptoPriceStore;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

import static java.lang.String.format;

public class StoreModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(StoreModule.class);

    @Override
    protected void configure() {
        bind(CryptoPriceStore.class).to(PostgresCryptoPriceStore.class);
    }

    @Provides
    @Singleton
    Session session() {
        SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = sessionFactory.openSession();
        log.info(format("Successfully connected to database[%s] using Hibernate", sessionFactory
                .getProperties().get("hibernate.connection.url")));
        return session;
    }
}
