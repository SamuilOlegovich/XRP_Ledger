package com.samuilolegovich;

import com.samuilolegovich.dto.PaymentDtoTest;
import com.samuilolegovich.model.payment.PaymentReal;
import com.samuilolegovich.model.payment.PaymentTest;

import java.util.Locale;


public class XRPLedgerApplication {

    public static void main(String[] args)  {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);

//        new PaymentTest().run();
//        new PaymentReal().run();

    }
}
