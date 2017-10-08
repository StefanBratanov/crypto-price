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

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

class ScheduledPriceProcessorService extends AbstractScheduledService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPriceProcessorService.class);

    private final PriceProcessor priceProcessor;
    private final PriceRetriever priceRetriever;
    private final String priceProcessingSchedule;
    private final List<Exchange> exchanges;
    private final Clock clock;

    @Inject
    ScheduledPriceProcessorService(PriceProcessor priceProcessor,
                                   PriceRetriever priceRetriever,
                                   @Named("price.processing.schedule") String priceProcessingSchedule,
                                   List<Exchange> exchanges,
                                   Clock clock) {
        this.priceProcessor = priceProcessor;
        this.priceRetriever = priceRetriever;
        this.priceProcessingSchedule = priceProcessingSchedule;
        this.exchanges = exchanges;
        this.clock = clock;
    }

    @Override
    protected void runOneIteration() throws Exception {
        LocalDateTime processingTime = LocalDateTime.now(clock);
        log.info("Start processing cryptoPrices at: {} for exchanges: {}",
                processingTime, exchanges);


        exchanges.stream()
                .flatMap((ex) -> priceRetriever
                        .retrieveMultiple(Arrays.asList(Crypto.values()), ex)
                        .stream())
                .peek(cp -> addArtificialDelay())
                .peek(cp -> normaliseTime(cp, processingTime))
                .forEach(priceProcessor::process);

    }

    @Override
    protected void shutDown() throws Exception {
        //
    }

    @Override
    protected Scheduler scheduler() {
        String[] schedule = priceProcessingSchedule.split(",");
        long delay = Long.parseLong(schedule[0]);
        TimeUnit timeUnit = TimeUnit.valueOf(schedule[1]);
        return Scheduler.newFixedDelaySchedule(0, delay, timeUnit);
    }

    private void normaliseTime(CryptoPrice cryptoPrice, LocalDateTime normalisedTime) {
        cryptoPrice.setUpdatedAt(normalisedTime);
    }

    private void addArtificialDelay() {
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            //TODO: error handling
        }
    }
}
