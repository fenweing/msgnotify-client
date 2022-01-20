package com.tuanbaol.messageclient.bean;

import lombok.Data;

@Data
public class Message {
    private String title;
    private String ticker;
    private String text;
    private String srcPack;
    private String time;
    private String body;

}
