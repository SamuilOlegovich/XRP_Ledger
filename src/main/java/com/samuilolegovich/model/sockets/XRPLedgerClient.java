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
import com.samuilolegovich.model.sockets.interfaces.StreamSubscriber;
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
public class XRPLedgerClient extends WebSocketClient {
    private final Map<StreamSubscriptionEnum, StreamSubscriber> activeSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, CommandListener> commandListeners = new ConcurrentHashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(XRPLedgerClient.class);

    private static final String CMD_UNSUBSCRIBE = "unsubscribe";
    private static final String CMD_SUBSCRIBE = "subscribe";
    private static final String ATTRIBUTE_TYPE = "type";
    private static final String ATTRIBUTE_ID = "id";
    private static final String COMMAND = "command";
    private static final String STREAMS = "streams";

    private volatile boolean closeWhenComplete = false;




    public XRPLedgerClient(URI serverUri) {
        super(serverUri);
    }

    public XRPLedgerClient(String serverUri) throws URISyntaxException {
        this(new URI(serverUri));
    }

    // ---->>>
    public String sendCommand(String command, CommandListener listener) throws InvalidStateException {
        return sendCommand(command, null, listener);
    }

    // ---->>>
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
        streams.forEach(t -> activeSubscriptions.remove(t));
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
            // **************************************************************  >>>>>>>>>>>  {"validated_ledgers":"26210849-27715662","fee_base":10,"ledger_index":27715662,"ledger_hash":"A51E8A74AA868E49BFA782D351A79C9103B44168082BC7E57CA5A9F592EB10A8","txn_count":2,"fee_ref":10,"ledger_time":705700431,"type":"ledgerClosed","reserve_base":10000000,"reserve_inc":2000000}
           // {"fee_base":10,"fee_ref":10,"ledger_hash":"A51E8A74AA868E49BFA782D351A79C9103B44168082BC7E57CA5A9F592EB10A8","ledger_index":27715662,"ledger_time":705700431,"reserve_base":10000000,"reserve_inc":2000000,"txn_count":2,"type":"ledgerClosed","validated_ledgers":"26210849-27715662"}
            //             // {"engine_result":"tesSUCCESS","engine_result_code":0,"engine_result_message":"The transaction was applied. Only final in a validated ledger.","ledger_hash":"4C8FAF91B85BE899418A38C80F3CF915AD0DF16C07DAAF2F986902C5C66F0520","ledger_index":27715638,"meta":{"AffectedNodes":[{"ModifiedNode":{"FinalFields":{"Account":"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe","Balance":"95204171442574848","Flags":0,"OwnerCount":0,"Sequence":3791059},"LedgerEntryType":"AccountRoot","LedgerIndex":"31CCE9D28412FF973E9AB6D0FA219BACF19687D9A2456A0C2ABC3280E9D47E37","PreviousFields":{"Balance":"95204171432574848"},"PreviousTxnID":"D982EDB40C85002BFD773885C909BAC16B792F71354AF05E7F49FC561C914D6E","PreviousTxnLgrSeq":27715632}},{"ModifiedNode":{"FinalFields":{"Account":"rPM2BhjjbWowF2cn5nMFeo5fx2WgysM3qW","Balance":"989999990","Flags":0,"OwnerCount":0,"Sequence":27715633},"LedgerEntryType":"AccountRoot","LedgerIndex":"D4252A96E93ABA1193C0F917A698FCD6CD24EBCE9144ABFC43E331D9227B90A3","PreviousFields":{"Balance":"1000000000","Sequence":27715632},"PreviousTxnID":"D982EDB40C85002BFD773885C909BAC16B792F71354AF05E7F49FC561C914D6E","PreviousTxnLgrSeq":27715632}}],"TransactionIndex":1,"TransactionResult":"tesSUCCESS","delivered_amount":"10000000"},"status":"closed","transaction":{"Account":"rPM2BhjjbWowF2cn5nMFeo5fx2WgysM3qW","Amount":"10000000","Destination":"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe","DestinationTag":777,"Fee":"10","Flags":2147483648,"LastLedgerSequence":27715640,"Sequence":27715632,"SigningPubKey":"ED77393D0DD3A77061E5F9DE81793BB56650D99CF426339D0E64A7A495932289A0","TransactionType":"Payment","TxnSignature":"B5BF2B1AFE7F9E98F51329DBF3DDFDD63E28C2183534CB67778F12E40F85B0328825D4BF8AEDB2145EF97B5C5B916DAB8A35E4876C7FC75F27F319717ECDDD0D","date":705700360,"hash":"3EAECA98809DF416A45FA677555912AFFC39A49E2F2D3E40D20DC8A76BCDA71C"},"type":"transaction","validated":true}
            StreamSubscriptionEnum subscription = StreamSubscriptionEnum.byMessageType(json.getString(ATTRIBUTE_TYPE));
            StreamSubscriber subscriber = activeSubscriptions.get(subscription);
            if (subscriber != null) {
                subscriber.onSubscription(subscription, json);
            }
        } else if (json.has(ATTRIBUTE_ID) && commandListeners.get(json.getString(ATTRIBUTE_ID)) != null) {
            commandListeners.get(json.getString(ATTRIBUTE_ID)).onResponse(json);
            commandListeners.remove(json.getString(ATTRIBUTE_ID));
        }

        if (closeWhenComplete && commandListeners.isEmpty() && activeSubscriptions.isEmpty()) {
            close();
        }

        LOG.info("Ledger message processed in {}ms", System.currentTimeMillis() - start);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOG.info("XRP ledger client closed (code {}), reason given: {}", code, reason);
        activeSubscriptions.clear();
        commandListeners.clear();
    }

    @Override
    public void onError(Exception exception) {
        LOG.error("XRP ledger client error {}", exception);
        // clear activeSubscriptions and commandListeners?
        // Is onError always followed by an onClose?
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
        if (!isOpen()) {
            throw new InvalidStateException();
        }
    }
}
