package com.example.whosin.model.Objects;

import java.io.Serializable;

public class User implements Serializable {
    private String email;
    private String fullName;
    private String phone;
    private String imageUri;
    private String id;

    public User() {
    }

    public User(User u) {
        this.email = u.email;
        this.fullName = u.fullName;
        this.phone =  u.phone;
        this.imageUri = u.imageUri;
    }

    public User(String email, String fullName, String phone,String imageUri , String id) {
        this.email = email;
        this.fullName = fullName;
        this.phone =  phone;
        this.imageUri = imageUri;
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", imageUri='" + imageUri + '\'' +
                "id = " + this.id +'}';
    }

}
