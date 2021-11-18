package com.samuilolegovich;

import com.samuilolegovich.model.RealXRPWallet;
import com.samuilolegovich.model.SendXRPPayment;
import com.samuilolegovich.model.TestDtoClasses;
import com.samuilolegovich.model.StartWorkPayment;

import java.util.Locale;


public class XRPLedgerApplication {

    public static void main(String[] args)  {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);
        new TestDtoClasses().run();
        new StartWorkPayment().run();
        new SendXRPPayment().run();
//        new RealXRPWallet().run();
    }
}
