package com.samuilolegovich.model.wallets;

import java.util.Map;

public interface MyWallets {
    Map<String, String> createNewWallet();
    String getBalance();
    String getSeed();
}
