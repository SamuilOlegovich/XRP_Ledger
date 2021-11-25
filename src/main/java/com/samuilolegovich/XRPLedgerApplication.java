package com.samuilolegovich;

import com.samuilolegovich.model.*;
import com.samuilolegovich.model.oldCode.StartWorkPayment;

import java.util.Locale;
import java.util.Map;


public class XRPLedgerApplication {
    // REAL
    private static PaymentManagerXRP paymentManagerXRP = null;
    private static PaymentManagerXRP paymentManagerXRPTwo = null;
    // TEST
    private static TestPaymentManagerXRP testPaymentManagerXRPOne = null;
    private static TestPaymentManagerXRP testPaymentManagerXRPTwo = null;



    public static void main(String[] args) throws InterruptedException {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);

//        new TestDtoClasses().run();
        new StartWorkPayment().run();
//        new SendXRPPayment().run();
//        new RealXRPWallet().run();


//        startTest();
//        startReal();
    }


    private static void startReal() {
        // ----------------------------- REAL -----------------------------
        paymentManagerXRP = new PaymentManagerXRP();
        paymentManagerXRPTwo = new PaymentManagerXRP();

        Map<String, String> map = paymentManagerXRP.createNewWallet();
        String seed = paymentManagerXRP.getSeed();
        System.out.println("SEED\n" + seed + "\n");

        System.out.println("----------------------------- TEST ONE REAL-----------------------------");
        System.out.println("Test Private Seed\n " + paymentManagerXRP.getPrivateKey() + "\n");
        System.out.println("Test Public Address\n " + paymentManagerXRP.getClassicAddress() + "\n");

        Map<String, String> stringMap = paymentManagerXRPTwo.connectAnExistingWallet(paymentManagerXRP.getSeed());

        System.out.println("----------------------------- TEST TWO REAL -----------------------------");
        System.out.println("Test Private Seed\n " + paymentManagerXRPTwo.getPrivateKey() + "\n");
        System.out.println("Test Public Address\n " + paymentManagerXRPTwo.getClassicAddress() + "\n");

//        paymentManagerXRP.sendPayment("ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5", 777, BigDecimal.ONE);
    }

    private static void startTest() {
        // ----------------------------- TEST -----------------------------
        // создаем менеджеры платежей
        testPaymentManagerXRPOne = new TestPaymentManagerXRP();
        testPaymentManagerXRPTwo = new TestPaymentManagerXRP();

        // создаем новый тестовый кошелек и получаем востановительную сид фразу
        Map<String, String> map = testPaymentManagerXRPOne.createNewTestWallet();
        String seedTest = testPaymentManagerXRPOne.getTestSeed();
        System.out.println("SEED TEST\n" + seedTest + "\n");

        System.out.println("----------------------------- TEST ONE TEST -----------------------------");
        System.out.println("Test Private Seed\n " + testPaymentManagerXRPOne.getTestPrivateKey() + "\n");
        System.out.println("Test Public Address\n " + testPaymentManagerXRPOne.getTestClassicAddress() + "\n");


        // востанавливаем кошелек из полученой востановительной фразы и проверяем его выводимые данные на совпадение
        Map<String, String> stringMap = testPaymentManagerXRPTwo.connectAnExistingTestWallet(testPaymentManagerXRPOne.getTestSeed());

        System.out.println("----------------------------- TEST TWO TEST -----------------------------");
        System.out.println("Test Private Seed\n " + testPaymentManagerXRPTwo.getTestPrivateKey() + "\n");
        System.out.println("Test Public Address\n " + testPaymentManagerXRPTwo.getTestClassicAddress() + "\n");


//        paymentManagerXRPThree.sendPayment("ra3GsPkHcLf3TS7asKXqzVAx2wR6mvaFs5", 777, BigDecimal.ONE);
    }
}

