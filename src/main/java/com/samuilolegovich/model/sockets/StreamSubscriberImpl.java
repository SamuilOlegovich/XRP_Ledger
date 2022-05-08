package com.samuilolegovich.model.sockets;

import com.samuilolegovich.model.sockets.enums.StreamSubscriptionEnum;
import com.samuilolegovich.model.sockets.interfaces.StreamSubscriber;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamSubscriberImpl implements StreamSubscriber {
    private static final Logger LOG = LoggerFactory.getLogger(StreamSubscriberImpl.class);

    @Override
    public void onSubscription(StreamSubscriptionEnum subscription, JSONObject message) {
        LOG.info("subscription returned a {} message", subscription.getMessageType());
        // handle transaction || ledger message
    }
}
