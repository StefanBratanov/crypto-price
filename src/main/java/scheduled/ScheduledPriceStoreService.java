package scheduled;

import com.google.common.util.concurrent.AbstractScheduledService;
import common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class ScheduledPriceStoreService extends AbstractScheduledService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPriceStoreService.class);

    private final PriceRetriever priceRetriever;
    private final CryptoPriceStore cryptoPriceStore;
    private final Clock clock;
    private final String priceStoreSchedule;
    private final List<Exchange> exchanges;

    @Inject
    ScheduledPriceStoreService(PriceRetriever priceRetriever,
                               CryptoPriceStore cryptoPriceStore,
                               Clock clock,
                               @Named("price.store.schedule") String priceStoreSchedule,
                               List<Exchange> exchanges) {
        this.priceRetriever = priceRetriever;
        this.cryptoPriceStore = cryptoPriceStore;
        this.clock = clock;
        this.priceStoreSchedule = priceStoreSchedule;
        this.exchanges = exchanges;
    }

    @Override
    protected void runOneIteration() throws Exception {
        LocalDateTime normalisedTime = LocalDateTime.now(clock);
        log.info("Start storing crypto prices at: {} for exchanges: {}",
                normalisedTime, exchanges);

        exchanges.stream()
                .flatMap((ex) -> priceRetriever
                        .retrieveMultiple(Arrays.asList(Crypto.values()), ex)
                        .stream())
                .peek(cp -> normaliseTime(cp, normalisedTime))
                .forEach(cryptoPriceStore::save);
    }

    @Override
    protected Scheduler scheduler() {
        String[] schedule = priceStoreSchedule.split(",");
        long delay = Long.parseLong(schedule[0]);
        TimeUnit timeUnit = TimeUnit.valueOf(schedule[1]);
        return Scheduler.newFixedDelaySchedule(0, delay, timeUnit);
    }

    private void normaliseTime(CryptoPrice cryptoPrice, LocalDateTime normalisedTime) {
        cryptoPrice.setUpdatedAt(normalisedTime);
    }
}
