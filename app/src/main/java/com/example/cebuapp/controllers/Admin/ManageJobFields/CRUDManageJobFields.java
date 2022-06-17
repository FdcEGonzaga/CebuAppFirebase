package com.example.cebuapp.controllers.Admin.ManageJobFields;

import com.example.cebuapp.model.JobFields;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class CRUDManageJobFields {
    private DatabaseReference databaseReference;

    public CRUDManageJobFields() {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        databaseReference = fdb.getReference("cebuapp_job_fields");
    }

    public Query getJobFieldsData(String key) {
        return databaseReference.orderByKey();
    }

    public Task<Void> addJobFields(JobFields jobs) {
        return databaseReference.push().setValue(jobs);
    }

    public Task<Void> updateJobFields(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> removeJobFields(String key) {
        return databaseReference.child(key).removeValue();
    }
}
