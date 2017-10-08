package websocket;

import com.google.common.collect.Maps;
import com.google.gson.*;
import common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@ServerEndpoint(value = "/price", configurator = PriceEndpointConfigurator.class)
public class PriceEndpoint implements WebSocketPriceBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(PriceEndpoint.class);

    private final Map<String, Session> sessionsById = Maps.newConcurrentMap();
    private final PriceRetriever priceRetriever;
    private final Gson gson;

    @Inject
    public PriceEndpoint(PriceRetriever priceRetriever, Gson gson) {
        this.priceRetriever = priceRetriever;
        this.gson = gson;
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("Connected ... " + session.getId());
        sessionsById.put(session.getId(), session);
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        log.info("Received message from {} : {}", session.getId(), message);
        Crypto crypto;
        Exchange exchange;
        try {
            String[] splitted = message.split(",");
            crypto = Crypto.valueOf(splitted[0]);
            exchange = Exchange.valueOf(splitted[1]);
        } catch (IllegalArgumentException ex) {
            return "invalid message: " + message +
                    "Message Format: [crypto,exchange]";
        }
        CryptoPrice price = priceRetriever.retrieve(crypto, exchange);
        return gson.toJson(price);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessionsById.remove(session.getId());
        log.info("Session {} closed because of {}", session.getId(), closeReason);
    }

    @Override
    public void broadcast(String message) {
        log.info("Sessions currently opened: {}",
                sessionsById.values().stream()
                        .filter(Session::isOpen)
                        .map(Session::getId)
                        .collect(Collectors.toList()));

        sessionsById.values().forEach(session -> {
            try {
                session.getBasicRemote()
                        .sendText(message);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }

}
