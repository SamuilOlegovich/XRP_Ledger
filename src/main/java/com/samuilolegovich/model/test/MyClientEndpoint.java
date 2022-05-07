package com.samuilolegovich.model.test;

import javax.websocket.*;
import java.io.IOException;

@ClientEndpoint
public class MyClientEndpoint {
    private Session session;

    @OnOpen
    public void onOpen (Session session) {
        System.out.println ("WebSocket opened: " + session.getId());
        this.session = session;
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println("!!!!!! retrieved: " + message);
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    @OnMessage
    public void sendMessage (String message) {
        System.out.println ("WebSocket received message: " + message);
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
