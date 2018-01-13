package com.titfer.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sotra on 7/13/2017.
 */

public class EventsModel implements Serializable {
    String title ;
    String date ;
    String desc ;
    String id ;
    ArrayList<String> imgs ;

    public EventsModel() {
    }

    public EventsModel(String title, String desc, String date ,  ArrayList<String> imgs) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.imgs = imgs;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }
}
