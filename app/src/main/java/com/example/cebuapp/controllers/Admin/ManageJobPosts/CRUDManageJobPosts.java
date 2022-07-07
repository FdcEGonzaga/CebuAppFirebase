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

    public Query getSearchedWord(String searchWord) {
        return databaseReference.orderByChild("jobPostTitle")
                .startAt(searchWord).endAt(searchWord + "\uf8ff");
    }

    public Query getAllApproved(Boolean isApproved) {
        return databaseReference.orderByChild("approved")
                .equalTo(isApproved);
    }

    public Query getOwnedJobPosts(String userEmail) {
        return databaseReference.orderByChild("jobAuthor")
                .equalTo(userEmail);
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
