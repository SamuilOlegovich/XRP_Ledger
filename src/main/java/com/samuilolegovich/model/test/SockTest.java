package com.samuilolegovich.model.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuilolegovich.model.sockets.RequestDto;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SockTest {

    public static void main(String[] args) {
        MyClientEndpoint myClientEndpoint = new MyClientEndpoint();
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();

        ObjectMapper objectMapper = new ObjectMapper();
        RequestDto requestDto = new RequestDto();
        String serObj = null;
        try {
            serObj = objectMapper.writeValueAsString(requestDto);
            System.out.println(serObj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


            try {
                // ws://localhost:41725/XRP_Ledger/hello
//                Session session = webSocketContainer.connectToServer(myClientEndpoint, URI.create("wss://s.altnet.rippletest.net:51233"));
//                webSocketContainer.connectToServer(myClientEndpoint, URI.create("ws://india.colorado.edu:13"));
                webSocketContainer.connectToServer(myClientEndpoint, URI.create("ws://localhost:41725/XRP_Ledger/hello"));

                System.out.println("***********************************");
                myClientEndpoint.sendMessage(serObj);

            } catch (DeploymentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
