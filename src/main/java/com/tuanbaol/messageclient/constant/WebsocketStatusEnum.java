package com.tuanbaol.messageclient.constant;

public enum WebsocketStatusEnum {
    CONNECTED(0, "连接成功"), DISCONNECTED(1, "连接断开");
    private Integer code;
    private String name;


    WebsocketStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }
    public String getName() {
        return name;
    }

}
