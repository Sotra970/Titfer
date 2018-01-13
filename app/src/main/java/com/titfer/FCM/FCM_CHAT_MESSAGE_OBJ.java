package com.titfer.FCM;

import com.titfer.Models.MessageModel;

/**
 * Created by lenovo on 7/1/2017.
 */



public class FCM_CHAT_MESSAGE_OBJ {


    String to ;
    MessageModel data ;

    public FCM_CHAT_MESSAGE_OBJ(String to, MessageModel data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public MessageModel getData() {
        return data;
    }

    public void setData(MessageModel data) {
        this.data = data;
    }
}


