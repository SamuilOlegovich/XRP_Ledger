package com.samuilolegovich.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;

@NoArgsConstructor
public class PaymentDtoTest implements Runnable {
    private ObjectMapper objectMapper;
    private PaymentDto paymentDto;
    private String stringJson;

    @Override
    @SneakyThrows
    public void run() {
        paymentDto = PaymentDto.builder()
                .destination("rUCzEr6jrEyMpjhs4wSdQdz4g8Y382NxfM")
                .account("rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe")
                .transactionType("Payment")
                .amount("2000000")
                .build();

        objectMapper = ObjectMapperFactory.create();
        stringJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(paymentDto);

        System.out.println("PaymentDto model:  -->  \n" + stringJson + "\n");
    }
}

/*
            Вывод
                {
                    "transactionType" : "Payment",
                    "account" : "rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe",
                    "amount" : "2000000",
                    "destination" : "rUCzEr6jrEyMpjhs4wSdQdz4g8Y382NxfM"
                }
*/