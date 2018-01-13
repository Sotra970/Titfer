package com.titfer.Models;

import java.io.Serializable;

/**
 * Created by sotra on 9/10/2017.
 */

public class RecomendationModel implements Serializable {
    AlbumModel itemModel ;
    AlbumModel albumModel;
    UserModel userModel;

    public AlbumModel getItemModel() {
        return itemModel;
    }

    public void setItemModel(AlbumModel itemModel) {
        this.itemModel = itemModel;
    }

    public AlbumModel getAlbumModel() {
        return albumModel;
    }

    public void setAlbumModel(AlbumModel albumModel) {
        this.albumModel = albumModel;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
