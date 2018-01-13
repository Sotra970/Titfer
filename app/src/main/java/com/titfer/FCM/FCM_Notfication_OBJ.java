package com.titfer.FCM;

import com.titfer.Models.MessageModel;
import com.titfer.Models.NotificationModel;

/**
 * Created by lenovo on 7/1/2017.
 */



public class FCM_Notfication_OBJ {


    String to ;
    NotificationModel data ;

    public FCM_Notfication_OBJ(String to, NotificationModel data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationModel getData() {
        return data;
    }

    public void setData(NotificationModel data) {
        this.data = data;
    }
}


