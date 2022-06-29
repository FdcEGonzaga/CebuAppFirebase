package com.example.cebuapp.Helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cebuapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataFetcher {
    private static User userName;
    private static DatabaseReference dbRef;
    private static String fullName, email;

    public static String getUserName(String userId) {
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("cebuapp_users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        if (userId.equals(ds.getKey())) {
                            fullName = (String) ds.child("fullname").getValue();
                        }
                    }
                } else {
                    Log.e("Error:", "Error getting data", task.getException());
                }
            }
        });
        return fullName;
    }
}
