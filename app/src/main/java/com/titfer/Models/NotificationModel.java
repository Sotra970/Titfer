package com.titfer.Models;

import java.io.Serializable;

/**
 * Created by sotra on 9/9/2017.
 */

public class NotificationModel implements Serializable {
    public NotificationModel() {
    }
    String  message  , date ;
    String action , user_id  , key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public NotificationModel(String message, String date) {
        this.message = message;
        this.date = date;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
