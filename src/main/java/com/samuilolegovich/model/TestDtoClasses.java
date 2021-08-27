package com.samuilolegovich.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuilolegovich.dto.PaymentDto;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;

@NoArgsConstructor
public class TestDtoClasses implements Runnable {

    @Override
    @SneakyThrows
    public void run() {
        PaymentDto paymentDto = PaymentDto.builder()
                .destination("rUCzEr6jrEyMpjhs4wSdQdz4g8Y382NxfM")
                .account("rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe")
                .transactionType("Payment")
                .amount("2000000")
                .build();

        ObjectMapper objectMapper = ObjectMapperFactory.create();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(paymentDto);

        System.out.println(json);

        /*
            Вывод
                {
                    "transactionType" : "Payment",
                    "account" : "rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe",
                    "amount" : "2000000",
                    "destination" : "rUCzEr6jrEyMpjhs4wSdQdz4g8Y382NxfM"
                }
        */
    }
}
