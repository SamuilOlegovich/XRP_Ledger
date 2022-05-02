package com.samuilolegovich.model.PaymentManager;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentManager {
    void sendPayment(String address, Integer tag, BigDecimal numberOfXRP);
    void monitorAccountReplenishmentXRP(Object o);
    void setterWallet();
    void updateWallet();
    void startSocket();

    Map<String, String> connectAnExistingWallet(String seed);
    Map<String, String> createNewWallet();

    String getClassicAddress();
    String getPrivateKey();
    String getPublicKey();
    String getXAddress();
    String getSeed();
}
