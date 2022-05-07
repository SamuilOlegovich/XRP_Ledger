package com.samuilolegovich.model.test;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

// ws://localhost:8080/mycontextroot/hello.
@ServerEndpoint("/hello")
public class MyEndpoint {
    private Session session;

    @OnOpen
    public void onCreateSession(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onTextMessage (String message) {
        System.out.println ("WebSocket received message: " + message);
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText("From server  --> " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
