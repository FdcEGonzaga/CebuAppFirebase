package com.example.cebuapp.controllers.Admin.ManageJobFields;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageJobPosts.ManageJobsPostsActivity;
import com.example.cebuapp.model.JobFields;
import com.example.cebuapp.controllers.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageJobsFieldsActivity extends AppCompatActivity {
    private TextView jf_isEmptyList, jf_title_label;
    private LinearLayout add_edit_form;
    private Button jf_action_btn, cancel_btn;
    private EditText jf_title_input;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ManageJobFieldsRVAdapter adapter;
    private boolean isLoadMore = false;
    private String key = null;
    private CRUDManageJobFields crudJobFields;
    private ProgressDialog dialog;
    private ImageButton backBtn, addNewBtn;
    private Intent intent;
    private JobFields editJobFieldTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_job_fields);

        if (isConnectedToInternet()) {
            // cast components
            castComponents();
            setListeners();
            loadData();

            // check intent extra for editing job field
            editJobFieldTitle = (JobFields) getIntent().getSerializableExtra("jobFieldEdit");
            if (editJobFieldTitle != null) {
                jf_title_input.setText(editJobFieldTitle.getJobFieldTitle());
                jf_title_input.requestFocus();
                jf_title_label.setText("Editing Job Field Title:");
                jf_action_btn.setText("EDIT");
                // hide form and show data
                add_edit_form.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
                // hide main btns
                addNewBtn.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            HelperUtilities.showNoInternetAlert(ManageJobsFieldsActivity.this);
        }
    }

    private void setListeners() {
        // add new btn
        addNewBtn.setOnClickListener(v -> {
            // hide main btns
            addNewBtn.setVisibility(View.INVISIBLE);
            backBtn.setVisibility(View.INVISIBLE);
            // show form and hide rv
            add_edit_form.setVisibility(View.VISIBLE);
            jf_title_label.setText("Add a Job Field Title:");
            jf_title_input.setText("");
            jf_title_input.requestFocus();
            jf_action_btn.setText("ADD");
        });

        // cancel btn
        cancel_btn.setOnClickListener(v -> {
            // show main btns
            addNewBtn.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.VISIBLE);
            // hide form and show rv
            add_edit_form.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        });

        // back to home btn
        backBtn.setOnClickListener(v-> {
            intent = new Intent(new Intent(ManageJobsFieldsActivity.this, HomeActivity.class));
            intent.putExtra("isFromMngJobPostActivity", true);
            startActivity(intent);
            finish();
        });

        // check recyclerview contents
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItem = linearLayoutManager.getItemCount();
                int lastVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (totalItem < lastVisible+3) {
                    if (!isLoadMore) {
                        isLoadMore = true;
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // job field add/edit btn is clicked
        jf_action_btn.setOnClickListener(v -> {
            // get value
            String jobFieldTitleVal = jf_title_input.getText().toString().trim().toUpperCase();

            // construct data
            JobFields jobFieldData = new JobFields(jobFieldTitleVal);

            // validate input
            if (jobFieldTitleVal.isEmpty()) {
                jf_title_input.setError("Job Field Title is required.");
                jf_title_input.requestFocus();
                return;
            }

            // update JobFieldTitle
            if (editJobFieldTitle != null && jf_action_btn.getText().equals("EDIT")) {
                // check if  job field title was edited
                if (jobFieldTitleVal.equals(editJobFieldTitle.getJobFieldTitle().trim())) {
                    jf_title_input.setError("Please make changes with the Job Field Title.");
                    jf_title_input.requestFocus();
                    return;
                }

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("jobFieldTitle", jobFieldTitleVal);
                crudJobFields.updateJobFields(editJobFieldTitle.getKey(), hashMap).addOnSuccessListener(suc -> {
                    Toast.makeText(ManageJobsFieldsActivity.this,
                            "Job Field Title was updated successfully!", Toast.LENGTH_SHORT).show();
                    // reset to adding form
                    jf_title_label.setText("Add a Job Field Title:");
                    jf_title_input.setText("");
                    jf_action_btn.setText("Add");
                    jf_title_input.clearFocus();
                    // hide form
                    add_edit_form.setVisibility(View.GONE);
                    // show main btns
                    addNewBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);

                }).addOnFailureListener(fail -> {
                    Toast.makeText(ManageJobsFieldsActivity.this,
                            "Job Field Title updating failed, please try again.", Toast.LENGTH_LONG).show();
                });

                // insert or add new prov
            } else {
                crudJobFields.addJobFields(jobFieldData).addOnSuccessListener(suc -> {
                    Toast.makeText(ManageJobsFieldsActivity.this,
                            "Job Field Title was added successfully!", Toast.LENGTH_SHORT).show();
                    jf_title_input.setText("");
                    // hide form
                    add_edit_form.setVisibility(View.GONE);
                    // show main btns
                    addNewBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);

                }).addOnFailureListener(fail -> {
                    Toast.makeText(ManageJobsFieldsActivity.this,
                            "Job Field Title adding failed, please try again.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void castComponents() {
        jf_title_label = findViewById(R.id.jf_title_label);
        jf_title_input = findViewById(R.id.jf_title_input);
        jf_action_btn = findViewById(R.id.jf_action_btn);
        jf_isEmptyList = findViewById(R.id.jf_is_empty_list);
        swipeRefreshLayout = findViewById(R.id.jf_swipe_refresh_layout);
        recyclerView = findViewById(R.id.jf_rv);

        // form
        add_edit_form = findViewById(R.id.add_edit_form);

        // btns
        addNewBtn = findViewById(R.id.add_new_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
        backBtn = findViewById(R.id.back_btn);

        // init firebase DB
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("cebuapp_job_fields");

        // load crudJobFields
        crudJobFields = new CRUDManageJobFields();
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        // showing the recycler view
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ManageJobFieldsRVAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        dialog.setTitle("Fetching all Cebu Job Field Title.");
        dialog.show();
        crudJobFields.getJobFieldsData(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<JobFields> jobFieldsList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    JobFields manageJobFields = data.getValue(JobFields.class);

                    // set data
                    manageJobFields.setKey(data.getKey());

                    // append to list
                    jobFieldsList.add(manageJobFields);
                    key = data.getKey();
                }

                if (jobFieldsList.isEmpty() == true) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    jf_isEmptyList.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(jobFieldsList);
                    adapter.notifyDataSetChanged();
                    isLoadMore = false;
                    swipeRefreshLayout.setRefreshing(false);
                    jf_isEmptyList.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(new Intent(ManageJobsFieldsActivity.this, HomeActivity.class));
        intent.putExtra("isFromMngJobPostActivity", true);
        startActivity(intent);
        finish();
    }
}