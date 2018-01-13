package com.titfer.Models;

import java.io.Serializable;

/**
 * Created by Moza on 26-Jul-17.
 */

public class UserModel implements Serializable {

    String firstName, lastName , country , email, phone, password, brandName, profession, specialty1, specialty2,specialty_search_key ,  id, profilePic;
    String uid ;
    int type;
    String bio = "A brief bio about the user" ;
    public boolean verified = false;
    public String followed ;

    public  boolean is_verified(){
        return  verified;
    }
    public  boolean is_followed(){
        return  followed == null ? false : true ;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    // empty constructor
    public UserModel() {}

    // customer constructor
    public UserModel(String firstName, String lastName, String email, String phone, String password, int typeId, String profilePic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.type = typeId;
        this.profilePic = profilePic;
    }


    // professional constructor
    public UserModel(String firstName, String lastName, String email, String phone, String password, String brandName, String profession, String specialty1, String specialty2, String specialty_search_key , int typeId, String profilePic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.brandName = brandName;
        this.profession = profession;
        this.specialty1 = specialty1;
        this.specialty2 = specialty2;
        this.specialty_search_key = specialty_search_key;
        this.type = typeId;
        this.profilePic = profilePic;
    }

    public String getFirstName() {
        return firstName  == null ? "" : firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName == null ? "" :lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBrandName() {
        return brandName == null ? "" : brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProfession() {
        return profession == null ? "" : profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getSpecialty1() {
        return specialty1 == null ? "" : specialty1;
    }

    public void setSpecialty1(String specialty1) {
        this.specialty1 = specialty1;
    }

    public String getSpecialty2() {
        return specialty2 == null ? "" : specialty2;
    }

    public void setSpecialty2(String specialty2) {
        this.specialty2 = specialty2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}


