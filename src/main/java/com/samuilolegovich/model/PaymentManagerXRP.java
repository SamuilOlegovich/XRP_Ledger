package com.samuilolegovich.model;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public final class PaymentManagerXRP {
    private TestWalletXRP testWalletXRP;
    private WalletXRP walletXRP;

    private TestSocketXRP testSocketXRP;
    private SocketXRP socketXRP;



    public void sendPaymentToAddressXRP(String address, String tag, String numberOfXRP) {}
    public void connectAnExistingWalletXRP(String seed){}
    public void createNewWalletXRP(){}
    public void startSocketXRP(){}

    public String getWalletXRPPublicAddress(){}
    public String getWalletXRPPrivateSeed(){}



    // Тест

    public void sendTestPaymentToAddressXRP(String address, String tag, String numberOfXRP) {}
    public void connectAnExistingTestWalletXRP(String seed){}
    public void createNewTestWalletXRP(){}
    public void startTestSocketXRP(){}

    public String getTestWalletXRPPublicAddress(){}
    public String getTestWalletXRPPrivateSeed(){}



    // тут подумать как лучше это сделать
    public void monitorAccountReplenishmentXRP(Object o){}
    public void monitorTestAccountReplenishmentXRP(Object o){}
}
