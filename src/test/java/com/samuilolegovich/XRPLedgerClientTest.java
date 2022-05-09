package com.samuilolegovich;

import com.samuilolegovich.model.sockets.XRPLedgerClient;
import com.samuilolegovich.model.sockets.enums.StreamSubscriptionEnum;
import com.samuilolegovich.model.sockets.exceptions.InvalidStateException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class XRPLedgerClientTest {
    private XRPLedgerClient client;
    private List<String> transactions;

    private static final Logger LOG = LoggerFactory.getLogger(XRPLedgerClientTest.class);

    @Before
    public void startClient() throws URISyntaxException, InterruptedException {
        // Get a client.
        // Получите клиента ********************************************************************************************
        this.client = new XRPLedgerClient("wss://fh.xrpl.ws");
        client.connectBlocking(3000, TimeUnit.MILLISECONDS);
        transactions = new ArrayList<>();
    }

    @Test
    public void sendCommand() throws InvalidStateException {
        // Send a command.
        // Отправить команду *******************************************************************************************
        client.sendCommand("ledger_current", (response) -> {
            LOG.info(response.toString(4));
        });
    }

    @Test
    public void sendCommandWithParameters() throws InvalidStateException {
        // Send a command with parameters.
        // Отправить команду с параметрами *****************************************************************************
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ledger_index", "validated");
        client.sendCommand("ledger", parameters, (response) -> {
            LOG.info(response.toString(4));
        });
    }

    @Test
    public void subscribeToTheTransactionStream() throws InvalidStateException {
        // Subscribe to the transaction stream (add transactions to a list as they come in).
        // Подпишитесь на поток транзакций (добавляйте транзакции в список по мере их поступления) *********************
        client.subscribe(EnumSet.of(StreamSubscriptionEnum.TRANSACTIONS), (subscription, message) -> {
            LOG.info("Получил сообщение от подписки {}: {}", subscription.getMessageType(), message);
            transactions.add(message.toString());
        });
    }

    @After
    public void stopClient() throws InterruptedException, InvalidStateException {
        // Tell the client to close when there are no more outstanding responses
        // for commands and all subscriptions have been unsubscribed.
        // Скажите клиенту закрыться,
        // когда больше не будет ожидающих ответов на команды и все подписки будут отменены. ***************************
        client.closeWhenComplete();

        // While we still have a connection check if the number of transactions received
        // has reached 100. If it has then unsubscribe from the transaction stream.
        // This should trigger automatic closing of the client, because of the previous
        // call to closeWhenComplete() (assuming all commands have been responded to).
        // Пока у нас еще есть проверка соединения, достигло ли количество полученных транзакций 100.
        // Если оно есть, то отписываемся от потока транзакций.
        // Это должно вызвать автоматическое закрытие клиента из-за предыдущего вызова closeWhenComplete()
        // (при условии, что все команды были обработаны). *************************************************************
        while (client.isOpen()) {
            LOG.info("Ожидание сообщений (транзакции получены: {})...", transactions.size());
            Thread.sleep(100);
            if (transactions.size() >= 100 && !client.getActiveSubscriptions().isEmpty()) {
                client.unsubscribe(client.getActiveSubscriptions());
            }
        }
    }






}
