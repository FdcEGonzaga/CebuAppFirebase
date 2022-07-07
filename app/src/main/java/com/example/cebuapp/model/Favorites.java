package com.example.cebuapp.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Favorites implements Serializable {

    @Exclude
    private String key;
    private String postCategory;
    private String postImg;
    private String postDesc1;
    private String postDesc2;
    private String postDesc3;
    private String postDesc4;

    public Favorites() {}
    public Favorites(String postCategory ,  String postImg, String postDesc1, String postDesc2, String postDesc3, String postDesc4) {
        this.postCategory = postCategory;
        this.postImg = postImg;
        this.postDesc1 = postDesc1;
        this.postDesc2 = postDesc2;
        this.postDesc3 = postDesc3;
        this.postDesc4 = postDesc4;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public String getPostDesc1() {
        return postDesc1;
    }

    public void setPostDesc1(String postDesc1) {
        this.postDesc1 = postDesc1;
    }

    public String getPostDesc2() {
        return postDesc2;
    }

    public void setPostDesc2(String postDesc2) {
        this.postDesc2 = postDesc2;
    }

    public String getPostDesc3() {
        return postDesc3;
    }

    public void setPostDesc3(String postDesc3) {
        this.postDesc3 = postDesc3;
    }

    public String getPostDesc4() {
        return postDesc4;
    }

    public void setPostDesc4(String postDesc4) {
        this.postDesc4 = postDesc4;
    }
}
