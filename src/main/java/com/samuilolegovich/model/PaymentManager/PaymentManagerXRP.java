package com.samuilolegovich.model.PaymentManager;

import com.samuilolegovich.enums.Enums;
import com.samuilolegovich.model.realization.SocketXRP;
import com.samuilolegovich.model.realization.WalletXRP;

import java.math.BigDecimal;
import java.util.Map;

// REAL
public class PaymentManagerXRP implements PaymentManager {
    private WalletXRP wallet;
    private SocketXRP socket;



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

    public void setterWallet() {
        wallet.setXrpHttpUrl(Enums.REAL_NET.value);
    }

}
