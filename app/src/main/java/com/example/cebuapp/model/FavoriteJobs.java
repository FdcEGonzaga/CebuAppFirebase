package com.example.cebuapp.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class FavoriteJobs implements Serializable {

    @Exclude
    private String key;
    private String postCategory;
    private String postCompanyImg;
    private String postCompanyName;
    private String postTitle;
    private String postYearExp;
    private String postSalary;
    private String postDate;

    public FavoriteJobs() {}
    public FavoriteJobs(String postCategory , String postCompanyImg, String postCompanyName, String postTitle, String postYearExp, String postSalary, String postDate) {
        this.postCategory = postCategory;
        this.postCompanyImg = postCompanyImg;
        this.postCompanyName = postCompanyName;
        this.postTitle = postTitle;
        this.postYearExp = postYearExp;
        this.postSalary = postSalary;
        this.postDate = postDate;
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

    public String getPostCompanyImg() {
        return postCompanyImg;
    }

    public void setPostCompanyImg(String postCompanyImg) {
        this.postCompanyImg = postCompanyImg;
    }

    public String getPostCompanyName() {
        return postCompanyName;
    }

    public void setPostCompanyName(String postCompanyName) {
        this.postCompanyName = postCompanyName;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostYearExp() {
        return postYearExp;
    }

    public void setPostYearExp(String postYearExp) {
        this.postYearExp = postYearExp;
    }

    public String getPostSalary() {
        return postSalary;
    }

    public void setPostSalary(String postSalary) {
        this.postSalary = postSalary;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }
}
