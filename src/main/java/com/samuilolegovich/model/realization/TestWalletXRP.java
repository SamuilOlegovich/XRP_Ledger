package com.samuilolegovich.model.realization;

import lombok.Builder;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.client.faucet.FaucetClient;
import org.xrpl.xrpl4j.client.faucet.FundAccountRequest;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.XAddress;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;


@Builder
public class TestWalletXRP {
    private AccountInfoRequestParams accountInfoRequestParams;
    private AccountInfoResult accountInfoResult;
    private WalletFactory walletFactory;
    private FaucetClient faucetClient;
    private Address classicAddress;
    private HttpUrl testRippledUrl;
    private XrplClient xrplClient;
    private Wallet testWallet;
    private XAddress xAddress;


    private String faucetClientHttpUrl = "https://s.altnet.rippletest.net:51234/";
    private String xrpHttpUrl = "https://s.altnet.rippletest.net:51234/";
    private String seedKey;



    public TestWalletXRP() throws JsonRpcClientErrorException {
        this.faucetClientHttpUrl = "https://s.altnet.rippletest.net:51234/";
        this.xrpHttpUrl = "https://s.altnet.rippletest.net:51234/";
        this.seedKey = null;
        initTestWalletXRP();
    }

    public TestWalletXRP(String seedKey, String xrpHttpUrl, String faucetClientHttpUrl) throws JsonRpcClientErrorException {
        this.faucetClientHttpUrl = faucetClientHttpUrl;
        this.xrpHttpUrl = xrpHttpUrl;
        this.seedKey = seedKey;
        initTestWalletXRP();
    }



    private void initTestWalletXRP() throws JsonRpcClientErrorException {
        // Construct a network client
        // Создайте сетевой клиент
        testRippledUrl = HttpUrl.get(xrpHttpUrl);     // test net
        xrplClient = new XrplClient(testRippledUrl);


        // Create a Wallet using a WalletFactory
        // Создайте кошелек с помощью WalletFactory
        walletFactory = DefaultWalletFactory.getInstance();
        if (seedKey == null) {
            testWallet = walletFactory.randomWallet(true).wallet();
        } else {
            testWallet = walletFactory.fromSeed(seedKey, true);
        }
        System.out.println("Test Wallet:\n  " + testWallet + "\n");


        // Get the Classic and X-Addresses from testWallet
        // Получите классический и X-адрес из testWallet
        classicAddress = testWallet.classicAddress();
        xAddress = testWallet.xAddress();
        System.out.println("Classic Address:\n  " + classicAddress + "\n");
        System.out.println("X-Address:\n    " + xAddress + "\n");


        // Fund the account using the testnet Faucet
        // Пополните счет с помощью Testnet Faucet
        faucetClient = FaucetClient.construct(HttpUrl.get(faucetClientHttpUrl));
        faucetClient.fundAccount(FundAccountRequest.of(testWallet.classicAddress()));


        // Look up your Account Info
        // Посмотрите информацию о своей учетной записи
        accountInfoRequestParams = AccountInfoRequestParams.of(classicAddress);
        accountInfoResult = xrplClient.accountInfo(accountInfoRequestParams);
        System.out.println("Account Info Result\n   " + accountInfoResult + "\n");
    }
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

