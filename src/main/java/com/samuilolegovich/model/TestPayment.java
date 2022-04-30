package com.samuilolegovich.model;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.HttpUrl;
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
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

import java.math.BigDecimal;

import static com.samuilolegovich.enums.Enums.*;

@NoArgsConstructor
public class TestPayment implements Runnable {
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
    private Wallet wallet;
    private Payment payment;
    private boolean isReal;

    private SignedTransaction<Payment> signedPayment;
    private SubmitResult<Transaction> prelimResult;

    @SneakyThrows
    @Override
    public void run() {
        isReal = TEST_OR_REAL_NET.value.equalsIgnoreCase(REAL_NET.toString());

        // Пример учетных данных ***************************************************************************************
        // Получить учетные данные
        walletFactory = DefaultWalletFactory.getInstance();
        wallet = walletFactory.fromSeed(isReal ? REAL_SEED.value : TEST_SEED.value, true);


        // Получите классический адрес из testWallet
        classicAddress = wallet.classicAddress();

        // Должен выдать адресс - rMCcNuTcajgw7YTgBy1sys3b89QqjUrMpH (test)
        System.out.println("Classic address: -->  " + classicAddress + "\n");


        // Подключение к серверу Testnet *******************************************************************************
        rippledUrl = HttpUrl.get(isReal ? REAL_NET.value : TEST_NET.value);
        xrplClient = new XrplClient(rippledUrl);

        // Выдаст - HardCodedTarget(type=JsonRpcClient, url=https://s.altnet.rippletest.net:51234/) (test)
        System.out.println("Xrpl client: -->  " + xrplClient.getJsonRpcClient() + "\n");


        // Готовим транзакцию ******************************************************************************************
        // Посмотрите информацию о вашей учетной записи
        requestParams = AccountInfoRequestParams.builder()
                .ledgerIndex(LedgerIndex.VALIDATED)
                .account(classicAddress)
                .build();

        accountInfoResult = xrplClient.accountInfo(requestParams);
        sequence = accountInfoResult.accountData().sequence();


        // Запросить текущую информацию о комиссии у rippled
        feeResult = xrplClient.fee();
        openLedgerFee = feeResult.drops().openLedgerFee();


        // Получение последнего проверенного индекса бухгалтерской книги
        validatedLedger = xrplClient.ledger(LedgerRequestParams.builder()
                .ledgerIndex(LedgerIndex.VALIDATED)
                .build())
                .ledgerIndex()
                .orElseThrow(() ->
                        new RuntimeException("LedgerIndex not available."));


        // Обходной путь для https://github.com/XRPLF/xrpl4j/issues/84
        lastLedgerSequence = UnsignedInteger.valueOf(validatedLedger.plus(UnsignedLong
                .valueOf(4))
                .unsignedLongValue()
                .intValue());


        // Создание платежа
        payment = Payment.builder()
                .account(classicAddress)
                .amount(XrpCurrencyAmount.ofXrp(BigDecimal.ONE))
                .destination(Address.of(isReal ? REAL_ADDRESS.value : TEST_ADDRESS.value))
                .sequence(sequence)
                .fee(openLedgerFee)
                .signingPublicKey(wallet.publicKey())
                .lastLedgerSequence(lastLedgerSequence)
                .build();

        System.out.println("Constructed Payment:\n" + payment + "\n");


        // Подписать транзакцию ****************************************************************************************
        // Создайте SignatureService для подписи платежа
        privateKey = PrivateKey.fromBase16EncodedPrivateKey(wallet.privateKey().get());
        System.out.println("Private key: -->  " + privateKey);

        signatureService = new SingleKeySignatureService(privateKey);

        // Подпишите платеж
        signedPayment = signatureService.sign(KeyMetadata.EMPTY, payment);
        System.out.println("Signed Payment: -->  " + signedPayment.signedTransaction());


        // Отправить транзакцию ****************************************************************************************
        prelimResult = xrplClient.submit(signedPayment);
        System.out.println("Prelim Result:  -- >  " + prelimResult);


    }
}
