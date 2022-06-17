package com.example.cebuapp.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class TouristSpot implements Serializable {
    @Exclude
    private String key;
    private String touristSpotAddress;
    private String touristSpotContactEmail;
    private String touristSpotContactNum;
    private String touristSpotDescription;
    private String touristSpotPostedDate;

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

    public String getTouristSpotPostedDate() {
        return touristSpotPostedDate;
    }

    public void setTouristSpotPostedDate(String touristSpotPostedDate) {
        this.touristSpotPostedDate = touristSpotPostedDate;
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

    private String touristSpotProvince;
    private String touristSpotTitle;

    public TouristSpot() {}
    public TouristSpot(String touristSpotAddress, String touristSpotContactEmail, String touristSpotContactNum, String touristSpotDescription,
                       String touristSpotPostedDate, String touristSpotProvince, String touristSpotTitle) {
        this.touristSpotAddress = touristSpotAddress;
        this.touristSpotContactEmail = touristSpotContactEmail;
        this.touristSpotContactNum = touristSpotContactNum;
        this.touristSpotDescription = touristSpotDescription;
        this.touristSpotPostedDate = touristSpotPostedDate;
        this.touristSpotProvince = touristSpotProvince;
        this.touristSpotTitle = touristSpotTitle;
    }

}
