package com.samuilolegovich;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuilolegovich.enums.StringEnum;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.JsonRpcRequest;
import org.xrpl.xrpl4j.model.client.XrplResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.lang.Byte.parseByte;
import static java.lang.Integer.parseInt;


public class XRPLedgerApplication {
    private  static byte[] bytes = new byte[128];

    public static void main(String[] args) throws Exception {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);
        okHttp();


    }

    public static void okHttp() throws IOException {
//        String json = "{\"method\":\"account_info\",\"params\":[\"AccountInfoRequestParams\":{\"account\":\"rJFR5n99ZP2AnZh1656iA6jVexLBG5G5TN\",\"ledgerSpecifier\":\"LedgerSpecifier\":{\"ledgerIndexShortcut\":\"validated\"},\"strict\":true,\"queue\":false,\"signerLists\":true}]}";
//        String json = "{\"method\":\"account_info\",\"params\":[\"AccountInfoRequestParams\":{\"account\":\"rJFR5n99ZP2AnZh1656iA6jVexLBG5G5TN\",\"ledgerSpecifier\":\"LedgerSpecifier\":{\"ledgerIndexShortcut\":\"validated\"},\"strict\":true,\"queue\":false,\"signerLists\":true}]}";
//        String json = "{\"method\":\"account_info\",\"params\":[{\"account\":\"rJFR5n99ZP2AnZh1656iA6jVexLBG5G5TN\",\"LedgerSpecifier\":{\"ledgerIndexShortcut\":\"validated\"},\"strict\":true,\"queue\":false,\"signerLists\":true}]}";
        String json = "{\"method\":\"account_info\",\"params\":[{\"account\":\"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx\",\"LedgerSpecifier\":{\"ledgerIndexShortcut\":\"validated\"},\"strict\":true,\"queue\":false,\"signerLists\":true}]}";
//        String json = "{method=account_info, params=[{account=rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx, LedgerSpecifier:{ledgerIndexShortcut=validated}, strict=true, queue=false, signerLists=true}]}";
        String url ="https://s1.ripple.com:51234/";



        MediaType MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(json, MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // {"result":{"account_data":{"Account":"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx","Balance":"271597977","Flags":0,"LedgerEntryType":"AccountRoot","OwnerCount":0,"PreviousTxnID":"1036982A0C335E3D71CAB59BFEB432DD6892A24A29D07613192F204EE08DE923","PreviousTxnLgrSeq":72445550,"Sequence":236,"index":"CFF42BC480FB4AD45D47A01463BBAC6339A0C1A219F51F11FF4413C134EFD3A8"},"ledger_hash":"22B5095CFB7280A6C3DC4E4AEFB1CAFD12C5D48A459BA71FA44E51C9C0444121","ledger_index":72666598,"status":"success","validated":true,"warnings":[{"id":1004,"message":"This is a reporting server.  The default behavior of a reporting server is to only return validated data. If you are looking for not yet validated data, include \"ledger_index : current\" in your request, which will cause this server to forward the request to a p2p node. If the forward is successful the response will include \"forwarded\" : \"true\""}]}}
            String sR = Objects.requireNonNull(Objects.requireNonNull(response.body()).string());
            System.out.println("OkHttp     +++++++++     " + sR);
            System.out.println("OkHttp     +++++++++     " + response.message());
            System.out.println("OkHttp     +++++++++     " + response.code());
            System.out.println("OkHttp     +++++++++     " + response.headers());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(sR);
            JsonNode result = responseNode.get("result");
            System.out.println(result.toString());
        }
    }

//    default <T extends XrplResult> T send(JsonRpcRequest request, JavaType resultType) throws JsonRpcClientErrorException {
//        JsonNode response = postRpcRequest(request);
//        System.out.println("_______________________  JsonNode response" + response.toString());
//        JsonNode result = response.get("result");
//        System.out.println("_______________________   JsonNode result" + result.toString());
//        checkForError(response);
//        try {
//            return objectMapper.readValue(result.toString(), resultType);
//        } catch (JsonProcessingException e) {
//            throw new JsonRpcClientErrorException(e);
//        }
//    }
//
//    default void checkForError(JsonNode response) throws JsonRpcClientErrorException {
//        if (response.has("result")) {
//            JsonNode result = response.get("result");
//            if (result.has("error")) {
//                String errorMessage = Optional.ofNullable(result.get("error_exception"))
//                        .map(JsonNode::asText)
//                        .orElseGet(() -> result.get("error_message").asText());
//                throw new JsonRpcClientErrorException(errorMessage);
//            }
//        }
//    }





    public static void test() {
        String in = "{\"engine_result\":\"tesSUCCESS\",\"engine_result_code\":0,\"engine_result_message\":\"The transaction was applied. Only final in a validated ledger.\",\"ledger_hash\":\"7F0484A6DE51807C6855FA0E6DCFD0FC06F35330977AD89E6ECCD65BBEAAF055\",\"ledger_index\":71579825,\"meta\":{\"AffectedNodes\":[{\"ModifiedNode\":{\"FinalFields\":{\"Account\":\"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45\",\"Balance\":\"2519012680\",\"Flags\":0,\"OwnerCount\":15,\"Sequence\":67761420},\"LedgerEntryType\":\"AccountRoot\",\"LedgerIndex\":\"2FFD2EE64E5ED2B1EA2A6B281ABFA02049475021B054E0E6A0AD03A470543283\",\"PreviousFields\":{\"Balance\":\"2520012695\",\"Sequence\":67761419},\"PreviousTxnID\":\"612A735915B92AAD9FF3C7325A14203F2A969E82095B4727E084F4F7EA1BEEE0\",\"PreviousTxnLgrSeq\":71579596}},{\"ModifiedNode\":{\"FinalFields\":{\"Account\":\"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx\",\"Balance\":\"49979548\",\"Flags\":0,\"OwnerCount\":7,\"Sequence\":122},\"LedgerEntryType\":\"AccountRoot\",\"LedgerIndex\":\"CFF42BC480FB4AD45D47A01463BBAC6339A0C1A219F51F11FF4413C134EFD3A8\",\"PreviousFields\":{\"Balance\":\"48979548\"},\"PreviousTxnID\":\"612A735915B92AAD9FF3C7325A14203F2A969E82095B4727E084F4F7EA1BEEE0\",\"PreviousTxnLgrSeq\":71579596}}],\"TransactionIndex\":85,\"TransactionResult\":\"tesSUCCESS\",\"delivered_amount\":\"1000000\"},\"status\":\"closed\",\"transaction\":{\"Account\":\"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45\",\"Amount\":\"1000000\",\"Destination\":\"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx\",\"DestinationTag\":333,\"Fee\":\"15\",\"Flags\":2147483648,\"LastLedgerSequence\":71579833,\"Sequence\":67761419,\"SigningPubKey\":\"03ACEAC0C382BB221C7BA96120DE3257E0F2549D12F6403D550496B849B229C79D\",\"TransactionType\":\"Payment\",\"TxnSignature\":\"30440220096386C630533A46FD1A13B9CE7A7A2F88312C6E2DCC93AA477DF291E32B7C5E02201A49F42103FF7A6B0C47F998040A5D09AB489C3E5E55C591E4FFE2D6E1571BF4\",\"date\":705588161,\"hash\":\"99343B8A27A974DF1FC7BB430F76E739B30A1CE8666A18A59F2AF45B8A7FEF35\"},\"type\":\"transaction\",\"validated\":true}\n";
        // {"date":705588161,"Account":"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45","Destination":"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx","TransactionType":"Payment","SigningPubKey":"03ACEAC0C382BB221C7BA96120DE3257E0F2549D12F6403D550496B849B229C79D","Amount":"1000000","Fee":"15","Flags":2147483648,"Sequence":67761419,"LastLedgerSequence":71579833,"TxnSignature":"30440220096386C630533A46FD1A13B9CE7A7A2F88312C6E2DCC93AA477DF291E32B7C5E02201A49F42103FF7A6B0C47F998040A5D09AB489C3E5E55C591E4FFE2D6E1571BF4","DestinationTag":333,"hash":"99343B8A27A974DF1FC7BB430F76E739B30A1CE8666A18A59F2AF45B8A7FEF35"}
        JSONObject json = new JSONObject(in);
        System.out.println("delivered_amount --->> " + json.getJSONObject("meta").getString("delivered_amount"));
//        "ModifiedNode":{"FinalFields":{"Account":"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx","Balance":"48979533"
        String myAcc = json.getJSONObject("meta").getJSONArray("AffectedNodes").getJSONObject(1).getJSONObject("ModifiedNode").getJSONObject("FinalFields").getString("Account");
        System.out.println("FinalFields ---> " + json.getJSONObject("meta").getJSONArray("AffectedNodes").getJSONObject(1).getJSONObject("ModifiedNode").getJSONObject("FinalFields").toString());
        System.out.println("Balance ---> " + json.getJSONObject("meta").getJSONArray("AffectedNodes").getJSONObject(1).getJSONObject("ModifiedNode").getJSONObject("FinalFields").getString("Balance"));

        // [
        // {"ModifiedNode":{"LedgerIndex":"2FFD2EE64E5ED2B1EA2A6B281ABFA02049475021B054E0E6A0AD03A470543283","FinalFields":{"Account":"rnoawLeRq2Vg3Rt8UiMqQdF6RbiQerdN45","OwnerCount":15,"Flags":0,"Sequence":67761420,"Balance":"2519012680"},"PreviousFields":{"Sequence":67761419,"Balance":"2520012695"},"PreviousTxnLgrSeq":71579596,"LedgerEntryType":"AccountRoot","PreviousTxnID":"612A735915B92AAD9FF3C7325A14203F2A969E82095B4727E084F4F7EA1BEEE0"}},
        // {"ModifiedNode":{"LedgerIndex":"CFF42BC480FB4AD45D47A01463BBAC6339A0C1A219F51F11FF4413C134EFD3A8","FinalFields":{"Account":"rsG3xqRQSnxfYfF9foHfy7fNEZZctDc3Dx","OwnerCount":7,"Flags":0,"Sequence":122,"Balance":"49979548"},"PreviousFields":{"Balance":"48979548"},"PreviousTxnLgrSeq":71579596,"LedgerEntryType":"AccountRoot","PreviousTxnID":"612A735915B92AAD9FF3C7325A14203F2A969E82095B4727E084F4F7EA1BEEE0"}}
        // ]

        System.out.println(json.getString("engine_result"));
        System.out.println(json.get("engine_result_code" + ""));
        System.out.println(json.getString("engine_result_message"));
        System.out.println(json.getString("type"));
        System.out.println(json.getBoolean("validated"));
        System.out.println(json.get("transaction"));
        System.out.println("date" + json.getJSONObject("transaction").getBigDecimal("date"));
        System.out.println(json.getJSONObject("transaction").getString("Destination"));
        System.out.println(json.getJSONObject("transaction").getString("Account"));
        System.out.println(json.getJSONObject("transaction").getString("TransactionType"));
        System.out.println(json.getJSONObject("transaction").getInt("DestinationTag"));

        // {"result":{"fee_base":10,"fee_ref":10,"ledger_hash":"22444765798B47D2F7CAF8D6635D5280845455A8A5AD82C2DDD50DD9DDD95E3C","ledger_index":27816950,"ledger_time":706017401,"reserve_base":10000000,"reserve_inc":2000000,"validated_ledgers":"26210849-27816950"},"status":"success","type":"response"}

        // {"fee_base":10,"fee_ref":10,"ledger_hash":"F5D6C52FED554957A582480430A55672FBFC9C02371885A857C99EED20DB6FA3","ledger_index":27816951,"ledger_time":706017402,"reserve_base":10000000,"reserve_inc":2000000,"txn_count":3,"type":"ledgerClosed","validated_ledgers":"26210849-27816951"}

        // пополнение без тега
        // {"engine_result":"tesSUCCESS","engine_result_code":0,"engine_result_message":"The transaction was applied. Only final in a validated ledger.","ledger_hash":"F5D6C52FED554957A582480430A55672FBFC9C02371885A857C99EED20DB6FA3","ledger_index":27816951,"meta":{"AffectedNodes":[{"CreatedNode":{"LedgerEntryType":"AccountRoot","LedgerIndex":"00EB83968F177FBCF5653D81C38F0DFDE95B71B2245EA9F4498281ABD2DB8D4F","NewFields":{"Account":"r4qwqwpYMiSMbwi3Rccw2p4ExvkFACNb6f","Balance":"1000000000","Sequence":27816951}}},{"ModifiedNode":{"FinalFields":{"Account":"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe","Balance":"95189538795201855","Flags":0,"OwnerCount":0,"Sequence":3805693},"LedgerEntryType":"AccountRoot","LedgerIndex":"31CCE9D28412FF973E9AB6D0FA219BACF19687D9A2456A0C2ABC3280E9D47E37","PreviousFields":{"Balance":"95189539795201867","Sequence":3805692},"PreviousTxnID":"4DDE3A6A4A4F7011B722D0D7AE99EB35FDAA0DB4DC4D51F2EF0F07093522CB67","PreviousTxnLgrSeq":27816942}}],"TransactionIndex":1,"TransactionResult":"tesSUCCESS","delivered_amount":"1000000000"},"status":"closed","transaction":{"Account":"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe","Amount":"1000000000","Destination":"r4qwqwpYMiSMbwi3Rccw2p4ExvkFACNb6f","Fee":"12","Flags":2147483648,"LastLedgerSequence":27816954,"Sequence":3805692,"SigningPubKey":"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC","TransactionType":"Payment","TxnSignature":"30450221009A93E59B9F042EEA04DAB3B5B51E528EF0D57C1C1375F394F5AC94119D14F5470220697561CA08BE06F9F0BA28F942A3B39C93234B9C72FBD9867FF37476598F40C3","date":706017402,"hash":"BE403D81B498B1B382B0A3E03A7A908D87C6D1E3484C813AAFC56E25CF3EF274"},"type":"transaction","validated":true}
        // пополнение с тегом
        // {"engine_result":"tesSUCCESS","engine_result_code":0,"engine_result_message":"The transaction was applied. Only final in a validated ledger.","ledger_hash":"A3871CD2DC09E7BF42582DA5CC8DCA5A437752E10A7017D741DD109151C76867","ledger_index":27816954,"meta":{"AffectedNodes":[{"ModifiedNode":{"FinalFields":{"Account":"r4qwqwpYMiSMbwi3Rccw2p4ExvkFACNb6f","Balance":"989999990","Flags":0,"OwnerCount":0,"Sequence":27816952},"LedgerEntryType":"AccountRoot","LedgerIndex":"00EB83968F177FBCF5653D81C38F0DFDE95B71B2245EA9F4498281ABD2DB8D4F","PreviousFields":{"Balance":"1000000000","Sequence":27816951},"PreviousTxnID":"BE403D81B498B1B382B0A3E03A7A908D87C6D1E3484C813AAFC56E25CF3EF274","PreviousTxnLgrSeq":27816951}},{"ModifiedNode":{"FinalFields":{"Account":"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe","Balance":"95189537805201843","Flags":0,"OwnerCount":0,"Sequence":3805694},"LedgerEntryType":"AccountRoot","LedgerIndex":"31CCE9D28412FF973E9AB6D0FA219BACF19687D9A2456A0C2ABC3280E9D47E37","PreviousFields":{"Balance":"95189537795201843"},"PreviousTxnID":"892550004561F93E97F4F09301C2C4478E680F9E181B8D11F5DF72CB326C43F1","PreviousTxnLgrSeq":27816952}}],"TransactionIndex":0,"TransactionResult":"tesSUCCESS","delivered_amount":"10000000"},"status":"closed","transaction":{"Account":"r4qwqwpYMiSMbwi3Rccw2p4ExvkFACNb6f","Amount":"10000000","Destination":"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe","DestinationTag":777,"Fee":"10","Flags":2147483648,"LastLedgerSequence":27816956,"Sequence":27816951,"SigningPubKey":"EDC13ECA9F790116ABA8EB13BC697DA9F658CF18D6A1AB3F17EC541C3F563C2992","TransactionType":"Payment","TxnSignature":"C2C24517C28DBD3F542DD08AAC2673B5326685DD1322C368EE79A5F0ED0EA608BD3740E3E5D48BB72FAE6D8BE73CE5999AF8BA4BC0815DEB93C84DDFA1E15A07","date":706017412,"hash":"E30718DEBCE31EFE5BBDF1878F48C8FAAF2A3C21F9CA83386767E26BA6E4A562"},"type":"transaction","validated":true}

        BigDecimal x = new BigDecimal("1.5");
        BigDecimal y = new BigDecimal("1.501");

        System.out.println(x.compareTo(y));
        System.out.println(x.toString());


        BigDecimal z = new BigDecimal("777666000");
        String s = z.toString();
        StringBuilder stringBuilder = new StringBuilder(s);
        stringBuilder.replace(s.length() - 6, s.length(), "");
        System.out.println(stringBuilder);
        stringBuilder.insert(0, 666);
        System.out.println(stringBuilder);
        System.out.println(x.multiply(new BigDecimal(2)));


        Integer ii = 2;
        Integer iii = 2;
        int iiii = 2;
        int iiiii = 2;
        System.out.println(ii.equals(iii));
        System.out.println(iii.equals(iiii));
        System.out.println(iiii == iiiii);
        System.out.println(iiii == iiiii);

        System.out.println("Lead the seed".toUpperCase());

        long ONE_XRP_IN_DROPS = 1_000_000L;
        BigDecimal one = new BigDecimal("62333444");
        BigDecimal tow = new BigDecimal("10000000");
        System.out.println("+++++++++++++++++" + one.toString());
        System.out.println("+++++++++++++++++" + tow.toString());
        BigDecimal bigDecimal = one.subtract(tow).divide(BigDecimal.valueOf(ONE_XRP_IN_DROPS), MathContext.DECIMAL128);
        System.out.println("+++++++++++++++++" + bigDecimal);

//        balanceXRP = one.subtract(BigDecimal.valueOf(10));
//        balanceXRP = one.subtract(tow).divide(BigDecimal.valueOf(ONE_XRP_IN_DROPS), MathContext.DECIMAL128);
//        setBalance();



        // tag 2147483647
        String tagResponse = "1234567890";
        String subStringOne = tagResponse.substring(0, 3);
        String subStringTwo = tagResponse.substring(3, 10);
        System.out.println(subStringOne);
        System.out.println(subStringTwo);
    }

}
