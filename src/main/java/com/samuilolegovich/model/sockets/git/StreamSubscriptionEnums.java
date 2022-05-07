package com.samuilolegovich.model.sockets.git;

public enum StreamSubscriptionEnums {
    SERVER("server", "serverStatus"),
    LEDGER("ledger", "ledgerClosed"),
    TRANSACTIONS("transactions", "transaction");
    private String values;
    private String value;

    StreamSubscriptionEnums(String values, String value) {
        this.values = values;
        this.value = value;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
