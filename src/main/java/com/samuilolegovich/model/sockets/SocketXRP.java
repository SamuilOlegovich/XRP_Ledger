package com.samuilolegovich.model.sockets;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.samuilolegovich.model.sockets.enums.StreamSubscriptionEnum;
import com.samuilolegovich.model.sockets.exceptions.InvalidStateException;
import com.samuilolegovich.model.sockets.interfaces.CommandListener;
import com.samuilolegovich.model.sockets.runnable.RestartSubscriberRun;
import com.samuilolegovich.subscribers.interfaces.StreamSubscriber;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A WebSocket client for the XRP ledger.
 * Subscribe to streams with subscribe() or send commands and wait for
 * a response with sendCommand().
 * Клиент WebSocket для реестра XRP.
 * Подпишитесь на потоки с помощью subscribe() или отправьте команды и дождитесь ответа с помощью sendCommand().
 */
public class SocketXRP extends WebSocketClient {
    private final Map<StreamSubscriptionEnum, StreamSubscriber> activeSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, CommandListener> commandListeners = new ConcurrentHashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(SocketXRP.class);

    private static final String CMD_UNSUBSCRIBE = "unsubscribe";
    private static final String CMD_SUBSCRIBE = "subscribe";
    private static final String ATTRIBUTE_TYPE = "type";
    private static final String ATTRIBUTE_ID = "id";
    private static final String COMMAND = "command";
    private static final String STREAMS = "streams";

    private volatile boolean closeWhenComplete;



    public SocketXRP(URI serverUri) {
        super(serverUri);
    }

    public SocketXRP(String serverUri) throws URISyntaxException {
        this(new URI(serverUri));
    }



    public String sendCommand(String command, CommandListener listener) throws InvalidStateException {
        return sendCommand(command, null, listener);
    }

    public String sendCommand(String command, Map<String, Object> parameters, CommandListener listener)
            throws InvalidStateException {
        checkOpen();
        String id = UUID.randomUUID().toString();

        JSONObject request = new JSONObject();
        request.put(COMMAND, command);
        request.put("id", id);

        if (parameters != null && !parameters.isEmpty()) { parameters.forEach(request::put); }

        send(request.toString());
        commandListeners.put(id, listener);
        return id;
    }

    public void subscribe(EnumSet<StreamSubscriptionEnum> streams, StreamSubscriber subscriber)
            throws InvalidStateException {
        checkOpen();
        LOG.info("Subscribing to: {}", streams);
        send(composeSubscribe(CMD_SUBSCRIBE, streams));
        streams.forEach(t -> activeSubscriptions.put(t, subscriber));
    }

    public void subscribe(EnumSet<StreamSubscriptionEnum> streams, Map<String, Object> parameters,
                          StreamSubscriber subscriber) throws InvalidStateException {
        checkOpen();
        LOG.info("Subscribing to: {}", streams);
        send(composeSubscribe(CMD_SUBSCRIBE, parameters, streams));
        streams.forEach(t -> activeSubscriptions.put(t, subscriber));
    }



    public void unsubscribe(EnumSet<StreamSubscriptionEnum> streams) throws InvalidStateException {
        checkOpen();
        LOG.info("Unsubscribing from: {}", streams);
        send(composeSubscribe(CMD_UNSUBSCRIBE, streams));
        streams.forEach(activeSubscriptions::remove);
    }

    public EnumSet<StreamSubscriptionEnum> getActiveSubscriptions() {
        return activeSubscriptions.isEmpty()
                ? EnumSet.noneOf(StreamSubscriptionEnum.class)
                : EnumSet.copyOf(activeSubscriptions.keySet());
    }

    public void closeWhenComplete() {
        closeWhenComplete = true;
    }

    @Override
    public void send(String message) {
        LOG.info("Sending message: {}", message);
        super.send(message);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        handshake.iterateHttpFields().forEachRemaining(LOG::debug);
        LOG.info("XRP ledger client opened");
    }

    @Override
    public void onMessage(String message) {
        long start = System.currentTimeMillis();
        LOG.info("XRPL client received a message:\n{}", message);
        JSONObject json = new JSONObject(message);

        if (json.has(ATTRIBUTE_TYPE)
                && (StreamSubscriptionEnum.byMessageType(json.getString(ATTRIBUTE_TYPE)) != null)) {

            StreamSubscriptionEnum subscription = StreamSubscriptionEnum.byMessageType(json.getString(ATTRIBUTE_TYPE));
            StreamSubscriber subscriber = activeSubscriptions.get(subscription);

            if (subscriber != null) {
                subscriber.onSubscription(subscription, json);
            }
        } else if (json.has(ATTRIBUTE_ID) && commandListeners.get(json.getString(ATTRIBUTE_ID)) != null) {
            commandListeners.get(json.getString(ATTRIBUTE_ID)).onResponse(json);
            commandListeners.remove(json.getString(ATTRIBUTE_ID));
        }

        if (closeWhenComplete && commandListeners.isEmpty() && activeSubscriptions.isEmpty()) { close(); }

        LOG.info("Ledger message processed in {}ms", System.currentTimeMillis() - start);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOG.info("XRP ledger client closed (code {}), reason given: {}", code, reason);
        activeSubscriptions.clear();
        commandListeners.clear();
        restartSocket(code);
    }

    @Override
    public void onError(Exception exception) {
        LOG.error("XRP ledger client error {}", exception);
        // Clear activeSubscriptions and commandListeners?
        // Is onError always followed by an onClose?

        // Очистить activeSubscriptions и commandListeners?
        // Всегда ли за onError следует onClose?
    }


    private String composeSubscribe(String command, EnumSet<StreamSubscriptionEnum> streams) {
        JSONObject request = new JSONObject();
        request.put(COMMAND, command);
        request.put(STREAMS, streams.stream().map(StreamSubscriptionEnum::getName).collect(Collectors.toList()));
        return request.toString();
    }

    private String composeSubscribe(String command, Map<String, Object> parameters,
                                    EnumSet<StreamSubscriptionEnum> streams) {
        JSONObject request = new JSONObject();
        request.put(COMMAND, command);
        request.put(STREAMS, streams.stream().map(StreamSubscriptionEnum::getName).collect(Collectors.toList()));
        if (parameters != null && !parameters.isEmpty()) { parameters.forEach(request::put); }
        return request.toString();
    }

    private void checkOpen() throws InvalidStateException {
        if (!isOpen()) { throw new InvalidStateException(); }
    }

    private void restartSocket(int code) {
        if (RestartSubscriberRun.FLAG) {
            RestartSubscriberRun restartSubscriberRun;
            if (code == 1006) {
                restartSubscriberRun = new RestartSubscriberRun(10000);
            } else if (code == -1) {
                restartSubscriberRun = new RestartSubscriberRun(20000);
            } else {
                restartSubscriberRun = new RestartSubscriberRun();
            }
            new Thread(restartSubscriberRun).start();
        }
    }
}
