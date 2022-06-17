package com.example.cebuapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;


public class Province implements Serializable {

    @Exclude
    private String key;
    private String provinceName;

    public Province() {}

    public Province(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
