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
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.ledger.LedgerRequestParams;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

@NoArgsConstructor
public class TestPayment implements Runnable {
    @SneakyThrows
    @Override
    public void run() {
        // Пример учетных данных --------------------------------------------------------
        // Получить учетные данные
        WalletFactory walletFactory = DefaultWalletFactory.getInstance();
        Wallet testWallet = walletFactory.fromSeed("sn3nxiW7v8KXzPzAqzyHXbSSKNuN9", true);


        // Получите классический адрес из testWallet
        Address classicAddress = testWallet.classicAddress();

        System.out.println(classicAddress);         // Должен выдать адресс - rMCcNuTcajgw7YTgBy1sys3b89QqjUrMpH


        // Подключение к серверу Testnet --------------------------------------------------------------------
        HttpUrl rippledUrl = HttpUrl.get("https://s.altnet.rippletest.net:51234/");
        XrplClient xrplClient = new XrplClient(rippledUrl);

        System.out.println(xrplClient.getJsonRpcClient());  // Выдаст - HardCodedTarget(type=JsonRpcClient, url=https://s.altnet.rippletest.net:51234/)


        // Готовим транзакцию ---------------------------------------------- ----------
        // Посмотрите информацию о вашей учетной записи
        AccountInfoRequestParams requestParams = AccountInfoRequestParams.builder()
                .ledgerIndex(LedgerIndex.VALIDATED)
                .account(classicAddress)
                .build();

        AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
        UnsignedInteger sequence = accountInfoResult.accountData().sequence();


        // Запросить текущую информацию о комиссии у rippled
        FeeResult feeResult = xrplClient.fee();
        XrpCurrencyAmount openLedgerFee = feeResult.drops().openLedgerFee();


        // Получение последнего проверенного индекса бухгалтерской книги
        LedgerIndex validatedLedger = xrplClient.ledger(LedgerRequestParams.builder()
                .ledgerIndex(LedgerIndex.VALIDATED)
                .build())
                .ledgerIndex()
                .orElseThrow(() ->
                        new RuntimeException("LedgerIndex not available."));


        // Обходной путь для https://github.com/XRPLF/xrpl4j/issues/84
        UnsignedInteger lastLedgerSequence = UnsignedInteger.valueOf(
                validatedLedger.plus(UnsignedLong.valueOf(4)).unsignedLongValue().intValue());


        // Создание платежа
        Payment payment = Payment.builder()
                .account(classicAddress)
                .amount(XrpCurrencyAmount.ofXrp(BigDecimal.ONE))
                .destination(Address.of("rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe"))
                .sequence(sequence)
                .fee(openLedgerFee)
                .signingPublicKey(testWallet.publicKey())
                .lastLedgerSequence(lastLedgerSequence)
                .build();

        System.out.println("Constructed Payment: " + payment);
        /*
            Вывод:
                Constructed Payment: Payment
                    {
                        account=rMCcNuTcajgw7YTgBy1sys3b89QqjUrMpH,
                        fee=10,
                        sequence=16449361,
                        lastLedgerSequence=20484790,
                        memos=[],
                        signers=[],
                        signingPublicKey=039543A0D3004CDA0904A09FB3710251C652D69EA338589279BC849D47A7B019A1,
                        flags=2147483648,
                        amount=1000000,
                        destination=rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe,
                        paths=[]
                     }
         */



        // Подписать транзакцию -----------------------------------------------------------
        // Создайте SignatureService для подписи платежа
        PrivateKey privateKey = PrivateKey.fromBase16EncodedPrivateKey(testWallet.privateKey().get());
        SignatureService signatureService = new SingleKeySignatureService(privateKey);
//                SingleKeySignatureService.builder()
//                .privateKey(privateKey)
//                .build();


        // Подпишите платеж
        SignedTransaction<Payment> signedPayment = signatureService.sign(KeyMetadata.EMPTY, payment);
        System.out.println("Signed Payment: " + signedPayment.signedTransaction());


        // Отправить транзакцию ---------------------------------------------------------
//        SubmitResult<Transaction> prelimResult = xrplClient.submit(signedPayment);
//        System.out.println(prelimResult);



    }
}
