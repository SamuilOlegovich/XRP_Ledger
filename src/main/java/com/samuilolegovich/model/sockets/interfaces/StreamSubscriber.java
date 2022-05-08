package com.samuilolegovich.model.sockets.interfaces;

import com.samuilolegovich.model.sockets.enums.StreamSubscriptionEnum;
import org.json.JSONObject;

public interface StreamSubscriber {
    void onSubscription(StreamSubscriptionEnum subscription, JSONObject message);
}
