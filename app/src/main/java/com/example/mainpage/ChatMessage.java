package com.example.mainpage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {
    public static final Long TYPE_RECEIVED = 0L; //訊息型別-收到的訊息
    public static final Long TYPE_SENT = 1L;     //訊息型別-發出的訊息
    public static final Long TYPE_RECEIVED_REQUEST = 2L;     //訊息型別-發出的訊息
    public static final Long TYPE_SENT_REQUEST = 3L;     //訊息型別-發出的訊息

    private String messageText; //訊息內容
    private String messageTime; //訊息時間
    private Long messageType;   //訊息型別
    private String messageReady; // 已讀、未讀訊息



    public ChatMessage(String messageText, Long messageType) {
        this.messageText = messageText;
        this.messageType = messageType;

        // 獲取現在時間 該時從从1970年1月1日起的毫秒数。
        Date date = new Date();// Mon Jul 15 04:36:18 GMT 2019
        // 設定時間格式
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd kk:mm:ss");

        // 設定時間
        messageTime = ft.format(date);

        // 預設未讀訊息
        messageReady = "0";
    }

    public ChatMessage(){

    }

    public Long getMessageType() {
        return messageType;
    }

    public void setMessageType(Long messageType) {
        this.messageType = messageType;
    }

    public String getMessageText() {
        return messageText.toString();
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageTime() {
        return messageTime.toString();
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageReady(){
        if(messageReady.equals( "已讀"))
            return "已讀";
        else
            return "";
    }

    public void setMessageReady(String messageReady) {
        this.messageReady = messageReady;
    }

}