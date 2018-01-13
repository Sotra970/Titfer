package com.titfer.Models;

import java.io.Serializable;

/**
 * Created by sotra on 7/13/2017.
 */

public class CatModel implements Serializable {
    String name ;
    int img ;

    public CatModel(String name, int img) {
        this.name = name;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
