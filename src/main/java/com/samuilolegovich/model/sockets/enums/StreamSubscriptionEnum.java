package com.samuilolegovich.model.sockets.enums;

import java.util.HashMap;
import java.util.Map;

public enum StreamSubscriptionEnum {
    SERVER("server", "serverStatus"),
    LEDGER("ledger", "ledgerClosed"),
    TRANSACTIONS("transactions", "transaction"),
    ACCOUNT_CHANNELS("ledger", "transaction"),
//    FLOW_OF_INCOMING_AND_OUTGOING_TRANSACTIONS("ledger", "transaction"),
//    ACCOUNT_CHANNELS("accounts", "transaction"),
    ;

    private final String responseMessageType;
    private final String name;

    private static final Map<String, StreamSubscriptionEnum> lookupByMessageType = new HashMap<>();
    private static final Map<String, StreamSubscriptionEnum> lookupByName = new HashMap<>();


    static {
        for (StreamSubscriptionEnum enums : StreamSubscriptionEnum.values()) {
            lookupByMessageType.put(enums.getMessageType(), enums);
            lookupByName.put(enums.getName(), enums);
        }
    }


    StreamSubscriptionEnum(String name, String responseMessageType) {
        this.responseMessageType = responseMessageType;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMessageType() {
        return responseMessageType;
    }



    public static StreamSubscriptionEnum byMessageType(String type) {
        return lookupByMessageType.get(type);
    }

    public static StreamSubscriptionEnum byName(String name) {
        return lookupByName.get(name);
    }
}
