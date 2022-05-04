package com.samuilolegovich.model.PaymentManager;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentManager {
    void sendPayment(String address, Integer tag, BigDecimal numberOfXRP, boolean isReal);
    void monitorAccountReplenishmentXRP(Object o, boolean isReal);
    void setterWallet(boolean isReal);
    void updateWallet(boolean isReal);

    Map<String, String> connectAnExistingWallet(String seed, boolean isReal);
    Map<String, String> createNewWallet(boolean isReal);

    String getClassicAddress(boolean isReal);
    String getPrivateKey(boolean isReal);
    String getPublicKey(boolean isReal);
    String getXAddress(boolean isReal);
    String getSeed(boolean isReal);

    BigDecimal getAllBalance(boolean isReal);
    BigDecimal getBalance(boolean isReal);

    boolean isTest(boolean isReal);
}
