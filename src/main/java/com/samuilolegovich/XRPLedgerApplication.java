package com.samuilolegovich;

import com.samuilolegovich.model.*;

import java.util.Locale;


public class XRPLedgerApplication {
    private static PaymentManagerXRP paymentManagerXRP = null;
    private static PaymentManagerXRP paymentManagerXRPTwo = null;

    public static void main(String[] args)  {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);
//        new TestDtoClasses().run();
//        new StartWorkPayment().run();
//        new SendXRPPayment().run();
//        new RealXRPWallet().run();
        paymentManagerXRP = new PaymentManagerXRP();
        paymentManagerXRPTwo = new PaymentManagerXRP();

        paymentManagerXRP.createNewTestWallet();
        System.out.println("Test Private Seed\n " + paymentManagerXRP.getTestPrivateSeed() + "\n");
        System.out.println("Test Public Address\n " + paymentManagerXRP.getTestPublicAddress() + "\n");

        paymentManagerXRPTwo.connectAnExistingTestWallet(paymentManagerXRP.getTestPrivateSeed());
        System.out.println("Test Private Seed\n " + paymentManagerXRPTwo.getTestPrivateSeed() + "\n");
        System.out.println("Test Public Address\n " + paymentManagerXRPTwo.getTestPublicAddress() + "\n");
    }
}
