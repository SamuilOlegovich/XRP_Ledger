package com.samuilolegovich.model;

import com.samuilolegovich.model.realization.TestSocketXRP;
import com.samuilolegovich.model.realization.TestWalletXRP;

import java.math.BigDecimal;
import java.util.Map;



public final class TestPaymentManagerXRP {
    // TEST
    public static String FAUCET_CLIENT_HTTP_URL_TEST = "https://faucet.altnet.rippletest.net";
    public static String XTP_HTTP_URL_ONE_TEST = "https://s.altnet.rippletest.net:51234/";

    private String faucetClientHttpUrl;
    private String httpUrlConnect;

    private TestWalletXRP wallet;
    private TestSocketXRP socket;



    public TestPaymentManagerXRP() {
        this.faucetClientHttpUrl = null;
        this.httpUrlConnect = null;
    }



    // ------------------------------------- TEST WALLET -------------------------------------

    public void sendTestPayment(String address, Integer tag, BigDecimal numberOfXRP) {
        if (wallet != null) wallet.sendPaymentToAddressXRP(address, tag, numberOfXRP);
    }

    public Map<String, String> connectAnExistingTestWallet(String seed) {
        if (wallet == null) wallet = new TestWalletXRP();
        return wallet.restoreWallet(seed);
    }

    public Map<String, String> createNewTestWallet() {
        if (wallet == null) wallet = new TestWalletXRP();
        return wallet.createNewWallet();
    }

    public void updateTestWallet() {
        if (wallet != null) wallet.restoreWallet(wallet.getSeed());
    }

    public void startTestSocket(){
        socket = new TestSocketXRP();
    }

    // тут подумать как лучше это сделать
    public void monitorTestAccountReplenishmentXRP(Object o){}

    public void setFaucetClientHttpUrl(String faucetClientHttpUrl) {
        this.faucetClientHttpUrl = faucetClientHttpUrl;
    }

    public void setHttpUrlConnect(String httpUrlConnect) {
        this.httpUrlConnect = httpUrlConnect;
    }

    public String getTestClassicAddress() {
        if (wallet == null) return null;
        return wallet.getClassicAddress();
    }

    public String getTestPrivateKey() {
        if (wallet == null) return null;
        return wallet.getPrivateKey();
    }

    public String getTestXAddress() {
        if (wallet == null) return null;
        return wallet.getXAddress();
    }


    public String getTestPublicKey() {
        if (wallet == null) return null;
        return wallet.getPublicKey();
    }

    public String getTestSeed() {
        if (wallet == null) return null;
        return wallet.getSeed();
    }

    private void setterTestWallet() {
//        if (testFaucetClientHttpUrl != null)
//            testWallet.setFaucetClientHttpUrl(testFaucetClientHttpUrl);
        if (httpUrlConnect != null)
            wallet.setXrpHttpUrl(httpUrlConnect);
    }

}
