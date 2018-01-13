package com.titfer.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sotra on 7/14/2017.
 */

public class DesignerSearchModel implements Serializable {
    UserModel userModel;
    ArrayList<AlbumModel > albumModels ;

    public DesignerSearchModel(UserModel userModel, ArrayList<AlbumModel> albumModels) {
        this.userModel = userModel;
        this.albumModels = albumModels;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public ArrayList<AlbumModel> getAlbumModels() {
        return albumModels;
    }

    public void setAlbumModels(ArrayList<AlbumModel> albumModels) {
        this.albumModels = albumModels;
    }
}
