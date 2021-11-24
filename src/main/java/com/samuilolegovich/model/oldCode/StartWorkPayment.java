package com.samuilolegovich.model.oldCode;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.client.faucet.FaucetClient;
import org.xrpl.xrpl4j.client.faucet.FundAccountRequest;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.transactions.*;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;



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
        System.out.println("Test Wallet:\n  " + testWallet + "\n");


        // Get the Classic and X-Addresses from testWallet
        // Получите классический и X-адрес из testWallet
        Address classicAddress = testWallet.classicAddress();
        XAddress xAddress = testWallet.xAddress();
        System.out.println("Classic Address:\n  " + classicAddress + "\n");
        System.out.println("X-Address:\n    " + xAddress + "\n");


        // Fund the account using the testnet Faucet
        // Пополните счет с помощью Testnet Faucet
        FaucetClient faucetClient = FaucetClient.construct(HttpUrl.get("https://faucet.altnet.rippletest.net"));
        faucetClient.fundAccount(FundAccountRequest.of(testWallet.classicAddress()));


        // Look up your Account Info
        // Посмотрите информацию о своей учетной записи
        AccountInfoRequestParams requestParams = AccountInfoRequestParams.of(classicAddress);
        AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
        System.out.println("Account Info Result\n   " + accountInfoResult + "\n");
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
}
