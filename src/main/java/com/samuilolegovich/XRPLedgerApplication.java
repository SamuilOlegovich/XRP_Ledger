package com.samuilolegovich;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.samuilolegovich.model.TestDtoClasses;
import com.samuilolegovich.model.TestPayment;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;

public class XRPLedgerApplication {
//    private static String xrpAddress = "rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe";

    public static void main(String[] args) throws JsonRpcClientErrorException, JsonProcessingException {
        new TestDtoClasses().run();
        new TestPayment().run();

    }
}
