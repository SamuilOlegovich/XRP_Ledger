package com.samuilolegovich;

import com.samuilolegovich.model.*;
import com.samuilolegovich.model.oldCode.SendXRPPayment;

import java.math.BigDecimal;
import java.util.Locale;


public class XRPLedgerApplication {
    private static PaymentManagerXRP paymentManagerXRP = null;
    private static PaymentManagerXRP paymentManagerXRPTwo = null;
    private static PaymentManagerXRP paymentManagerXRPThree = null;
    private static PaymentManagerXRP paymentManagerXRPFour = null;



    public static void main(String[] args) throws InterruptedException {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);

//        new TestDtoClasses().run();
//        new StartWorkPayment().run();
//        new SendXRPPayment().run();
//        new RealXRPWallet().run();


        startTest();
//        startReal();





    }


    private static void startReal() {
        // ----------------------------- REAL -----------------------------
        paymentManagerXRP = new PaymentManagerXRP();
        paymentManagerXRPTwo = new PaymentManagerXRP();
//
        paymentManagerXRP.createNewWallet();
        String seed = paymentManagerXRP.getSeed();
        System.out.println("SEED\n" + seed + "\n");

        System.out.println("----------------------------- TEST ONE -----------------------------");
        System.out.println("Test Private Seed\n " + paymentManagerXRP.getPrivateSeed() + "\n");
        System.out.println("Test Public Address\n " + paymentManagerXRP.getPublicAddress() + "\n");

//        paymentManagerXRP.sendPayment("ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5", 777, BigDecimal.ONE);

        paymentManagerXRPTwo.connectAnExistingWallet(paymentManagerXRP.getSeed());

        System.out.println("----------------------------- TEST TWO -----------------------------");
        System.out.println("Test Private Seed\n " + paymentManagerXRPTwo.getPrivateSeed() + "\n");
        System.out.println("Test Public Address\n " + paymentManagerXRPTwo.getPublicAddress() + "\n");
    }

    private static void startTest() {
        // ----------------------------- TEST -----------------------------
        paymentManagerXRPThree = new PaymentManagerXRP();
        paymentManagerXRPFour = new PaymentManagerXRP();
//
        paymentManagerXRPThree.createNewTestWallet();
        String seedTest = paymentManagerXRPThree.getTestSeed();
        System.out.println("SEED TEST\n" + seedTest + "\n");

        System.out.println("----------------------------- TEST ONE -----------------------------");
        System.out.println("Test Private Seed\n " + paymentManagerXRPThree.getTestPrivateSeed() + "\n");
        System.out.println("Test Public Address\n " + paymentManagerXRPThree.getTestPublicAddress() + "\n");

//        paymentManagerXRPThree.sendPayment("ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5", 777, BigDecimal.ONE);

        paymentManagerXRPFour.connectAnExistingTestWallet(paymentManagerXRPThree.getTestSeed());

        System.out.println("----------------------------- TEST TWO -----------------------------");
        System.out.println("Test Private Seed\n " + paymentManagerXRPFour.getTestPrivateSeed() + "\n");
        System.out.println("Test Public Address\n " + paymentManagerXRPFour.getTestPublicAddress() + "\n");
    }
}

