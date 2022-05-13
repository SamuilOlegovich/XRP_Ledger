package com.samuilolegovich;

import com.samuilolegovich.enums.BooleanEnum;
import com.samuilolegovich.enums.StringEnum;
import com.samuilolegovich.model.PaymentManager.PaymentManagerXRP;
import com.samuilolegovich.model.sockets.XRPLedgerClient;
import com.samuilolegovich.model.sockets.enums.StreamSubscriptionEnum;
import com.samuilolegovich.model.sockets.exceptions.InvalidStateException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.samuilolegovich.enums.StringEnum.*;


public class XRPLedgerClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(XRPLedgerClientTest.class);

    private final String RESPONSE ="response";
    private final String SUCCESS ="success";
    private final String STATUS ="status";
    private final String TYPE ="type";
    private final String ID ="id";
    private final int TAG = 777;

    private int transactionsSize = 100;

    private PaymentManagerXRP paymentManager;
    private List<String> transactions;
    private XRPLedgerClient client;
    private String idCommand;


    @Before
    public void startClient() throws URISyntaxException, InterruptedException {
        // Get a client.
        // Получите клиента ********************************************************************************************
        transactions = new ArrayList<>();
        Locale.setDefault(Locale.ENGLISH);
        BooleanEnum.setValue(BooleanEnum.IS_REAL, false);
        BooleanEnum.setValue(BooleanEnum.IS_WALLET_TEST, false);

        client = new XRPLedgerClient(WSS_REAL.getValue());
        client.connectBlocking(3000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void sendCommand() throws InvalidStateException {
        // Send a command.
        // Отправить команду *******************************************************************************************
         idCommand = client.sendCommand("ledger_current", (response) -> {
             Assert.assertEquals(SUCCESS, response.getString(STATUS));
             Assert.assertEquals(RESPONSE, response.getString(TYPE));
             Assert.assertEquals(idCommand, response.getString(ID));
             LOG.info(response.toString(4));
             conclusionAboutPositiveResult();
        });
    }

    @Test
    public void sendCommandWithParameters() throws InvalidStateException {
        // Send a command with parameters.
        // Отправить команду с параметрами *****************************************************************************
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ledger_index", "validated");

        idCommand = client.sendCommand("ledger", parameters, (response) -> {
            Assert.assertEquals(SUCCESS, response.getString(STATUS));
            Assert.assertEquals(RESPONSE, response.getString(TYPE));
            Assert.assertEquals(idCommand, response.getString(ID));
            LOG.info(response.toString(4));
            conclusionAboutPositiveResult();
        });
    }

    @Test
    public void sendCommandWithParametersAccountChannels() throws InvalidStateException {
        // Send a command with parameters.
        // Отправить команду с параметрами *****************************************************************************
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("account", "rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn");
        parameters.put("destination_account", "ra5nK24KXen9AHvsdFTKHSANinZseWnPcX");
        parameters.put("ledger_index", "validated");

        idCommand = client.sendCommand("account_channels", parameters, (response) -> {
            Assert.assertEquals(SUCCESS, response.getString(STATUS));
            Assert.assertEquals(RESPONSE, response.getString(TYPE));
            Assert.assertEquals(idCommand, response.getString(ID));
            Assert.assertEquals("true", response.getString("validated"));
            LOG.info(response.toString(4));
            conclusionAboutPositiveResult();
        });
    }


    // {"engine_result":"tesSUCCESS",
    //  "engine_result_code":0,
    //  "engine_result_message":"The transaction was applied. Only final in a validated ledger.",
    //  "ledger_hash":"7F0484A6DE51807C6855FA0E6DCFD0FC06F35330977AD89E6ECCD65BBEAAF055",
    //  "ledger_index":71579825,
    //  "meta":{
    //          "AffectedNodes":[
    //                          {
    //                          "ModifiedNode":{
    //                                          "FinalFields":{
    //                                                      "Account":"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45",
    //                                                      "Balance":"2519012680",
    //                                                      "Flags":0,
    //                                                      "OwnerCount":15,
    //                                                      "Sequence":67761420},
    //                                          "LedgerEntryType":"AccountRoot",
    //                                          "LedgerIndex":"2FFD2EE64E5ED2B1EA2A6B281ABFA02049475021B054E0E6A0AD03A470543283",
    //                                          "PreviousFields":{
    //                                                      "Balance":"2520012695",
    //                                                      "Sequence":67761419},
    //                                          "PreviousTxnID":"612A735915B92AAD9FF3C7325A14203F2A969E82095B4727E084F4F7EA1BEEE0",
    //                                          "PreviousTxnLgrSeq":71579596}
    //                         },{"ModifiedNode":{
    //                                          "FinalFields":{
    //                                                      "Account":"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx",
    //                                                      "Balance":"49979548",
    //                                                      "Flags":0,
    //                                                      "OwnerCount":7,
    //                                                      "Sequence":122},
    //                                          "LedgerEntryType":"AccountRoot",
    //                                          "LedgerIndex":"CFF42BC480FB4AD45D47A01463BBAC6339A0C1A219F51F11FF4413C134EFD3A8",
    //                                          "PreviousFields":{
    //                                                      "Balance":"48979548"},
    //                                          "PreviousTxnID":"612A735915B92AAD9FF3C7325A14203F2A969E82095B4727E084F4F7EA1BEEE0",
    //                                          "PreviousTxnLgrSeq":71579596}
    //                        }
    //                        ],
    //         "TransactionIndex":85,
    //         "TransactionResult":"tesSUCCESS",
    //         "delivered_amount":"1000000"
    //      },
    //  "status":"closed",
    //  "transaction":{
    //              "Account":"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45",
    //              "Amount":"1000000",
    //              "Destination":"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx",
    //              "DestinationTag":333,
    //              "Fee":"15",
    //              "Flags":2147483648,
    //              "LastLedgerSequence":71579833,
    //              "Sequence":67761419,
    //              "SigningPubKey":"03ACEAC0C382BB221C7BA96120DE3257E0F2549D12F6403D550496B849B229C79D",
    //              "TransactionType":"Payment",
    //              "TxnSignature":"30440220096386C630533A46FD1A13B9CE7A7A2F88312C6E2DCC93AA477DF291E32B7C5E02201A49F42103FF7A6B0C47F998040A5D09AB489C3E5E55C591E4FFE2D6E1571BF4",
    //              "date":705588161,
    //              "hash":"99343B8A27A974DF1FC7BB430F76E739B30A1CE8666A18A59F2AF45B8A7FEF35"
    //              },
    //  "type":"transaction",
    //  "validated":true}


    // уход
    // {"engine_result":"tesSUCCESS","engine_result_code":0,"engine_result_message":"The transaction was applied. Only final in a validated ledger.","ledger_hash":"F3FBF72FD489A7464A4B6F8E52771910A678F1EAE73C5E973AF59C4844D77D34","ledger_index":71580612,"meta":{"AffectedNodes":[{"ModifiedNode":{"FinalFields":{"Account":"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45","Balance":"2520012680","Flags":0,"OwnerCount":15,"Sequence":67761420},"LedgerEntryType":"AccountRoot","LedgerIndex":"2FFD2EE64E5ED2B1EA2A6B281ABFA02049475021B054E0E6A0AD03A470543283","PreviousFields":{"Balance":"2519012680"},                    "PreviousTxnID":"99343B8A27A974DF1FC7BB430F76E739B30A1CE8666A18A59F2AF45B8A7FEF35","PreviousTxnLgrSeq":71579825}},{"ModifiedNode":{"FinalFields":{"Account":"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx","Balance":"48979533","Flags":0,"OwnerCount":7,"Sequence":123},"LedgerEntryType":"AccountRoot","LedgerIndex":"CFF42BC480FB4AD45D47A01463BBAC6339A0C1A219F51F11FF4413C134EFD3A8","PreviousFields":{"Balance":"49979548","Sequence":122},"PreviousTxnID":"99343B8A27A974DF1FC7BB430F76E739B30A1CE8666A18A59F2AF45B8A7FEF35","PreviousTxnLgrSeq":71579825}}],"TransactionIndex":65,"TransactionResult":"tesSUCCESS","delivered_amount":"1000000"},"status":"closed","transaction":{"Account":"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx","Amount":"1000000","Destination":"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45","DestinationTag":7700,"Fee":"15","Flags":2147483648,"LastLedgerSequence":71580620,"Sequence":122,     "SigningPubKey":"0214C42799BD528C17F7B1180036F43E26879461D0CD16520140C60E6FF8D8392A","TransactionType":"Payment","TxnSignature":"3045022100EBD34156522565E566E02E89CDFCA4C773268576E136B9658BF41CB26DC27194022023BAFA3A04933459FCFD0A41B828D271381333A70513610648B3F90431CA7A07","date":705591270,"hash":"79FD34E08CA6CECE8DD251EC7A3EB6726833EEFD7421A001BA52AB9A2797DEA7"},"type":"transaction","validated":true}
    // {"engine_result":"tesSUCCESS","engine_result_code":0,"engine_result_message":"The transaction was applied. Only final in a validated ledger.","ledger_hash":"7F0484A6DE51807C6855FA0E6DCFD0FC06F35330977AD89E6ECCD65BBEAAF055","ledger_index":71579825,"meta":{"AffectedNodes":[{"ModifiedNode":{"FinalFields":{"Account":"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45","Balance":"2519012680","Flags":0,"OwnerCount":15,"Sequence":67761420},"LedgerEntryType":"AccountRoot","LedgerIndex":"2FFD2EE64E5ED2B1EA2A6B281ABFA02049475021B054E0E6A0AD03A470543283","PreviousFields":{"Balance":"2520012695","Sequence":67761419},"PreviousTxnID":"612A735915B92AAD9FF3C7325A14203F2A969E82095B4727E084F4F7EA1BEEE0","PreviousTxnLgrSeq":71579596}},{"ModifiedNode":{"FinalFields":{"Account":"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx","Balance":"49979548","Flags":0,"OwnerCount":7,"Sequence":122},"LedgerEntryType":"AccountRoot","LedgerIndex":"CFF42BC480FB4AD45D47A01463BBAC6339A0C1A219F51F11FF4413C134EFD3A8","PreviousFields":{"Balance":"48979548"},               "PreviousTxnID":"612A735915B92AAD9FF3C7325A14203F2A969E82095B4727E084F4F7EA1BEEE0","PreviousTxnLgrSeq":71579596}}],"TransactionIndex":85,"TransactionResult":"tesSUCCESS","delivered_amount":"1000000"},"status":"closed","transaction":{"Account":"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45","Amount":"1000000","Destination":"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx","DestinationTag":333, "Fee":"15","Flags":2147483648,"LastLedgerSequence":71579833,"Sequence":67761419,"SigningPubKey":"03ACEAC0C382BB221C7BA96120DE3257E0F2549D12F6403D550496B849B229C79D","TransactionType":"Payment","TxnSignature":"30440220096386C630533A46FD1A13B9CE7A7A2F88312C6E2DCC93AA477DF291E32B7C5E02201A49F42103FF7A6B0C47F998040A5D09AB489C3E5E55C591E4FFE2D6E1571BF4","date":705588161,"hash":"99343B8A27A974DF1FC7BB430F76E739B30A1CE8666A18A59F2AF45B8A7FEF35"},"type":"transaction","validated":true}
    // приход



    @Test
    public void subscribeToTheTransactionStream() throws InvalidStateException, InterruptedException {
        // Subscribe to the transaction stream (add transactions to a list as they come in).
        // Подпишитесь на поток транзакций (добавляйте транзакции в список по мере их поступления) *********************
        client.subscribe(EnumSet.of(StreamSubscriptionEnum.TRANSACTIONS), (subscription, message) -> {
            System.out.println(subscription.getMessageType());
            System.out.println(subscription.getName());
            System.out.println(message.toString());
            LOG.info("Получил сообщение от подписки {}: {}", subscription.getMessageType(), message);
            transactions.add(message.toString());
        });
        conclusionAboutPositiveResult();
    }

    @Test
    public void subscribeToTheAccountTransactionStream() throws InvalidStateException,
            URISyntaxException,
            InterruptedException {
        // Subscribe to the stream of wallet balance changes.
        // Подписаться на стрим изменения баланса кошелька *************************************************************
        client.close();
        client = new XRPLedgerClient(WSS_TEST.getValue());
        client.connectBlocking(3000, TimeUnit.MILLISECONDS);
        transactionsSize = 1;

        createPaymentManager();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accounts", List.of(paymentManager.getClassicAddress(BooleanEnum.IS_REAL.isB())));

        client.subscribe(EnumSet.of(StreamSubscriptionEnum.ACCOUNT_CHANNELS), parameters, (subscription, message) -> {
            if (message.has("engine_result")
                    && message.has("transaction")
                    && message.getJSONObject("transaction").has("DestinationTag")
                    && message.getJSONObject("transaction").getInt("DestinationTag") == TAG) {

                Assert.assertEquals(TAG, message.getJSONObject("transaction").getInt("DestinationTag"));
                Assert.assertEquals("tesSUCCESS", message.getString("engine_result"));
                Assert.assertEquals("transaction",message.getString("type"));
                Assert.assertEquals(0, message.getInt("engine_result_code"));
                Assert.assertTrue(message.getBoolean("validated"));

                Assert.assertEquals("The transaction was applied. Only final in a validated ledger.",
                        message.getString("engine_result_message"));
                Assert.assertEquals(paymentManager.getClassicAddress(BooleanEnum.IS_REAL.isB()),
                        message.getJSONObject("transaction").getString("Account"));
                Assert.assertEquals(ADDRESS_FOR_SEND_TEST.getValue(),
                        message.getJSONObject("transaction").getString("Destination"));
                Assert.assertEquals("Payment",
                        message.getJSONObject("transaction").getString("TransactionType"));

                conclusionAboutPositiveResult();
            }

            LOG.info("Получил сообщение от подписки {}: {}", subscription.getMessageType(), message);
            transactions.add(message.toString());
        });
        Thread.sleep(15000);
        makePayment();
        Thread.sleep(15000);
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
            if (transactions.size() >= transactionsSize && !client.getActiveSubscriptions().isEmpty()) {
                client.unsubscribe(client.getActiveSubscriptions());
            }
        }
    }

    private void createPaymentManager() {
        LOG.info("Создаем менеджера платежей.");
        paymentManager = new PaymentManagerXRP();
        LOG.info("Менеджер платежей создан.");
    }

    private void makePayment() {
        LOG.info("Создаем платеж.");
        paymentManager.sendPayment(BooleanEnum.IS_REAL.isB()
                        ? StringEnum.ADDRESS_FOR_SEND_REAL.getValue()
                        : StringEnum.ADDRESS_FOR_SEND_TEST.getValue(),
                TAG,
                BigDecimal.TEN,
                BooleanEnum.IS_REAL.isB());
        LOG.info("Платеж создан.");
    }

    private void conclusionAboutPositiveResult() {
        System.out.println("\n" + "\n"
                + "*********************************************" + "\n"
                + "*********************************************" + "\n"
                + "*******                               *******" + "\n"
                + "*******     TEST PASSED EXCELLENT     *******" + "\n"
                + "*******     ТЕСТ ПРОЙДЕН ОТЛИЧНО!     *******" + "\n"
                + "*******                               *******" + "\n"
                + "*********************************************" + "\n"
                + "*********************************************" + "\n"
                + "\n" + "\n");
    }
}
