package com.samuilolegovich.model.wallets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import com.samuilolegovich.enums.EnumStr;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.XrplClient;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.samuilolegovich.enums.EnumBoo.IS_WALLET;


public class WalletXRP implements Wallet, MyWallets {
    private SeedWalletGenerationResult generationResult;
    private AccountInfoRequestParams requestParams;
    private AccountInfoResult accountInfoResult;
    private UnsignedInteger lastLedgerSequence;
    private WalletFactory walletFactory;
    private UnsignedInteger sequence;
    private Address classicAddress;
    private XrplClient xrplClient;
    private HttpUrl rippledUrl;
    private Wallet wallet;

    private SignedTransaction<Payment> signedPayment;

    private Map<String, String> createNewWalletData;

    private boolean paymentWasSuccessful = false;


    public WalletXRP() {
        if (IS_WALLET.b) { restoreWallet();
        } else {
            this.createNewWalletData = createNewWallet();
        }
    }


    @Override
    public Optional<String> privateKey() {
        return Optional.of(wallet.privateKey().get());
    }

    @Override
    public String publicKey() {
        return wallet.publicKey();
    }

    @Override
    public Address classicAddress() {
        return wallet.classicAddress();
    }

    @Override
    public XAddress xAddress() {
        return wallet.xAddress();
    }

    @Override
    public boolean isTest() {
        return false;
    }

    @Override
    public BigDecimal getBalance() {
        getInformationAboutYourAccount();
        return accountInfoResult != null
                ? accountInfoResult.accountData().balance().toXrp()
                : BigDecimal.ZERO;
    }

    @Override
    public String getSeed() {
        if (createNewWalletData != null) {
            return generationResult.seed();
        }
        return "Это не новый кошелек, у вас уже есть востановительная фраза";
    }


    @Override
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
        EnumStr.setValue(EnumStr.SEED_REAL, getSeed());
        createConnect();
        getInformationAboutYourAccount();

        createNewWalletData = Map.of(
                "Seed", EnumStr.SEED_REAL.value,
                "Public Key", publicKey(),
                "Private Key", privateKey().get(),
                "Classic Address", classicAddress().toString(),
                "X Address", xAddress().toString(),
                "Balance", accountInfoResult != null
                        ? accountInfoResult.accountData().balance().toString()
                        : "0000000"
        );

        return new HashMap<>(createNewWalletData);
    }


    public Map<String, String> restoreWallet() {
        walletFactory = DefaultWalletFactory.getInstance();
        wallet = walletFactory.fromSeed(EnumStr.SEED_REAL.value, true);

        // Get the Classic address from wallet
        // Получите классический адрес из wallet
        classicAddress = wallet.classicAddress();
        createConnect();
        getInformationAboutYourAccount();

        return Map.of(
                "Public Key", publicKey(),
                "Private Key", privateKey().get(),
                "Classic Address", classicAddress().toString(),
                "X Address", xAddress().toString(),
                "Balance", accountInfoResult != null
                        ? accountInfoResult.accountData().balance().toString()
                        : "0000000"
        );
    }


    // ТУТ СДЕЛАТЬ ТАК ЧТОБЫ ПРОСТОЕ ЧИСЛО С ЗАПЯТОЙ ПЕРЕРАБАТЫВАЛОСЬ В БИГ ДЕЦИМИАЛ************************************
    public void sendPaymentToAddressXRP(String address, Integer tag, BigDecimal numberOfXRP) {
        try {
            createConnect();
            getInformationAboutYourAccount();

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
            System.out.println("Ledger Index:  -- >  " + validatedLedger.toString());


            // Workaround for https://github.com/XRPLF/xrpl4j/issues/84
            // Обходной путь для https://github.com/XRPLF/xrpl4j/issues/84
            lastLedgerSequence = UnsignedInteger.valueOf(validatedLedger.plus(UnsignedLong.valueOf(4))
                    .unsignedLongValue().intValue());
            System.out.println("Unsigned Integer:  -- >  " + lastLedgerSequence);


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
            System.out.println("Constructed Payment:  -- >  " + payment);


            // Sign transaction -----------------------------------------------------------
            // Подписать транзакцию -------------------------------------------------------
            // Construct a SignatureService to sign the Payment
            // Создайте SignatureService для подписи платежа
            PrivateKey privateKey = PrivateKey.fromBase16EncodedPrivateKey(wallet.privateKey().get());
            SignatureService signatureService = new SingleKeySignatureService(privateKey);
            System.out.println("Private Key:  -- >  " + privateKey);

            // Sign the Payment
            signedPayment = signatureService.sign(KeyMetadata.EMPTY, payment);
            System.out.println("Signed Payment:  -- >  " + signedPayment.signedTransaction());


            // Submit transaction ---------------------------------------------------------
            // Отправить транзакцию -------------------------------------------------------
            SubmitResult<Transaction> prelimResult = xrplClient.submit(signedPayment);
            System.out.println("Submit Result Transaction:  -- >  " + prelimResult);
            waitForValidationTransaction();
        } catch (JsonRpcClientErrorException | JsonProcessingException e) {
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
                System.out.println("Payment was validated with result code:  -- >  "
                        + transactionResult.metadata().get().transactionResult());
                transactionValidated = true;
            } else {
                boolean lastLedgerSequenceHasPassed = FluentCompareTo.is(latestValidatedLedgerIndex.unsignedLongValue())
                        .greaterThan(UnsignedLong.valueOf(lastLedgerSequence.intValue()));
                if (lastLedgerSequenceHasPassed) {
                    // LastLedgerSequence прошел. Последний ответ tx
                    System.out.println("LastLedgerSequence has passed. Last tx response:  -- >  " + transactionResult);
                    transactionExpired = true;
                    // Check transaction results --------------------------------------------------
                    // Проверить результаты транзакции --------------------------------------------
                    checkTransactionResults(transactionResult, signedPayment);
                } else {
                    // Платеж еще не подтвержден
                    System.out.println("Payment not yet validated.");
                }
            }
        }
    }


    // Check transaction results --------------------------------------------------
    // Проверить результаты транзакции --------------------------------------------
    private void checkTransactionResults(TransactionResult<Payment> transactionResult, SignedTransaction<Payment> signedPayment) {
        AtomicBoolean flag = new AtomicBoolean(true);
        while (flag.get()) {
            System.out.println("Transaction Result:  -- >  " + transactionResult);
            System.out.println("Explorer link:  -- >  https://testnet.xrpl.org/transactions/" + signedPayment.hash());

            transactionResult.metadata().ifPresent(metadata -> {
                System.out.println("Result code:  -- >  " + metadata.transactionResult());
                metadata.deliveredAmount().ifPresent(deliveredAmount ->
                        System.out.println("XRP Delivered:  -- >  " + ((XrpCurrencyAmount) deliveredAmount).toXrp()));
                flag.set(false);
            });

            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createConnect() {
        // Connect --------------------------------------------------------
        // Соединять ------------------------------------------------------
        rippledUrl = HttpUrl.get(EnumStr.POST_URL_ONE.value);
        xrplClient = new XrplClient(rippledUrl);
    }

    private void getInformationAboutYourAccount() {
        try {
            // Prepare transaction --------------------------------------------------------
            // Подготовить транзакцию -----------------------------------------------------
            // Look up your Account Info
            // Посмотрите информацию о своей учетной записи
            requestParams = AccountInfoRequestParams.builder()
                    .ledgerIndex(LedgerIndex.VALIDATED)
                    .account(classicAddress)
                    .build();
            accountInfoResult = xrplClient.accountInfo(requestParams);

            sequence = accountInfoResult.accountData().sequence();
            System.out.println("Account Info Request Params:  -- >  " + requestParams.account());
            System.out.println("Unsigned Integer:  -- >  " + sequence.toString());
        } catch (JsonRpcClientErrorException e) {
            System.out.println("Для начала стоило бы активировать счет - пополнить.");
        }
    }
}