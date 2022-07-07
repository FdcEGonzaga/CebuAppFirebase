package com.example.cebuapp.controllers.User.ManageFavorites;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CRUDManageFavorites {
    private DatabaseReference databaseReference;
    private FirebaseDatabase fdb;

    public CRUDManageFavorites() {
        fdb = FirebaseDatabase.getInstance();
    }

    public Query getAllFavorites(String customPath, String postCategory) {
        databaseReference = fdb.getReference(customPath.trim());
        return databaseReference.orderByChild("postCategory")
                .equalTo(postCategory);
    }

    public Query checkIfFavorite(String customPath, String postKey) {
        databaseReference = fdb.getReference(customPath.trim());
        return databaseReference.orderByKey();
    }

    public Task<Void> removeToFavorite(String customPath, String postKey) {
        databaseReference = fdb.getReference(customPath.trim());
        return databaseReference.child(postKey).removeValue();
    }
}
