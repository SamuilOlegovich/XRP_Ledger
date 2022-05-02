package com.samuilolegovich.model.wallets;

import java.util.Map;

public interface Wallets {
    Map<String, String> createNewWallet();
    String getClassicAddress();
    String getPrivateKey();
    String getPublicKey();
    String getXAddress();
    String getSeed();
}
