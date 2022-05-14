package com.samuilolegovich.model.PaymentManager;

import com.samuilolegovich.enums.BooleanEnum;
import com.samuilolegovich.enums.StringEnum;
import com.samuilolegovich.model.PaymentManager.interfaces.PaymentManager;
import com.samuilolegovich.model.PaymentManager.interfaces.Presets;
import com.samuilolegovich.model.PaymentManager.interfaces.SocketManager;
import com.samuilolegovich.model.sockets.SocketXRP;
import com.samuilolegovich.model.sockets.enums.StreamSubscriptionEnum;
import com.samuilolegovich.model.sockets.exceptions.InvalidStateException;
import com.samuilolegovich.model.sockets.interfaces.CommandListener;
import com.samuilolegovich.model.sockets.interfaces.StreamSubscriber;
import com.samuilolegovich.model.wallets.WalletXRP;
import com.samuilolegovich.model.wallets.WalletXRPTest;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Map;

public class PaymentAndSocketManagerXRPL implements PaymentManager, SocketManager, Presets {
    private final WalletXRPTest walletTest;

    private SocketXRP socket;
    private WalletXRP wallet;


    public PaymentAndSocketManagerXRPL() {
        this.walletTest = new WalletXRPTest();
        this.socket = createNewSocket();
        this.wallet = new WalletXRP();
    }

    private SocketXRP createNewSocket() {
        try {
            return new SocketXRP(BooleanEnum.IS_REAL.isB()
                    ? StringEnum.WSS_REAL.getValue()
                    : StringEnum.WSS_TEST.getValue());
        } catch (URISyntaxException e) { e.printStackTrace(); }
        return null;
    }



    // Payment *********************************************************************************************************

    @Override
    public void sendPayment(String address, Integer tag, BigDecimal numberOfXRP, boolean isReal) {
        if (isReal) { wallet.sendPaymentToAddressXRP(address, tag, numberOfXRP);
        } else { walletTest.sendPaymentToAddressXRP(address, tag, numberOfXRP); }
    }

    @Override
    public Map<String, String> connectAnExistingWallet(String seed, boolean isReal) {
        if (isReal) {
            StringEnum.setValue(StringEnum.SEED_REAL, seed);
            return wallet.restoreWallet();
        }
        StringEnum.setValue(StringEnum.SEED_TEST, seed);
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
    public BigDecimal getBalance(boolean isReal) {
        BigDecimal allBalance = getAllBalance(isReal);
        BigDecimal activationPayment = new BigDecimal(StringEnum.ACTIVATION_PAYMENT.getValue());
        int compareTo = allBalance.compareTo(activationPayment);
        if (compareTo <= 0) { return new BigDecimal("0.000000"); }
        return allBalance.subtract(activationPayment);
    }

    @Override
    public BigDecimal getAllBalance(boolean isReal) {
        if (isReal) { return wallet.getBalance(); }
        return walletTest.getBalance();
    }



    // Socket **********************************************************************************************************

    @Override
    public String sendCommand(String command, CommandListener listener) throws InvalidStateException {
        return socket.sendCommand(command, null, listener);
    }

    @Override
    public String sendCommand(String command, Map<String, Object> parameters, CommandListener listener)
            throws InvalidStateException {
        return socket.sendCommand(command, parameters, listener);
    }

    @Override
    public void subscribe(EnumSet<StreamSubscriptionEnum> streams, StreamSubscriber subscriber)
            throws InvalidStateException {
        socket.subscribe(streams, subscriber);
    }

    @Override
    public void subscribe(EnumSet<StreamSubscriptionEnum> streams, Map<String, Object> parameters,
                          StreamSubscriber subscriber) throws InvalidStateException {
        socket.subscribe(streams, parameters, subscriber);
    }

    @Override
    public void unsubscribe(EnumSet<StreamSubscriptionEnum> streams) throws InvalidStateException {
        socket.unsubscribe(streams);
    }

    @Override
    public EnumSet<StreamSubscriptionEnum> getActiveSubscriptions() {
        return socket.getActiveSubscriptions();
    }

    @Override
    public void closeWhenComplete() {
        socket.closeWhenComplete();
    }



    // Presets *********************************************************************************************************
}
