package com.example.cebuapp.model;

import android.os.Parcel;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class JobFields implements Serializable {

    @Exclude
    private String key;
    private String jobFieldTitle;

    public JobFields() {}

    public JobFields(String jobFieldTitle) {
        this.jobFieldTitle = jobFieldTitle;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJobFieldTitle() {
        return jobFieldTitle;
    }

    public void setJobFieldTitle(String jobFieldTitle) {
        this.jobFieldTitle = jobFieldTitle;
    }
}
