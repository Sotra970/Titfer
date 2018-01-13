package com.titfer.Models;

import java.io.Serializable;

/**
 * Created by sotra on 9/7/2017.
 */

public class CartModel implements Serializable {
    String id , price , designer_name , customer_name  , date  , title , img ,  designer_key   , consumer_key , state= "created"  ;
    public  CartModel(String id , String title  , String  img  , String  consumer_key , String designer_key  , String designer_name ,String customer_name ){
        this.id = id ;
        this.title = title ;
        this.img = img ;
        this.designer_key = designer_key ;
        this.designer_name = designer_name ;
        this.consumer_key = consumer_key ;
        this.customer_name = customer_name ;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public CartModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDesigner_key() {
        return designer_key;
    }

    public void setDesigner_key(String designer_key) {
        this.designer_key = designer_key;
    }

    public String getConsumer_key() {
        return consumer_key;
    }

    public void setConsumer_key(String consumer_key) {
        this.consumer_key = consumer_key;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDesigner_name() {
        return designer_name;
    }

    public void setDesigner_name(String designer_name) {
        this.designer_name = designer_name;
    }
}
