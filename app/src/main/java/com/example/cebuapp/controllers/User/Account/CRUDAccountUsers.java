package com.example.cebuapp.controllers.User.Account;

import com.example.cebuapp.model.FoodArea;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class CRUDAccountUsers {
    private DatabaseReference databaseReference;

    public CRUDAccountUsers() {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        databaseReference = fdb.getReference("cebuapp_users");
    }

    public Query get(String key) {
        return databaseReference.orderByKey();
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
