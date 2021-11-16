package com.samuilolegovich.model;

import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.wallet.DefaultWalletFactory;
import org.xrpl.xrpl4j.wallet.SeedWalletGenerationResult;
import org.xrpl.xrpl4j.wallet.Wallet;
import org.xrpl.xrpl4j.wallet.WalletFactory;

public class RealXRPWallet implements Runnable {


    @Override
    public void run() {
        // Получение реального счета XRP --------------------------------------------------------
        WalletFactory walletFactory = DefaultWalletFactory.getInstance();
        // Eсли реальный счет есть используем эту конструкцию и добавляем в нее реальный ключ сид
        // Wallet wallet = walletFactory.fromSeed("ss3Mm19qHo4YLJQDdkyCqwnpMxNRU", true);
        // иначе
        SeedWalletGenerationResult generationResult = walletFactory.randomWallet(false);
        Wallet wallet = generationResult.wallet();
        System.out.println(wallet.classicAddress()); // Example: rGCkuB7PBr5tNy68tPEABEtcdno4hE6Y7f
        System.out.println(generationResult.seed()); // Example: sp6JS7f14BuwFY8Mw6bTtLKWauoUs



        // Подключение к производственной книге XRP --------------------------------------------------------
        HttpUrl rippledUrl = HttpUrl.get("https://xrplcluster.com");
        // HttpUrl rippledUrl = HttpUrl.get("https://s1.ripple.com:51234/");
        // HttpUrl rippledUrl = HttpUrl.get("https://s2.ripple.com:51234/");
        XrplClient xrplClient = new XrplClient(rippledUrl);

    }
}
