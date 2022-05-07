package com.samuilolegovich.model.sockets;

import java.io.Serializable;

public class MyFirstRequestDto implements Serializable {
    private String id = "my_first_request";
    private String command = "server_info";
    private int api_version =  1;

    public MyFirstRequestDto(String id, String command, int api_version) {
        this.id = id;
        this.command = command;
        this.api_version = api_version;
    }

    public MyFirstRequestDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getApi_version() {
        return api_version;
    }

    public void setApi_version(int api_version) {
        this.api_version = api_version;
    }
}
