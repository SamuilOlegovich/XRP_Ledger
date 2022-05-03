package com.samuilolegovich.model.PaymentManager;

import com.samuilolegovich.enums.EnumStr;
import com.samuilolegovich.model.sockets.SocketXRP;
import com.samuilolegovich.model.wallets.WalletXRP;
import com.samuilolegovich.model.wallets.WalletXRPTest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

// REAL
public class PaymentManagerXRP implements PaymentManager {
    private WalletXRPTest walletTest;
    private WalletXRP wallet;
    private SocketXRP socket;

    public PaymentManagerXRP() {
        this.walletTest = new WalletXRPTest();
        this.wallet = new WalletXRP();
        this.socket = new SocketXRP();
    }



    @Override
    public void sendPayment(String address, Integer tag, BigDecimal numberOfXRP, boolean isReal) {
        if (isReal) { wallet.sendPaymentToAddressXRP(address, tag, numberOfXRP);
        } else { walletTest.sendPaymentToAddressXRP(address, tag, numberOfXRP); }
    }

    @Override
    public Map<String, String> connectAnExistingWallet(String seed, boolean isReal) {
        if (isReal) {
            EnumStr.setValue(EnumStr.SEED_REAL, seed);
            return wallet.restoreWallet();
        }
        EnumStr.setValue(EnumStr.SEED_TEST, seed);
        return walletTest.restoreWallet();
    }

    @Override
    public Map<String, String> createNewWallet(boolean isReal) {
        if (isReal) { return wallet.createNewWallet(); }
        return walletTest.createNewWallet();
    }

    @Override
    public void updateWallet(boolean isReal) {
        if (isReal) { wallet.restoreWallet();
        } else { walletTest.restoreWallet(); }
    }

    @Override
    public void monitorAccountReplenishmentXRP(Object o, boolean isReal) {
        // TO DO тут подумать как лучше это сделать
    }

    @Override
    public void setterWallet(boolean isReal) {

    }

    @Override
    public String getClassicAddress(boolean isReal) {
        if (isReal) { return wallet.classicAddress().toString(); }
        return walletTest.classicAddress().toString();
    }

    @Override
    public String getPrivateKey(boolean isReal) {
        if (isReal) { return wallet.privateKey().get(); }
        return walletTest.privateKey().get();
    }

    @Override
    public String getXAddress(boolean isReal) {
        if (isReal) { return wallet.xAddress().toString(); }
        return walletTest.xAddress().toString();
    }

    @Override
    public String getPublicKey(boolean isReal) {
        if (isReal) { return wallet.publicKey(); }
        return walletTest.publicKey();
    }

    @Override
    public String getSeed(boolean isReal) {
        if (isReal) { return wallet.getSeed(); }
        return walletTest.getSeed();
    }

    @Override
    public boolean isTest(boolean isReal) {
        if (isReal) { return wallet.isTest(); }
        return walletTest.isTest();
    }

    @Override
    public String getBalance(boolean isReal) {
        if (isReal) { return wallet.getBalance(); }
        return walletTest.getBalance();
    }

}
