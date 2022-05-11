package com.samuilolegovich;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Locale;


public class XRPLedgerApplication {

    public static void main(String[] args) throws Exception {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);
        String in =  "{\"engine_result\":\"tesSUCCESS\",\"engine_result_code\":0,\"engine_result_message\":\"The transaction was applied. Only final in a validated ledger.\",\"ledger_hash\":\"7F0484A6DE51807C6855FA0E6DCFD0FC06F35330977AD89E6ECCD65BBEAAF055\",\"ledger_index\":71579825,\"meta\":{\"AffectedNodes\":[{\"ModifiedNode\":{\"FinalFields\":{\"Account\":\"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45\",\"Balance\":\"2519012680\",\"Flags\":0,\"OwnerCount\":15,\"Sequence\":67761420},\"LedgerEntryType\":\"AccountRoot\",\"LedgerIndex\":\"2FFD2EE64E5ED2B1EA2A6B281ABFA02049475021B054E0E6A0AD03A470543283\",\"PreviousFields\":{\"Balance\":\"2520012695\",\"Sequence\":67761419},\"PreviousTxnID\":\"612A735915B92AAD9FF3C7325A14203F2A969E82095B4727E084F4F7EA1BEEE0\",\"PreviousTxnLgrSeq\":71579596}},{\"ModifiedNode\":{\"FinalFields\":{\"Account\":\"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx\",\"Balance\":\"49979548\",\"Flags\":0,\"OwnerCount\":7,\"Sequence\":122},\"LedgerEntryType\":\"AccountRoot\",\"LedgerIndex\":\"CFF42BC480FB4AD45D47A01463BBAC6339A0C1A219F51F11FF4413C134EFD3A8\",\"PreviousFields\":{\"Balance\":\"48979548\"},\"PreviousTxnID\":\"612A735915B92AAD9FF3C7325A14203F2A969E82095B4727E084F4F7EA1BEEE0\",\"PreviousTxnLgrSeq\":71579596}}],\"TransactionIndex\":85,\"TransactionResult\":\"tesSUCCESS\",\"delivered_amount\":\"1000000\"},\"status\":\"closed\",\"transaction\":{\"Account\":\"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45\",\"Amount\":\"1000000\",\"Destination\":\"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx\",\"DestinationTag\":333,\"Fee\":\"15\",\"Flags\":2147483648,\"LastLedgerSequence\":71579833,\"Sequence\":67761419,\"SigningPubKey\":\"03ACEAC0C382BB221C7BA96120DE3257E0F2549D12F6403D550496B849B229C79D\",\"TransactionType\":\"Payment\",\"TxnSignature\":\"30440220096386C630533A46FD1A13B9CE7A7A2F88312C6E2DCC93AA477DF291E32B7C5E02201A49F42103FF7A6B0C47F998040A5D09AB489C3E5E55C591E4FFE2D6E1571BF4\",\"date\":705588161,\"hash\":\"99343B8A27A974DF1FC7BB430F76E739B30A1CE8666A18A59F2AF45B8A7FEF35\"},\"type\":\"transaction\",\"validated\":true}\n";
        JSONObject json = new JSONObject(in);
        System.out.println(json.getString("engine_result"));
        System.out.println(json.getString("engine_result_code" + ""));
        System.out.println(json.getString("engine_result_message"));
//        System.out.println(json.getString("DestinationTag"));
//        System.out.println(json.getString("TransactionType"));
//        System.out.println(json.getString("Account"));
//        System.out.println(json.getString("Destination"));
//        System.out.println(json.getString("date"));
        System.out.println(json.getString("type"));
        System.out.println(json.getString("validated"));


        BigDecimal x = new BigDecimal("1.5");
        BigDecimal y = new BigDecimal("1.501");

        System.out.println(x.compareTo(y));
    }
}
