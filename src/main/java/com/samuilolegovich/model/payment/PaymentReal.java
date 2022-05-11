package com.samuilolegovich.model.payment;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import com.samuilolegovich.enums.BooleanEnum;
import lombok.SneakyThrows;
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
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.SeedWalletGenerationResult;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.samuilolegovich.enums.StringEnum.*;


public class PaymentReal implements Runnable {
    private SeedWalletGenerationResult generationResult;
    private AccountInfoRequestParams requestParams;
    private AccountInfoResult accountInfoResult;
    private UnsignedInteger lastLedgerSequence;
    private SignatureService signatureService;
    private XrpCurrencyAmount openLedgerFee;
    private WalletFactory walletFactory;
    private LedgerIndex validatedLedger;
    private UnsignedInteger sequence;
    private Address classicAddress;
    private PrivateKey privateKey;
    private XrplClient xrplClient;
    private FeeResult feeResult;
    private HttpUrl rippledUrl;
    private org.xrpl.xrpl4j.model.transactions.Payment payment;
    private Wallet wallet;

    private boolean isWallet;

    private SignedTransaction<org.xrpl.xrpl4j.model.transactions.Payment> signedPayment;
    private SubmitResult<Transaction> prelimResult;

    @SneakyThrows
    @Override
    public void run() {
        // Determine if we have a seed phrase for the wallet or is it worth generating a new one
        // Определяем есть ли у нас сид фраза для кошелька или стоит сгенирировать новый
        isWallet = BooleanEnum.IS_WALLET.isB();


        // Credential example
        // Get credentials
        // Пример учетных данных ***************************************************************************************
        // Получить учетные данные
        walletFactory = DefaultWalletFactory.getInstance();

        // If the wallet does not exist, then we generate a new one, if there is, then we restore it from the seed phrase
        // Если кошелька не существует то генерируем новый, если есть то востанавливаем его из сид фразы
        if (isWallet) {
            generationResult = walletFactory.randomWallet(false);
            wallet = generationResult.wallet();
            System.out.println(wallet.classicAddress()); // Example: rGCkuB7PBr5tNy68tPEABEtcdno4hE6Y7f
            System.out.println(generationResult.seed()); // Example: sp6JS7f14BuwFY8Mw6bTtLKWauoUs
        } else {
            wallet = walletFactory.fromSeed(SEED_REAL.getValue(), true);
        }


        // Get classic address from wallet
        // Получите классический адрес из wallet
        classicAddress = wallet.classicAddress();

        // Must give an address example -> rMCcNuTcajgw7YTgBy1sys3b89QqjUrMpH
        // Должен выдать адресс -> rMCcNuTcajgw7YTgBy1sys3b89QqjUrMpH
        System.out.println("Classic address: -->  " + classicAddress + "\n");


        // Server connection
        // Подключение к серверу ***************************************************************************************
        rippledUrl = HttpUrl.get(NET_REAL_POST_URL_ONE.getValue());
        xrplClient = new XrplClient(rippledUrl);

        // Will issue
        // Выдаст - HardCodedTarget(type=JsonRpcClient, url=https://s2.ripple.com:51234/)
        System.out.println("Xrpl client: -->  " + xrplClient.getJsonRpcClient() + "\n");


        // Preparing a transaction
        // View your account information
        // Готовим транзакцию ******************************************************************************************
        // Посмотрите информацию о вашей учетной записи
        requestParams = AccountInfoRequestParams.builder()
                .ledgerIndex(LedgerIndex.VALIDATED)
                .account(classicAddress)
                .build();

        accountInfoResult = xrplClient.accountInfo(requestParams);
        sequence = accountInfoResult.accountData().sequence();


        // Request current commission information from rippled
        // Запросить текущую информацию о комиссии у rippled
        feeResult = xrplClient.fee();
        openLedgerFee = feeResult.drops().openLedgerFee();


        // Getting the last audited ledger index
        // Получение последнего проверенного индекса бухгалтерской книги
        validatedLedger = xrplClient.ledger(LedgerRequestParams.builder()
                .ledgerIndex(LedgerIndex.VALIDATED)
                .build())
                .ledgerIndex()
                .orElseThrow(() ->
                        new RuntimeException("LedgerIndex not available."));


        // Workaround for
        // Обходной путь для https://github.com/XRPLF/xrpl4j/issues/84
        lastLedgerSequence = UnsignedInteger.valueOf(validatedLedger.plus(UnsignedLong
                .valueOf(4))
                .unsignedLongValue()
                .intValue());


        // Creating a payment
        // Создание платежа
        payment = org.xrpl.xrpl4j.model.transactions.Payment.builder()
                .account(classicAddress)
                .amount(XrpCurrencyAmount.ofXrp(BigDecimal.ONE))
                .destination(Address.of(ADDRESS_FOR_SEND_REAL.getValue()))
                .sequence(sequence)
                .fee(openLedgerFee)
                .signingPublicKey(wallet.publicKey())
                .lastLedgerSequence(lastLedgerSequence)
                .build();

        System.out.println("Constructed Payment:  -- >  " + payment + "\n");


        // Sign transaction
        // Create a SignatureService to sign the payment
        // Подписать транзакцию ****************************************************************************************
        // Создайте SignatureService для подписи платежа
        privateKey = PrivateKey.fromBase16EncodedPrivateKey(wallet.privateKey().get());
        System.out.println("Private key: -->  " + privateKey);

        signatureService = new SingleKeySignatureService(privateKey);

        // Sign payment
        // Подпишите платеж
        signedPayment = signatureService.sign(KeyMetadata.EMPTY, payment);
        System.out.println("Signed Payment: -->  " + signedPayment.signedTransaction());


        // Send transaction
        // Отправить транзакцию ****************************************************************************************
        prelimResult = xrplClient.submit(signedPayment);
        System.out.println("Prelim Result:  -- >  " + prelimResult);

        // Подождать подтверждения транзакции **************************************************************************
//        waitForTransactionConfirmation();
    }


    // ******************* ПЕРЕСМОТРЕТЬ И ПЕРЕОСМЫСЛИТЬ ЭТОТ КОД *******************************************************
    private boolean waitForTransactionConfirmation() throws InterruptedException, JsonRpcClientErrorException {
        // Wait for validation
        // Подождите подтверждения *************************************************************************************
        boolean transactionValidated = false;
        boolean transactionExpired = false;

        while (!transactionValidated && !transactionExpired) {
            Thread.sleep(4 * 1000);

            LedgerIndex latestValidatedLedgerIndex = xrplClient.ledger(LedgerRequestParams.builder()
                    .ledgerIndex(LedgerIndex.VALIDATED).build())
                    .ledgerIndex().orElseThrow(() ->
                            new RuntimeException("Ledger response did not contain a LedgerIndex."));

            TransactionResult<Payment> transactionResult =
                    xrplClient.transaction(TransactionRequestParams.of(signedPayment.hash()), Payment.class);

            if (transactionResult.validated()) {
                // Payment confirmed by result code
                // Платеж подтвержден кодом результата
                System.out.println("Payment was validated with result code:\n   "
                        + transactionResult.metadata().get().transactionResult() + "\n");
                transactionValidated = true;
            } else {
                boolean lastLedgerSequenceHasPassed = FluentCompareTo.
                        is(latestValidatedLedgerIndex.unsignedLongValue())
                        .greaterThan(UnsignedLong.valueOf(lastLedgerSequence.intValue()));
                if (lastLedgerSequenceHasPassed) {
                    // LastLedgerSequence прошел. Последний ответ tx
                    System.out.println("LastLedgerSequence has passed. Last tx response:\n  " + transactionResult + "\n");
                    transactionExpired = true;
                    // Check transaction results --------------------------------------------------
                    // Проверить результаты транзакции --------------------------------------------
                    checkTransactionResults(transactionResult, signedPayment);
                } else {
                    // Платеж еще не подтвержден
                    System.out.println("Payment not yet validated.\n");
                }
            }
        }
        return true;
    }

    // Check transaction results
    // Проверить результаты транзакции *********************************************************************************
    private void checkTransactionResults(TransactionResult<Payment> transactionResult,
                                         SignedTransaction<Payment> signedPayment) {

        AtomicBoolean flag = new AtomicBoolean(true);

        while (flag.get()) {
            System.out.println("Transaction Result:  -- >  " + transactionResult + "\n");
            System.out.println("Explorer link:  -- >  https://testnet.xrpl.org/transactions/"
                    + signedPayment.hash() + "\n");

            transactionResult.metadata().ifPresent(metadata -> {
                System.out.println("Result code:  -- >  "
                        + metadata.transactionResult() + "\n");

                metadata.deliveredAmount().ifPresent(deliveredAmount ->
                        System.out.println("XRP Delivered:  -- >  "
                                + ((XrpCurrencyAmount) deliveredAmount).toXrp() + "\n")
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
