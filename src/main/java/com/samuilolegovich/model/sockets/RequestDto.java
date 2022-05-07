package com.samuilolegovich.model.sockets;

import java.io.Serializable;

public class RequestDto implements Serializable {
    private long id = 1;
    private String command = "account_channels";
    private String account = "rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn";
    private String destination_account = "ra5nK24KXen9AHvsdFTKHSANinZseWnPcX";
    private String ledger_index = "validated";

    public RequestDto(long id, String command, String account, String destination_account, String ledger_index) {
        this.id = id;
        this.command = command;
        this.account = account;
        this.destination_account = destination_account;
        this.ledger_index = ledger_index;
    }

    public RequestDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDestination_account() {
        return destination_account;
    }

    public void setDestination_account(String destination_account) {
        this.destination_account = destination_account;
    }

    public String getLedger_index() {
        return ledger_index;
    }

    public void setLedger_index(String ledger_index) {
        this.ledger_index = ledger_index;
    }
}
