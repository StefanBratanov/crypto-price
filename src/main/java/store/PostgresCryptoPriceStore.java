package store;

import common.CryptoPrice;
import common.CryptoPriceStore;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
class PostgresCryptoPriceStore implements CryptoPriceStore {

    private static final Logger log = LoggerFactory.getLogger(PostgresCryptoPriceStore.class);

    private final Session session;

    @Override
    public void save(CryptoPrice cryptoPrice) {

        session.beginTransaction();

        session.save(cryptoPrice);
        log.info("{} has been saved to the database.",
                cryptoPrice);

        session.getTransaction().commit();
    }
}
