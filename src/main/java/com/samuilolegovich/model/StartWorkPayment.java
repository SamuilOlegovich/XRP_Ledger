package com.samuilolegovich.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import com.samuilolegovich.model.todo.KeyPairServiceImpl;
import com.samuilolegovich.model.todo.KeyStoreTypeImpl;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.client.faucet.FaucetClient;
import org.xrpl.xrpl4j.client.faucet.FundAccountRequest;
import org.xrpl.xrpl4j.codec.binary.XrplBinaryCodec;
import org.xrpl.xrpl4j.crypto.KeyMetadata;
import org.xrpl.xrpl4j.crypto.PrivateKey;
import org.xrpl.xrpl4j.crypto.signing.SignatureService;
import org.xrpl.xrpl4j.crypto.signing.SignatureUtils;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.ledger.LedgerRequestParams;
import org.xrpl.xrpl4j.model.transactions.*;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

import java.math.BigDecimal;

@NoArgsConstructor
public class StartWorkPayment implements Runnable {

    @SneakyThrows
    @Override
    public void run() {
        // Construct a network client
        // Создайте сетевой клиент
        HttpUrl testRippledUrl = HttpUrl.get("https://s.altnet.rippletest.net:51234/");     // test net
        HttpUrl realRippledUrl = HttpUrl.get("https://s2.ripple.com:51234/");               // real net
        XrplClient xrplClient = new XrplClient(testRippledUrl);


        // Create a Wallet using a WalletFactory
        // Создайте кошелек с помощью WalletFactory
        WalletFactory walletFactory = DefaultWalletFactory.getInstance();
        Wallet testWallet = walletFactory.randomWallet(true).wallet();
        System.out.println("Test Wallet:\n" + testWallet + "\n");


        // Get the Classic and X-Addresses from testWallet
        // Получите классический и X-адрес из testWallet
        Address classicAddress = testWallet.classicAddress();
        XAddress xAddress = testWallet.xAddress();
        System.out.println("Classic Address:\n" + classicAddress + "\n");
        System.out.println("X-Address:\n" + xAddress + "\n");


        // Fund the account using the testnet Faucet
        // Пополните счет с помощью Testnet Faucet
        FaucetClient faucetClient = FaucetClient.construct(HttpUrl.get("https://faucet.altnet.rippletest.net"));
        faucetClient.fundAccount(FundAccountRequest.of(testWallet.classicAddress()));


        // Look up your Account Info
        // Посмотрите информацию о своей учетной записи
        AccountInfoRequestParams requestParams = AccountInfoRequestParams.of(classicAddress);
        AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
        System.out.println("Account Info Result\n" + accountInfoResult + "\n");
    }

    /*  Running the GetAccountInfo sample...
        Constructing an XrplClient connected to https://s.altnet.rippletest.net:51234/
        Generated a wallet with the following public key: ED015D922B5EACF09DF01168141FF27FA6229B0FAB9B4CD88D2B6DA036090EFAA4
        Classic Address: rBXHGshqXu3Smy9FUsQTmo49bGpQUQEm3X
        X-Address: T7yMiiJJCmgY2yg5WB2davUedDeBFAG5B8r9KHjKCxDdvv3
        Funded the account using the Testnet faucet.
        AccountInfoResult{
            status=success,
            accountData=AccountRootObject{
                ledgerEntryType=ACCOUNT_ROOT,
                account=rBXHGshqXu3Smy9FUsQTmo49bGpQUQEm3X,
                balance=1000000000,
                flags=0,
                ownerCount=0,
                previousTransactionId=0000000000000000000000000000000000000000000000000000000000000000,
                previousTransactionLedgerSequence=0,
                sequence=17178149,
                signerLists=[],
                index=0DC1B13C73A7F3D2D82446526D0C5D08E88F89BA442D54291117F1A08E447685
            },
            ledgerCurrentIndex=17178149,
            validated=false
        }   */




    public void runn() throws JsonRpcClientErrorException {

        // Пример учетных данных --------------------------------------------------------
        // Получить учетные данные
        WalletFactory walletFactory = DefaultWalletFactory.getInstance();
        Wallet testWallet = walletFactory.fromSeed("sn3nxiW7v8KXzPzAqzyHXbSSKNuN9", true);


        // Получите классический адрес из testWallet
        Address classicAddress = testWallet.classicAddress();

        System.out.println("Classic address:\n" + classicAddress + "\n");         // Должен выдать адресс - rMCcNuTcajgw7YTgBy1sys3b89QqjUrMpH


        // Подключение к серверу Testnet --------------------------------------------------------------------
        HttpUrl rippledUrl = HttpUrl.get("https://s.altnet.rippletest.net:51234/");
        XrplClient xrplClient = new XrplClient(rippledUrl);

        System.out.println("Xrpl client:\n" + xrplClient.getJsonRpcClient() + "\n");  // Выдаст - HardCodedTarget(type=JsonRpcClient, url=https://s.altnet.rippletest.net:51234/)


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

        System.out.println("Constructed Payment:\n" + payment + "\n");
                /*  Вывод:
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
        System.out.println("Private key:\n" + privateKey + "\n");


        // тут пока сделал заглушки для дальнейшей реализации БЫДЬ ВНИМАТЕЛЬНЫМ - заглушки лежат в пакете todo
        SignatureService signatureService = new SingleKeySignatureService(new KeyStoreTypeImpl(),
                new SignatureUtils(new ObjectMapper(), new XrplBinaryCodec()),
                new KeyPairServiceImpl());


        // Подпишите платеж
//        SignedTransaction<Payment> signedPayment = signatureService.sign(KeyMetadata.EMPTY, payment);
        System.out.println(signatureService.sign(KeyMetadata.EMPTY, payment));
//        SignedTransaction<Payment> signedPayment = signatureService.signn(KeyMetadata.EMPTY, payment);
//        System.out.println("Signed Payment: " + signedPayment.signedTransaction() + "\n");


        // Отправить транзакцию ---------------------------------------------------------
//        SubmitResult<Transaction> prelimResult = xrplClient.submit(signedPayment);
//        System.out.println(prelimResult);



    }
}
