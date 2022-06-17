package com.example.cebuapp.controllers.Admin.ManageFoodAreas;

import com.example.cebuapp.model.FoodArea;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class CRUDManageFoodAreas {
    private DatabaseReference databaseReference;

    public CRUDManageFoodAreas() {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        databaseReference = fdb.getReference("cebuapp_food_areas");
    }

    public Query get(String key) {
        return databaseReference.orderByKey();
    }

    public Query getByProvince(String provinceName) {
        return databaseReference.orderByChild("foodProvince").equalTo(provinceName);
    }

    public Query getAllApproved(Boolean isApproved) {
        return databaseReference.orderByChild("foodApproved").equalTo(isApproved);
    }

    public Task<Void> add(FoodArea foodAreas) {
        return databaseReference.push().setValue(foodAreas);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> delete(String key) {
        return databaseReference.child(key).removeValue();
    }
}
