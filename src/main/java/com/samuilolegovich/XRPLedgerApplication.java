package com.samuilolegovich;

import java.math.BigDecimal;
import java.util.Locale;


public class XRPLedgerApplication {

    public static void main(String[] args) throws Exception {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);


        BigDecimal x = new BigDecimal("1.5");
        BigDecimal y = new BigDecimal("1.501");

        System.out.println(x.compareTo(y));
    }
}
