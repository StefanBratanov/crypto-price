package common;

import com.google.gson.*;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class CommonModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(CommonModule.class);
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    protected void configure() {
        bind(Clock.class).toInstance(Clock.systemUTC());

        //setting properties
        Properties properties = new Properties();
        try {
            String propertiesFilename = "configuration.properties";
            InputStream propertiesFileInputStream = getClass().getClassLoader()
                    .getResourceAsStream(propertiesFilename);
            properties.load(propertiesFileInputStream);
            log.info("Loading properties from {}", propertiesFilename);
            Names.bindProperties(binder(), properties);
        } catch (Exception e) {
            log.error("Could not load config: ", e);
            throw new IllegalStateException(e);
        }
    }

    @Provides
    List<Exchange> exchanges(@Named("exchanges") String exchanges) {
        return Arrays.stream(exchanges.split(","))
                .map(Exchange::valueOf)
                .collect(Collectors.toList());
    }

    @Singleton
    @Provides
    Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (date, typeOfT, context) ->
                        new JsonPrimitive(date.format(DateTimeFormatter.ofPattern(TIME_FORMAT))))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                    String date = json.getAsJsonPrimitive().getAsString();
                    return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(TIME_FORMAT));
                })
                .create();

    }
}
