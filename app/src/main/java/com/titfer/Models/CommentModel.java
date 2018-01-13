package com.titfer.Models;

import java.io.Serializable;

/**
 * Created by sotra on 7/13/2017.
 */

public class CommentModel implements Serializable {
    String comment ;
    String img ;
    String user_name ;
    String id ;

    public CommentModel() {
    }

    public CommentModel(String comment, String img , String user_name) {
        this.user_name = user_name;
        this.comment = comment;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
