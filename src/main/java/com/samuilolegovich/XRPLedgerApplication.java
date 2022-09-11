package com.samuilolegovich;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Integer.parseInt;



public class XRPLedgerApplication {
    private  static byte[] bytes = new byte[128];

    public static void main(String[] args) throws Exception {
        // Обязательно стоит установить локаль иначе будет падать с ошибкой парсинга даты
        Locale.setDefault(Locale.ENGLISH);
//        okHttp();
//        test();
//        time();
//        getIndex();

//        ex();
    }


}
