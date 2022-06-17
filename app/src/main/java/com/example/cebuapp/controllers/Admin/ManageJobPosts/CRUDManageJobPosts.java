package com.example.cebuapp.controllers.Admin.ManageJobPosts;

import com.example.cebuapp.model.JobPosts;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class CRUDManageJobPosts {
    private DatabaseReference databaseReference;

    public CRUDManageJobPosts() {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        databaseReference = fdb.getReference("cebuapp_job_posts");
    }

    public Query getAll(String key) {
        return databaseReference.orderByKey();
    }

    public Query getByCategory(String category) {
        return databaseReference.orderByChild("jobPostJobField").equalTo(category);
    }

    public Query getAllApproved(Boolean isApproved) {
        return databaseReference.orderByChild("jobPostApproved").equalTo(isApproved);
    }

    public Task<Void> add(JobPosts jobs) {
        return databaseReference.push().setValue(jobs);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> delete(String key) {
        return databaseReference.child(key).removeValue();
    }
}
