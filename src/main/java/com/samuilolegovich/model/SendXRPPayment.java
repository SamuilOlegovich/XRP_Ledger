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

@NoArgsConstructor
public class SendXRPPayment implements Runnable {
    // Адрес: ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5
    // Секрет: ss3Mm19qHo4YLJQDdkyCqwnpMxNRU
    // Баланс: 1000 XRP



    @SneakyThrows
    @Override
    public void run() {
        // Example Credentials --------------------------------------------------------
        // Пример учетных данных ------------------------------------------------------
        WalletFactory walletFactory = DefaultWalletFactory.getInstance();
        Wallet testWallet = walletFactory.fromSeed("ss3Mm19qHo4YLJQDdkyCqwnpMxNRU", true);
        System.out.println("Wallet:\n" + "public key -> " + testWallet.publicKey() + "\n"
                + "Classic Address -> " + testWallet.classicAddress() + "\n"
                + "X Address -> " + testWallet.xAddress() + "\n"
                + "Private Key -> " + testWallet.privateKey().toString() + "\n");

        /*  Wallet:
            public key -> 03B465A41ECDD657CA26966E5B755C28D7BFCBB9B68F4BCA9424AEDAAF82CC2087
            Classic Address -> ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5
            X Address -> T7Cm1E7hmtMMkF2hMALhVyk9XzRwL5XqLf3tdoibQE2ek4j
            Private Key -> Optional[5F5166C152ED79A24337E9B88A49CA9991465CC621A684CD23D62DC0A6296A6B]
            */


        // Get the Classic address from testWallet
        // Получите классический адрес из testWallet
        Address classicAddress = testWallet.classicAddress();
        System.out.println("Classic Address:\n" + classicAddress + "\n"); // "ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5"



        // Connect --------------------------------------------------------
        // Соединять ------------------------------------------------------
        HttpUrl testRippledUrl = HttpUrl.get("https://s.altnet.rippletest.net:51234/");     // test net
        HttpUrl realRippledUrl = HttpUrl.get("https://s2.ripple.com:51234/");               // real net
        XrplClient xrplClient = new XrplClient(testRippledUrl);



        // Prepare transaction --------------------------------------------------------
        // Подготовить транзакцию -----------------------------------------------------
        // Look up your Account Info
        // Посмотрите информацию о своей учетной записи
        AccountInfoRequestParams requestParams = AccountInfoRequestParams.builder()
                .ledgerIndex(LedgerIndex.VALIDATED)
                .account(classicAddress)
                .build();
        AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
        UnsignedInteger sequence = accountInfoResult.accountData().sequence();
        System.out.println("Account Info Request Params:\n" + requestParams.account() + "\n");
        System.out.println("Unsigned Integer:\n" + sequence.toString() + "\n");


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
        System.out.println("Ledger Index:\n" + validatedLedger.toString() + "\n");


        // Workaround for https://github.com/XRPLF/xrpl4j/issues/84
        // Обходной путь для https://github.com/XRPLF/xrpl4j/issues/84
        UnsignedInteger lastLedgerSequence =
                UnsignedInteger.valueOf(validatedLedger.plus(UnsignedLong.valueOf(4)).unsignedLongValue().intValue());
        System.out.println("Unsigned Integer:\n" + lastLedgerSequence.toString() + "\n");


        // Construct a Payment
        // Создать платеж
        Payment payment = Payment.builder()
                .account(classicAddress)
                .amount(XrpCurrencyAmount.ofXrp(BigDecimal.ONE))
                .destination(Address.of("ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5"))
                .sequence(sequence)
                .fee(openLedgerFee)
                .signingPublicKey(testWallet.publicKey())
                .lastLedgerSequence(lastLedgerSequence)
                .build();
        System.out.println("Constructed Payment:\n" + payment + "\n");
        /*{
          "TransactionType": "Payment",
          "Account": "rEP7HYs9KZzR4Y4ppo4KkQScnwcbmxtx7r",
          "Amount": "22000000",
          "Destination": "rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe",
          "LastLedgerSequence": 22639306,
          "Flags": 0,
          "Sequence": 22639206,
          "Fee": "12"
        }*/


        // Sign transaction -----------------------------------------------------------
        // Подписать транзакцию -------------------------------------------------------
        // Construct a SignatureService to sign the Payment
        // Создайте SignatureService для подписи платежа
        PrivateKey privateKey = PrivateKey.fromBase16EncodedPrivateKey(testWallet.privateKey().get());
        SignatureService signatureService = new SingleKeySignatureService(privateKey);

        // Sign the Payment
        SignedTransaction<Payment> signedPayment = signatureService.sign(KeyMetadata.EMPTY, payment);
        System.out.println("Signed Payment:\n" + signedPayment.signedTransaction() + "\n");

    }
}
