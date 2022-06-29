package com.example.cebuapp.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class TouristSpot implements Serializable {
    @Exclude
    private String key;
    private Boolean approved;
    private String touristAuthor;
    private String touristSpotImg;
    private String touristSpotAddress;
    private String touristSpotContactEmail;
    private String touristSpotContactNum;
    private String touristSpotDescription;
    private String touristSpotPosted;
    private String touristSpotProvince;
    private String touristSpotTitle;
    private Integer pos;

    public TouristSpot() {}
    public TouristSpot(Boolean approved, String touristAuthor, String touristSpotImg, String touristSpotAddress, String touristSpotContactEmail,
                       String touristSpotContactNum, String touristSpotDescription, String touristSpotPosted, String touristSpotProvince,
                       String touristSpotTitle, Integer pos) {
        this.approved = approved;
        this.touristAuthor = touristAuthor;
        this.touristSpotImg = touristSpotImg;
        this.touristSpotAddress = touristSpotAddress;
        this.touristSpotContactEmail = touristSpotContactEmail;
        this.touristSpotContactNum = touristSpotContactNum;
        this.touristSpotDescription = touristSpotDescription;
        this.touristSpotPosted = touristSpotPosted;
        this.touristSpotProvince = touristSpotProvince;
        this.touristSpotTitle = touristSpotTitle;
        this.pos = pos;
    }

    public String getTouristAuthor() {
        return touristAuthor;
    }

    public void setTouristAuthor(String touristAuthor) {
        this.touristAuthor = touristAuthor;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getTouristSpotImg() {
        return touristSpotImg;
    }

    public void setTouristSpotImg(String touristSpotImg) {
        this.touristSpotImg = touristSpotImg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTouristSpotAddress() {
        return touristSpotAddress;
    }

    public void setTouristSpotAddress(String touristSpotAddress) {
        this.touristSpotAddress = touristSpotAddress;
    }

    public String getTouristSpotContactEmail() {
        return touristSpotContactEmail;
    }

    public void setTouristSpotContactEmail(String touristSpotContactEmail) {
        this.touristSpotContactEmail = touristSpotContactEmail;
    }

    public String getTouristSpotContactNum() {
        return touristSpotContactNum;
    }

    public void setTouristSpotContactNum(String touristSpotContactNum) {
        this.touristSpotContactNum = touristSpotContactNum;
    }

    public String getTouristSpotDescription() {
        return touristSpotDescription;
    }

    public void setTouristSpotDescription(String touristSpotDescription) {
        this.touristSpotDescription = touristSpotDescription;
    }

    public String getTouristSpotPosted() {
        return touristSpotPosted;
    }

    public void setTouristSpotPosted(String touristSpotPosted) {
        this.touristSpotPosted = touristSpotPosted;
    }

    public String getTouristSpotProvince() {
        return touristSpotProvince;
    }

    public void setTouristSpotProvince(String touristSpotProvince) {
        this.touristSpotProvince = touristSpotProvince;
    }

    public String getTouristSpotTitle() {
        return touristSpotTitle;
    }

    public void setTouristSpotTitle(String touristSpotTitle) {
        this.touristSpotTitle = touristSpotTitle;
    }
}
