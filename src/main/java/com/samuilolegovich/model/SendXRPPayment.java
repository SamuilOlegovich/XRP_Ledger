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
import org.xrpl.xrpl4j.model.client.transactions.TransactionRequestParams;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.immutables.FluentCompareTo;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

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
        System.out.println("Wallet:\n   " + "public key -> " + testWallet.publicKey() + "\n"
                + "     Classic Address -> " + testWallet.classicAddress() + "\n"
                + "     X Address -> " + testWallet.xAddress() + "\n"
                + "     Private Key -> " + testWallet.privateKey().toString() + "\n");



        /*
            Wallet:
                public key -> 03B465A41ECDD657CA26966E5B755C28D7BFCBB9B68F4BCA9424AEDAAF82CC2087
                Classic Address -> ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5
                X Address -> T7Cm1E7hmtMMkF2hMALhVyk9XzRwL5XqLf3tdoibQE2ek4j
                Private Key -> Optional[5F5166C152ED79A24337E9B88A49CA9991465CC621A684CD23D62DC0A6296A6B]
         */


        // Get the Classic address from testWallet
        // Получите классический адрес из testWallet
        Address classicAddress = testWallet.classicAddress();
        System.out.println("Classic Address:\n  " + classicAddress + "\n"); // "ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5"



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
        UnsignedInteger lastLedgerSequence =
                UnsignedInteger.valueOf(validatedLedger.plus(UnsignedLong.valueOf(4)).unsignedLongValue().intValue());
        System.out.println("Unsigned Integer:\n " + lastLedgerSequence + "\n");


        // Construct a Payment
        // Создать платеж
        Payment payment = Payment.builder()
                .account(classicAddress)
                // указываем сколько XRP хотим отправить
                .amount(XrpCurrencyAmount.ofXrp(BigDecimal.TEN))
                .destination(Address.of("ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5"))
                .sequence(sequence)
                .fee(openLedgerFee)
                .signingPublicKey(testWallet.publicKey())
                .lastLedgerSequence(lastLedgerSequence)
                .build();
        System.out.println("Constructed Payment:\n  " + payment + "\n");
        /*
            {
                "TransactionType": "Payment",
                "Account": "rEP7HYs9KZzR4Y4ppo4KkQScnwcbmxtx7r",
                "Amount": "22000000",
                "Destination": "rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe",
                "LastLedgerSequence": 22639306,
                "Flags": 0,
                "Sequence": 22639206,
                "Fee": "12"
             }
         */


        // Sign transaction -----------------------------------------------------------
        // Подписать транзакцию -------------------------------------------------------
        // Construct a SignatureService to sign the Payment
        // Создайте SignatureService для подписи платежа
        PrivateKey privateKey = PrivateKey.fromBase16EncodedPrivateKey(testWallet.privateKey().get());
        SignatureService signatureService = new SingleKeySignatureService(privateKey);
        System.out.println("Private Key:\n  " + privateKey + "\n");
        /*
            Подписанный BLOB-объект транзакции:
                12000022000000002401597266201B015972CA6140000000014FB18068400000000000000C732103FAC4276861759A5A04D3EB
                D626776CE6B4A70426FA51700E930FFD7C368A4DA274473045022100BFC390C2F12A62FDE9197D009496CE7F0FCC32C21DED26
                B290D3AAED2B773AB4022047F477A9D7F4847ABB65CBBBAD0F5FE730DDCE6F9E6F7BF1A5066A2EEB426F2F81149DD1B04693DD
                14E01D7272C8DE4E60884A9184988314F667B0CA50CC7709A220B0561B85E53A48461FA8
            Идентификационный хэш:
                228E95CABB466AE371221A6EC42190D073717F2452938FE7E988E80CE1C249DD
        */

        // Sign the Payment
        SignedTransaction<Payment> signedPayment = signatureService.sign(KeyMetadata.EMPTY, payment);
        System.out.println("Signed Payment:\n   " + signedPayment.signedTransaction() + "\n");



        // Submit transaction ---------------------------------------------------------
        // Отправить транзакцию -------------------------------------------------------
        SubmitResult<Transaction> prelimResult = xrplClient.submit(signedPayment);
        System.out.println("Submit Result Transaction:\n    " + prelimResult + "\n");
        /*
            SubmitResult{
                status=success,
                result=temREDUNDANT,
                engineResultMessage=Sends same currency to self.,
                transactionBlob=120000228000000024015971AD201B01597A7B6140000000000F424068400000000000000A732103B465A41
                                ECDD657CA26966E5B755C28D7BFCBB9B68F4BCA9424AEDAAF82CC208774473045022100FDDE9D2111CB13F6
                                83C02186DB966923B499A1CD61E61A84987E3005BF7F03EC02206FC2DC38D54E7CDA14C9DEA57882783C8DD
                                B5BF694174C99730049FE62136F288114383818B0F1FF1B17F96ECF48726F825970994D0B8314383818B0F1
                                FF1B17F96ECF48726F825970994D0B,
                transactionResult=TransactionResult{
                    transaction=Payment{
                        account=ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5,
                        fee=10, sequence=22639021,
                        lastLedgerSequence=22641275,
                        memos=[],
                        signers=[],
                        signingPublicKey=03B465A41ECDD657CA26966E5B755C28D7BFCBB9B68F4BCA9424AEDAAF82CC2087,
                        transactionSignature=3045022100FDDE9D2111CB13F683C02186DB966923B499A1CD61E61A84987E3005BF7F03EC
                                             02206FC2DC38D54E7CDA14C9DEA57882783C8DDB5BF694174C99730049FE62136F28,
                        hash=8D318C0FD2AE58D20662465148B598B50DCB27F13C9A54BEF8CC873081AF68A3,
                        flags=2147483648,
                        amount=1000000,
                        destination=ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5,
                        paths=[]
                    },
                    validated=false
                },
                accepted=true,
                accountSequenceAvailable=22639021,
                accountSequenceNext=22639021,
                applied=false,
                broadcast=false,
                kept=true,
                queued=false,
                openLedgerCost=10,
                validatedLedgerIndex=22641272
            }
        */



        // Wait for validation --------------------------------------------------------
        // Подождите подтверждения ----------------------------------------------------
        boolean transactionValidated = false;
        boolean transactionExpired = false;

        while (!transactionValidated && !transactionExpired) {
            Thread.sleep(4 * 1000);

            LedgerIndex latestValidatedLedgerIndex = xrplClient.ledger(
                    LedgerRequestParams.builder().ledgerIndex(LedgerIndex.VALIDATED).build()
            ).ledgerIndex().orElseThrow(() -> new RuntimeException("Ledger response did not contain a LedgerIndex."));

            TransactionResult<Payment> transactionResult = xrplClient.transaction(
                    TransactionRequestParams.of(signedPayment.hash()), Payment.class
            );

            if (transactionResult.validated()) {
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
