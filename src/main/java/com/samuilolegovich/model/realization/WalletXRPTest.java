package com.samuilolegovich.model.realization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import com.samuilolegovich.model.PaymentManager.PaymentManagerXRPTest;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.client.faucet.FaucetClient;
import org.xrpl.xrpl4j.client.faucet.FundAccountRequest;
import org.xrpl.xrpl4j.crypto.KeyMetadata;
import org.xrpl.xrpl4j.crypto.PrivateKey;
import org.xrpl.xrpl4j.crypto.signing.SignatureService;
import org.xrpl.xrpl4j.crypto.signing.SignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.SingleKeySignatureService;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.ledger.LedgerRequestParams;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.client.transactions.TransactionRequestParams;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.immutables.FluentCompareTo;
import org.xrpl.xrpl4j.model.transactions.*;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.SeedWalletGenerationResult;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;



public class WalletXRPTest {
    private AccountInfoRequestParams accountInfoRequestParams;
    private SeedWalletGenerationResult generationResult;
    private SignedTransaction<Payment> signedPayment;
    private AccountInfoResult accountInfoResult;
    private UnsignedInteger lastLedgerSequence;
    private WalletFactory walletFactory;
    private FaucetClient faucetClient;
    private Address classicAddress;
    private XrplClient xrplClient;
    private HttpUrl rippledUrl;
    private XAddress xAddress;
    private Wallet wallet;

    private boolean paymentWasSuccessful;

    private String faucetClientHttpUrl;
    private String xrpHttpUrl;
    private String seedKey;



    public WalletXRPTest() {
        this.xrpHttpUrl = PaymentManagerXRPTest.XTP_HTTP_URL_ONE_TEST;
        this.paymentWasSuccessful = false;
        this.seedKey = null;
    }

    public WalletXRPTest(String seedKey, String xrpHttpUrl) {
        this.paymentWasSuccessful = false;
        this.xrpHttpUrl = xrpHttpUrl;
        this.seedKey = seedKey;
    }




    public void setXrpHttpUrl(String xrpHttpUrl) {
        this.xrpHttpUrl = xrpHttpUrl;
    }

    public String getSeed() {
        if (generationResult != null) return generationResult.seed();
        return "Это не новый кошелек, у вас уже есть востановительная фраза";
    }

    public String getClassicAddress() {
        return wallet.classicAddress().toString();
    }

    public String getXAddress() {
        return wallet.xAddress().toString();
    }

    public String getPrivateKey() {
        return wallet.privateKey().get();
    }

    public String getPublicKey() {
        return wallet.publicKey();
    }



    private final static String RESULT = "result";
    private final static String SUCCESS = "success";
    private final static String TES_SUCCESS = "tesSUCCESS";
    private final static String METHOD_GET_TRANSACTION = "/v2/accounts/{0}/transactions";
    private final static String METHOD_GET_BALANCE = "/v2/accounts/{0}/balances";
    private final static String METHOD_POST_SIGN = "sign";



//    public double getBalance(){
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("currency", CoinConstant.COIN_XRP);
//        String re = HttpUtil.jsonGet(getUrl + MessageFormat.format(METHOD_GET_BALANCE, address), params);
//        if(!StringUtils.isEmpty(re)){
//            JSONObject json = JSON.parseObject(re);
//            if (SUCCESS.equals(json.getString(RESULT))) {
//                JSONArray array = json.getJSONArray("balances");
//                if (array != null && array.size() > 0) {
//                    // Общий баланс
//                    double balance = array.getJSONObject(0).getDoubleValue("value");
//                    if (balance >= 20) {
//                        // Доступный баланс xrp заморозит 20 монет
//                        return BigDecimalUtil.sub(balance, 20);
//                    }
//                }
//            }
//        }
//        return 0.00;
//    }








    public Map<String, String> createNewWallet() {
        walletFactory = DefaultWalletFactory.getInstance();
        generationResult = walletFactory.randomWallet(false);
        wallet = generationResult.wallet();

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        classicAddress = wallet.classicAddress();
        replenishBalanceWallet();

        return Map.of(
                "Seed", getSeed(),
                "Public Key", getPublicKey(),
                "Private Key", getPrivateKey(),
                "Classic Address", getClassicAddress(),
                "X Address", getXAddress(),
                "Balance", accountInfoResult.accountData().balance().toString()
        );
    }



    public Map<String, String> restoreWallet(String seed) {
        seedKey = seed;
        walletFactory = DefaultWalletFactory.getInstance();
        wallet = walletFactory.fromSeed(seedKey, true);
        System.out.println("Wallet:\n   " + "public key -> " + wallet.publicKey() + "\n"
                + "     Classic Address -> " + wallet.classicAddress() + "\n"
                + "     X Address -> " + wallet.xAddress() + "\n"
                + "     Private Key -> " + wallet.privateKey().toString() + "\n");

        // Get the Classic address from wallet
        // Получите классический адрес из wallet
        classicAddress = wallet.classicAddress();
        System.out.println("Classic Address:\n  " + classicAddress + "\n");
        replenishBalanceWallet();

        return Map.of(
                "Public Key", getPublicKey(),
                "Private Key", getPrivateKey(),
                "Classic Address", getClassicAddress(),
                "X Address", getXAddress(),
                "Balance", accountInfoResult.accountData().balance().toString()
        );
    }


    // пополняем тестовый счет
    private void replenishBalanceWallet() {
        try {
            // Fund the account using the testnet Faucet
            // Пополните счет с помощью Testnet Faucet
            FaucetClient faucetClient = FaucetClient.construct(HttpUrl.get(PaymentManagerXRPTest.FAUCET_CLIENT_HTTP_URL_TEST));
            faucetClient.fundAccount(FundAccountRequest.of(wallet.classicAddress()));
            createConnect();

            // Look up your Account Info
            // Посмотрите информацию о своей учетной записи
            accountInfoRequestParams = AccountInfoRequestParams.of(classicAddress);
            accountInfoResult = xrplClient.accountInfo(accountInfoRequestParams);
            System.out.println("Account Info Result\n   " + accountInfoResult + "\n");
        } catch (JsonRpcClientErrorException e) {
            e.printStackTrace();
        }
    }

    private void createConnect() {
        try {
            // Connect --------------------------------------------------------
            // Соединять ------------------------------------------------------
            rippledUrl = HttpUrl.get(xrpHttpUrl);
            xrplClient = new XrplClient(rippledUrl);
            System.out.println("Server Info\n   " + xrplClient.serverInfo().toString() + "\n");
        } catch (JsonRpcClientErrorException e) {
            e.printStackTrace();
        }
    }



    public void sendPaymentToAddressXRP(String address, Integer tag, BigDecimal numberOfXRP) {
        try {
            // Connect --------------------------------------------------------
            // Соединять ------------------------------------------------------
            createConnect();

            // Prepare transaction --------------------------------------------------------
            // Подготовить транзакцию -----------------------------------------------------
            // Look up your Account Info
            // Посмотрите информацию о своей учетной записи
            AccountInfoRequestParams requestParams = AccountInfoRequestParams.builder()
                    .ledgerIndex(LedgerIndex.VALIDATED)
                    .account(classicAddress)
                    .build();
            // ??????????????? JsonRpcClientErrorException ???????????????
//            accountInfoResult = xrplClient.accountInfo(requestParams);
            UnsignedInteger sequence = accountInfoResult.accountData().sequence();
            System.out.println("Account Info Request Params:\n  " + requestParams.account() + "\n");
            System.out.println("Unsigned Integer:\n " + sequence.toString() + "\n");


            // Request current fee information from rippled
            // Запросить информацию о текущих сборах у rippled
            FeeResult feeResult = xrplClient.fee();
            XrpCurrencyAmount openLedgerFee = feeResult.drops().openLedgerFee();


            // Get the latest validated ledger index
            // Получите последний проверенный индекс бухгалтерской книги
            LedgerIndex validatedLedger = xrplClient.ledger(LedgerRequestParams.builder()
                    .ledgerIndex(LedgerIndex.VALIDATED)
                    .build())
                    .ledgerIndex()
                    .orElseThrow(() -> new RuntimeException("LedgerIndex not available."));
            System.out.println("Ledger Index:\n " + validatedLedger.toString() + "\n");


            // Workaround for https://github.com/XRPLF/xrpl4j/issues/84
            // Обходной путь для https://github.com/XRPLF/xrpl4j/issues/84
            lastLedgerSequence = UnsignedInteger.valueOf(validatedLedger.plus(UnsignedLong.valueOf(4))
                    .unsignedLongValue().intValue());
            System.out.println("Unsigned Integer:\n " + lastLedgerSequence + "\n");


            // Construct a Payment
            // Создать платеж
            Payment payment = Payment.builder()
                    .account(classicAddress)
                    // указываем сколько XRP хотим отправить
                    .amount(XrpCurrencyAmount.ofXrp(numberOfXRP))
                    .destination(Address.of(address))
                    .destinationTag(UnsignedInteger.valueOf(tag))
                    .sequence(sequence)
                    .fee(openLedgerFee)
                    .signingPublicKey(wallet.publicKey())
                    .lastLedgerSequence(lastLedgerSequence)
                    .build();
            System.out.println("Constructed Payment:\n  " + payment + "\n");


            // Sign transaction -----------------------------------------------------------
            // Подписать транзакцию -------------------------------------------------------
            // Construct a SignatureService to sign the Payment
            // Создайте SignatureService для подписи платежа
            PrivateKey privateKey = PrivateKey.fromBase16EncodedPrivateKey(wallet.privateKey().get());
            SignatureService signatureService = new SingleKeySignatureService(privateKey);
            System.out.println("Private Key:\n  " + privateKey + "\n");

            // Sign the Payment
            // Подпишите платеж
            signedPayment = signatureService.sign(KeyMetadata.EMPTY, payment);
            System.out.println("Signed Payment:\n   " + signedPayment.signedTransaction() + "\n");


            // Submit transaction ---------------------------------------------------------
            // Отправить транзакцию -------------------------------------------------------
            SubmitResult<Transaction> prelimResult = xrplClient.submit(signedPayment);
            System.out.println("Submit Result Transaction:\n    " + prelimResult.toString() + "\n");
            waitForValidationTransaction();

        } catch (JsonRpcClientErrorException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }




    private void waitForValidationTransaction() throws JsonRpcClientErrorException {
        // Wait for validation --------------------------------------------------------
        // Подождите подтверждения ----------------------------------------------------
        boolean transactionValidated = false;
        boolean transactionExpired = false;

        while (!transactionValidated && !transactionExpired) {
            try {
                Thread.sleep(4 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LedgerIndex latestValidatedLedgerIndex = xrplClient.ledger(LedgerRequestParams.builder()
                    .ledgerIndex(LedgerIndex.VALIDATED).build())
                    .ledgerIndex()
                    .orElseThrow(() -> new RuntimeException("Ledger response did not contain a LedgerIndex."));

            TransactionResult<Payment> transactionResult = xrplClient.transaction(
                    TransactionRequestParams.of(signedPayment.hash()), Payment.class
            );

            if (transactionResult.validated()) {
                // Платеж подтвержден кодом результата
                System.out.println("Payment was validated with result code:\n   "
                        + transactionResult.metadata().get().transactionResult() + "\n");
                transactionValidated = true;
            } else {
                boolean lastLedgerSequenceHasPassed = FluentCompareTo.is(latestValidatedLedgerIndex.unsignedLongValue())
                        .greaterThan(UnsignedLong.valueOf(lastLedgerSequence.intValue()));
                if (lastLedgerSequenceHasPassed) {
                    // LastLedgerSequence прошел. Последний ответ tx
                    System.out.println("LastLedgerSequence has passed. Last tx response:\n  " + transactionResult + "\n");
                    transactionExpired = true;
                    // Check transaction results --------------------------------------------------
                    // Проверить результаты транзакции --------------------------------------------
                    // зависает в итоге и ни к чему не приводит - надо проверить на реальном счете, может к тесту не применимо
//                    checkTransactionResults(transactionResult, signedPayment);
                } else {
                    // Платеж еще не подтвержден
                    System.out.println("Payment not yet validated.\n");
                }
            }
        }
    }



    // Check transaction results --------------------------------------------------
    // Проверить результаты транзакции --------------------------------------------
    private void checkTransactionResults(TransactionResult<Payment> transactionResult, SignedTransaction<Payment> signedPayment) {
        AtomicBoolean flag = new AtomicBoolean(true);

        while (flag.get()) {
            System.out.println("Transaction Result:\n   " + transactionResult + "\n");
            System.out.println("Explorer link:\n    https://testnet.xrpl.org/transactions/" + signedPayment.hash() + "\n");

            transactionResult.metadata().ifPresent(metadata -> {
                System.out.println("Result code:\n  " + metadata.transactionResult() + "\n");

                metadata.deliveredAmount().ifPresent(deliveredAmount ->
                        System.out.println("XRP Delivered:\n    " + ((XrpCurrencyAmount) deliveredAmount).toXrp() + "\n")
                );
                flag.set(false);
            });

            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

