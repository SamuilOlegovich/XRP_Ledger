package com.samuilolegovich.model.sockets.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Test {
    private static final Logger LOG = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws URISyntaxException, InterruptedException, InvalidStateException {

        // Get a client.
        XRPLedgerClient client = new XRPLedgerClient("wss://fh.xrpl.ws");
        client.connectBlocking(3000, TimeUnit.MILLISECONDS);

        // Send a command.
        client.sendCommand("ledger_current", (response) -> {
            LOG.info(response.toString(4));
        });

        // Send a command with parameters.
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ledger_index", "validated");
        client.sendCommand("ledger", parameters, (response) -> {
            LOG.info(response.toString(4));
        });

        // Subscribe to the transaction stream (add transactions to a list as they come in).
        List<String> transactions = new ArrayList<>();
        client.subscribe(EnumSet.of(StreamSubscriptionEnums.TRANSACTIONS), (subscription, message) -> {
            LOG.info("Got message from subscription {}: {}", subscription.getMessageType(), message);
            transactions.add(message.toString());
        });

        // Tell the client to close when there are no more pending responses
        // for commands and all subscriptions have been unsubscribed.
        client.closeWhenComplete();

        // While we still have a connection check if the number of transactions received
        // has reached 100. If it has then unsubscribe from the transaction stream.
        // This should trigger automatic closing of the client, because of the previous
        // call to closeWhenComplete() (assuming all commands have been responded to).
        while (client.isOpen()) {
            LOG.info("Waiting for messages (transactions received: {})...", transactions.size());
            Thread.sleep(100);
            if (transactions.size() >= 100 && !client.getActiveSubscriptions().isEmpty()) {
                client.unsubscribe(client.getActiveSubscriptions());
            }
        }
    }
}
