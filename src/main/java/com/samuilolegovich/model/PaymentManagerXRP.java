package com.samuilolegovich.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.samuilolegovich.model.realization.SocketXRP;
import com.samuilolegovich.model.realization.TestSocketXRP;
import com.samuilolegovich.model.realization.TestWalletXRP;
import com.samuilolegovich.model.realization.WalletXRP;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;

import java.math.BigDecimal;


public final class PaymentManagerXRP {
    private String testFaucetClientHttpUrl;
    private String testHttpUrlConnect;

    private TestWalletXRP testWallet;
    private TestSocketXRP testSocket;

    private String faucetClientHttpUrl;
    private String httpUrlConnect;

    private WalletXRP wallet;
    private SocketXRP socket;




    public PaymentManagerXRP() {
        this.testFaucetClientHttpUrl = null;
        this.faucetClientHttpUrl = null;
        this.testHttpUrlConnect = null;
        this.httpUrlConnect = null;
    }

    public void sendPayment(String address, String tag, String numberOfXRP) {}
    public void connectAnExistingWallet(String seed) {}
    public void createNewWallet() {}
    public void updateWallet() {}
    public void startSocket() {}

    // TO DO тут подумать как лучше это сделать
    public void monitorAccountReplenishmentXRP(Object o){}


    public void setFaucetClientHttpUrl(String faucetClientHttpUrl) {
        this.faucetClientHttpUrl = faucetClientHttpUrl;
    }

    public void setHttpUrlConnect(String httpUrlConnect) {
        this.httpUrlConnect = httpUrlConnect;
    }

    public String getPublicAddress() {
        return wallet.getPublicAddress();
    }

    public String getPrivateSeed() {
        return wallet.getPrivateKey();
    }

    private void setterWalletXRP() {
        if (faucetClientHttpUrl != null)
            wallet.setFaucetClientHttpUrl(faucetClientHttpUrl);
        if (testHttpUrlConnect != null)
            wallet.setXrpHttpUrl(httpUrlConnect);
    }



    // ------------------------------------- TEST WALLET -------------------------------------

    public void sendTestPayment(String address, Integer tag, BigDecimal numberOfXRP) {
        if (testWallet == null) {
            testWallet = new TestWalletXRP();
            setterTestWallet();
            testWallet.init();
        }
        testWallet.sendPaymentToAddressXRP(address, tag, numberOfXRP);
    }

    public void connectAnExistingTestWallet(String seed) {
        if (testWallet == null) {
            testWallet = new TestWalletXRP();
            setterTestWallet();
        }
        testWallet.setSeedKey(seed);
        testWallet.init();
    }

    public void createNewTestWallet() {
        if (testWallet == null) {
            testWallet = new TestWalletXRP();
            setterTestWallet();
            testWallet.init();
        }
    }

    public void updateTestWallet() {
        if (testWallet == null) {
            createNewTestWallet();
        } else {
            setterTestWallet();
            testWallet.init();
        }
    }

    public void startTestSocket(){}

    // тут подумать как лучше это сделать
    public void monitorTestAccountReplenishmentXRP(Object o){}

    public void setTestFaucetClientHttpUrl(String testFaucetClientHttpUrl) {
        this.testFaucetClientHttpUrl = testFaucetClientHttpUrl;
    }

    public void setTestHttpUrlConnect(String testHttpUrlConnect) {
        this.testHttpUrlConnect = testHttpUrlConnect;
    }

    public String getTestPublicAddress() {
        return testWallet.getPublicAddress();
    }

    public String getTestPrivateSeed() {
        return testWallet.getPrivateKey();
    }

    private void setterTestWallet() {
        if (testFaucetClientHttpUrl != null)
            testWallet.setFaucetClientHttpUrl(testFaucetClientHttpUrl);
        if (testHttpUrlConnect != null)
            testWallet.setXrpHttpUrl(testHttpUrlConnect);
    }
}
