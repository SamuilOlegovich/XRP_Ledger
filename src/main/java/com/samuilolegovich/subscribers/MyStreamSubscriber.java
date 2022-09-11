package com.samuilolegovich.subscribers;

import com.samuilolegovich.model.sockets.enums.StreamSubscriptionEnum;
import com.samuilolegovich.subscribers.interfaces.StreamSubscriber;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MyStreamSubscriber implements StreamSubscriber {
    private static final Logger LOG = LoggerFactory.getLogger(MyStreamSubscriber.class);

    @Override
    public void onSubscription(StreamSubscriptionEnum subscription, JSONObject message) {
        LOG.info("subscription returned a {} message", subscription.getMessageType());
        // handle transaction || ledger message
        // // обработка транзакции || сообщение бухгалтерской книги
    }
}
