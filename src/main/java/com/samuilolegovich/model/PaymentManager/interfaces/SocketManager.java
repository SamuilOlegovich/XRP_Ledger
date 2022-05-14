package com.samuilolegovich.model.PaymentManager.interfaces;

import com.samuilolegovich.model.sockets.enums.StreamSubscriptionEnum;
import com.samuilolegovich.model.sockets.exceptions.InvalidStateException;
import com.samuilolegovich.model.sockets.interfaces.CommandListener;
import com.samuilolegovich.model.sockets.interfaces.StreamSubscriber;

import java.util.EnumSet;
import java.util.Map;

public interface SocketManager {
    void subscribe(EnumSet<StreamSubscriptionEnum> streams, Map<String, Object> parameters, StreamSubscriber subscriber) throws InvalidStateException;
    void subscribe(EnumSet<StreamSubscriptionEnum> streams, StreamSubscriber subscriber) throws InvalidStateException;
    void unsubscribe(EnumSet<StreamSubscriptionEnum> streams) throws InvalidStateException;
    void closeWhenComplete();

    String sendCommand(String command, Map<String, Object> parameters, CommandListener listener) throws InvalidStateException;
    String sendCommand(String command, CommandListener listener) throws InvalidStateException;

    EnumSet<StreamSubscriptionEnum> getActiveSubscriptions();
}
