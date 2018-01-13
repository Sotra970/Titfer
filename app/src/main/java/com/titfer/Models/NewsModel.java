package com.titfer.Models;

import java.io.Serializable;

/**
 * Created by sotra on 7/13/2017.
 */

public class NewsModel implements Serializable {
    String title ;
    String desc ;
    String id ;
    String img ;

    public NewsModel() {
    }

    public NewsModel(String title, String desc, String img) {
        this.title = title;
        this.desc = desc;
        this.img = img;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
