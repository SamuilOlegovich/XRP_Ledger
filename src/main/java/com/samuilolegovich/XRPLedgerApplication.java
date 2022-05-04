package com.samuilolegovich;

import com.samuilolegovich.enums.BooleanEnum;
import com.samuilolegovich.enums.StringEnum;
import com.samuilolegovich.model.PaymentManager.PaymentManager;
import com.samuilolegovich.model.PaymentManager.PaymentManagerXRP;

import java.math.BigDecimal;
import java.util.Locale;


public class XRPLedgerApplication {

    public static void main(String[] args)  {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);
        testPayment();
    }

    public static void testPayment() {
//        new PaymentTest().run();
//        new PaymentReal().run();
        BooleanEnum.setValue(BooleanEnum.IS_REAL, false);
        BooleanEnum.setValue(BooleanEnum.IS_WALLET, true);

        PaymentManager paymentManager = new PaymentManagerXRP();

        System.out.println("X Address  -- >  " + paymentManager.getXAddress(BooleanEnum.IS_REAL.b));
        System.out.println("Classic Address  -- >  " + paymentManager.getClassicAddress(BooleanEnum.IS_REAL.b));
        System.out.println("Private Key  -- >  " + paymentManager.getPrivateKey(BooleanEnum.IS_REAL.b));
        System.out.println("Public Key  -- >  " + paymentManager.getPublicKey(BooleanEnum.IS_REAL.b));
        System.out.println("Seed  -- >  " + paymentManager.getSeed(BooleanEnum.IS_REAL.b));

        System.out.println("All Balance  -- >  " + paymentManager.getAllBalance(BooleanEnum.IS_REAL.b));
        System.out.println("Balance  -- >  " + paymentManager.getBalance(BooleanEnum.IS_REAL.b));

        paymentManager.sendPayment(StringEnum.ADDRESS_REAL.value, 777, BigDecimal.ONE, BooleanEnum.IS_REAL.b);

        System.out.println("AllBalance  -- >  " + paymentManager.getAllBalance(BooleanEnum.IS_REAL.b));
        System.out.println("Balance  -- >  " + paymentManager.getBalance(BooleanEnum.IS_REAL.b));
    }
}
