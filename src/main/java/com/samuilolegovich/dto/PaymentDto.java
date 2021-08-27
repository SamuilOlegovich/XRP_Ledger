package com.samuilolegovich.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto implements Serializable {
//    private static final String TransactionType = "Payment";
//    private static final String Account = "rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe";
//    private static final String Amount = "2000000";
//    private static final String Destination = "rUCzEr6jrEyMpjhs4wSdQdz4g8Y382NxfM";
    private String transactionType;     // Индикатор того, что это платеж.
    private String account;             // Адрес отправки.
    private String amount;              // Адрес, на который должен быть получен XRP («Пункт назначения»). Это не может совпадать с адресом отправки.
    private String destination;         // Сумма XRP для отправки («Сумма»). Обычно это целое число в «каплях» XRP, где 1000000 капель равняется 1 XRP.
}
