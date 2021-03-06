package com.example.cebuapp.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class FoodArea implements Serializable {
    @Exclude
    private String key;
    private Boolean approved;
    private String foodImg;
    private String foodAuthor;
    private String foodAddress;
    private String foodContactEmail;
    private String foodContactNum;
    private String foodDescription;
    private String foodPosted;
    private String foodProvince;
    private String foodTitle;

    public FoodArea() {}
    public FoodArea(Boolean approved, String foodAuthor, String foodImg, String foodAddress, String foodContactEmail, String foodContactNum,
                    String foodDescription, String foodPosted, String foodProvince, String foodTitle) {
        this.approved = approved;
        this.foodAuthor = foodAuthor;
        this.foodImg = foodImg;
        this.foodAddress = foodAddress;
        this.foodContactEmail = foodContactEmail;
        this.foodContactNum = foodContactNum;
        this.foodDescription = foodDescription;
        this.foodPosted = foodPosted;
        this.foodProvince = foodProvince;
        this.foodTitle = foodTitle;
    }

    public String getFoodAuthor() {
        return foodAuthor;
    }

    public void setFoodAuthor(String foodAuthor) {
        this.foodAuthor = foodAuthor;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getFoodImg() {
        return foodImg;
    }

    public void setFoodImg(String foodImg) {
        this.foodImg = foodImg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFoodAddress() {
        return foodAddress;
    }

    public void setFoodAddress(String foodAddress) {
        this.foodAddress = foodAddress;
    }

    public String getFoodContactEmail() {
        return foodContactEmail;
    }

    public void setFoodContactEmail(String foodContactEmail) {
        this.foodContactEmail = foodContactEmail;
    }

    public String getFoodContactNum() {
        return foodContactNum;
    }

    public void setFoodContactNum(String foodContactNum) {
        this.foodContactNum = foodContactNum;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getFoodPosted() {
        return foodPosted;
    }

    public void setFoodPosted(String foodPosted) {
        this.foodPosted = foodPosted;
    }

    public String getFoodProvince() {
        return foodProvince;
    }

    public void setFoodProvince(String foodProvince) {
        this.foodProvince = foodProvince;
    }

    public String getFoodTitle() {
        return foodTitle;
    }

    public void setFoodTitle(String foodTitle) {
        this.foodTitle = foodTitle;
    }
}
