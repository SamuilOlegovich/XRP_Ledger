package com.samuilolegovich;

import com.samuilolegovich.enums.EnumBoo;
import com.samuilolegovich.enums.EnumStr;
import com.samuilolegovich.model.PaymentManager.PaymentManager;
import com.samuilolegovich.model.PaymentManager.PaymentManagerXRP;

import java.math.BigDecimal;
import java.util.Locale;


public class XRPLedgerApplication {

    public static void main(String[] args)  {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);

//        new PaymentTest().run();
//        new PaymentReal().run();
        EnumBoo.setValue(EnumBoo.IS_REAL, true);
        EnumBoo.setValue(EnumBoo.IS_WALLET, true);
//        EnumStr.setValue(EnumStr.REAL_SEED, "sEdSyKacM9uMcHco7o8oEnu1hyYnSVP");

        PaymentManager paymentManager = new PaymentManagerXRP();

        System.out.println("X Address  -- >  " + paymentManager.getXAddress(EnumBoo.IS_REAL.b));
        System.out.println("Classic Address  -- >  " + paymentManager.getClassicAddress(EnumBoo.IS_REAL.b));
        System.out.println("Private Key  -- >  " + paymentManager.getPrivateKey(EnumBoo.IS_REAL.b));
        System.out.println("Public Key  -- >  " + paymentManager.getPublicKey(EnumBoo.IS_REAL.b));
        System.out.println("Seed  -- >  " + paymentManager.getSeed(EnumBoo.IS_REAL.b));
        System.out.println("Balance  -- >  " + paymentManager.getBalance(EnumBoo.IS_REAL.b));

        paymentManager.sendPayment(EnumStr.ADDRESS_REAL.value, 777, BigDecimal.ONE, EnumBoo.IS_REAL.b);

        System.out.println("Balance  -- >  " + paymentManager.getBalance(EnumBoo.IS_REAL.b));
        System.out.println();

    }

    // СДЕЛАТЬ ОБНОВЛЕНИЕ БАЛАНСА ПО ЕГО ЗАПРОСУ ИНАЧЕ ОН ПОКАЗЫВАЕТ ОТ ПОСЛЕНЕГО ОБНОВЛЕНИЯ
}
