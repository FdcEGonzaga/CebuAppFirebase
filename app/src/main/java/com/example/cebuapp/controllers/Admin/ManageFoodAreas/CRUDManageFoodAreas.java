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

    public Query getAll(String key) {
        return databaseReference.orderByKey();
    }

    public Query getSearchedWord(String searchWord) {
        return databaseReference.orderByChild("foodTitle").startAt(searchWord).endAt(searchWord + "\uf8ff");
    }

    public Query getAllApproved() {
        return databaseReference.orderByChild("approved").equalTo(true);
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
