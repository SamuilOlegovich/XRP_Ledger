package com.samuilolegovich.model.sockets.git;

import org.json.JSONObject;

public interface StreamSubscriber {
    public void onSubscription(StreamSubscription subscription, JSONObject message);
}
