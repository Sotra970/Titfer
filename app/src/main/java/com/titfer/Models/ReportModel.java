package com.titfer.Models;

import java.io.Serializable;

/**
 * Created by sotra on 7/13/2017.
 */

public class ReportModel implements Serializable {
    String problem  ;
    String user_id ;
    UserModel userModel;
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ReportModel() {
    }

    public ReportModel(String problem, String user_id) {
        this.problem = problem;
        this.user_id = user_id;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
