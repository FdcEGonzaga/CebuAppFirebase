package com.example.cebuapp.controllers.Admin.ManageProvinces;

import com.example.cebuapp.model.Province;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class CRUDManageProvinces {
    private DatabaseReference databaseReference;

    public CRUDManageProvinces() {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        databaseReference = fdb.getReference("cebuapp_provinces");
    }

    public Query getProvinceData(String key) {
        return databaseReference.orderByKey();
    }

    public Task<Void> addProvince(Province province) {
        return databaseReference.push().setValue(province);
    }

    public Task<Void> updateProvinceName(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> removeProvinceName(String key) {
        return databaseReference.child(key).removeValue();
    }

}
