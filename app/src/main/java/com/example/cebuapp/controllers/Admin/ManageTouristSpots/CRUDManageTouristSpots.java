package com.example.cebuapp.controllers.Admin.ManageTouristSpots;

import com.example.cebuapp.model.TouristSpot;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class CRUDManageTouristSpots {
    private DatabaseReference databaseReference;

    public CRUDManageTouristSpots() {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        databaseReference = fdb.getReference("cebuapp_tourist_spots");
    }

    public Query getAll(String key) {
        return databaseReference.orderByKey();
    }

    public Query getSearchedWord(String searchWord) {
        return databaseReference.orderByChild("touristSpotTitle").startAt(searchWord).endAt(searchWord + "\uf8ff");
    }

    public Query getAllApproved() {
        return databaseReference.orderByChild("approved").equalTo(true);
    }

    public Task<Void> add(TouristSpot touristSpots) {
        return databaseReference.push().setValue(touristSpots);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> delete(String key) {
        return databaseReference.child(key).removeValue();
    }
}
