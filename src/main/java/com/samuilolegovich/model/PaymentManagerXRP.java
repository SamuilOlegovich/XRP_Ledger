package com.samuilolegovich.model;

import com.samuilolegovich.model.realization.SocketXRP;
import com.samuilolegovich.model.realization.WalletXRP;

import java.math.BigDecimal;
import java.util.Map;


public final class PaymentManagerXRP {
    // REAL
    public static String POST_URL_TWO = "https://s2.ripple.com:51234/";
    public static String POST_URL = "https://s1.ripple.com:51234/";
    public static String GET_URL = "https://data.ripple.com";

    private String faucetClientHttpUrl;
    private String httpUrlConnect;

    private WalletXRP wallet;
    private SocketXRP socket;




    public PaymentManagerXRP() {
        this.faucetClientHttpUrl = null;
        this.httpUrlConnect = null;
    }




    // ------------------------------------- REAL WALLET -------------------------------------

    public void sendPayment(String address, Integer tag, BigDecimal numberOfXRP) {
        if (wallet != null) wallet.sendPaymentToAddressXRP(address, tag, numberOfXRP);
    }

    public Map<String, String> connectAnExistingWallet(String seed) {
        if (wallet == null) wallet = new WalletXRP();
        return wallet.restoreWallet(seed);
    }

    public Map<String, String> createNewWallet() {
        if (wallet == null) wallet = new WalletXRP();
        return wallet.createNewWallet();
    }

    public void updateWallet() {
        if (wallet != null) wallet.restoreWallet(wallet.getSeed());
    }

    public void startSocket() {
        if (socket == null) {
            socket = new SocketXRP();
        }
    }

    // TO DO тут подумать как лучше это сделать
    public void monitorAccountReplenishmentXRP(Object o) {}

    public void setFaucetClientHttpUrl(String faucetClientHttpUrl) {
        this.faucetClientHttpUrl = faucetClientHttpUrl;
    }

    public void setHttpUrlConnect(String httpUrlConnect) {
        this.httpUrlConnect = httpUrlConnect;
    }

    public String getClassicAddress() {
        if (wallet == null) return null;
        return wallet.getClassicAddress();
    }

    public String getPrivateKey() {
        if (wallet == null) return null;
        return wallet.getPrivateKey();
    }

    public String getXAddress() {
        if (wallet == null) return null;
        return wallet.getXAddress();
    }


    public String getPublicKey() {
        if (wallet == null) return null;
        return wallet.getPublicKey();
    }

    public String getSeed() {
        if (wallet == null) return null;
        return wallet.getSeed();
    }

    private void setterWallet() {
        if (httpUrlConnect != null)
            wallet.setXrpHttpUrl(httpUrlConnect);
    }

}
