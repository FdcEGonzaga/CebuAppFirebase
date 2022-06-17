package com.example.cebuapp.controllers.Admin.ManageJobPosts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cebuapp.R;
import com.example.cebuapp.model.JobPosts;
import com.example.cebuapp.controllers.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ManageJobsPostsActivity extends AppCompatActivity {
    private TextView form_title, jp_title_label, jp_desc_label, jp_exp_label, jp_salary_label, jp_link_label,
            jp_field_label, jp_address_label, jp_company_label, jp_company_dets_label, jp_is_empty_list;
    private EditText jp_title_input, jp_desc_input, jp_exp_input, jp_salary_input, jp_link_input, jp_company_input,
            jp_company_dets_input;
    private Spinner jp_field_spinner, jp_address_spinner;

    // form
    private ScrollView add_edit_form;
    private Button jp_action_btn, cancel_btn;

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ManageJobPostsRVAdapter adapter;
    private boolean isLoadMore = false;
    private String key = null;
    private CRUDManageJobPosts crudJobPosts;
    private SwipeRefreshLayout swipeRefreshLayout;

    // job field spinnner
    private Spinner jobFieldSpinner;
    private DatabaseReference jobFieldSpinnerRef;
    private ArrayList<String> jobFieldSpinnerList;
    private ArrayAdapter<String> jobFieldSpinnerAdapter;

    // province spinner
    private Spinner provinceSpinner;
    private DatabaseReference provinceSpinnerRef;
    private ArrayList<String> provinceSpinnerList;
    private ArrayAdapter<String> provinceSpinnerAdapter;

    private ImageButton backBtn, addNewBtn;
    private Intent intent;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_job_posts);

        // cast components
        castComponents();

        // spinner
        spinnerComponents();
        setSpinner();

        // init firebase DB
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("cebuapp_job_posts");

        // load crudJobPosts
        crudJobPosts = new CRUDManageJobPosts();
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        // showing the recycler view
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ManageJobPostsRVAdapter(this);
        recyclerView.setAdapter(adapter);
        loadData();

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

        // check intent extra for editing job field
        JobPosts editJobPost = (JobPosts) getIntent().getSerializableExtra("jobFieldEdit");
        if (editJobPost != null) {
            // show form and hide rv
            add_edit_form.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            // hide main btns
            addNewBtn.setVisibility(View.INVISIBLE);
            backBtn.setVisibility(View.INVISIBLE);

            // reuse form
            form_title.setText("Editing a Job Post");
            jp_title_input.setText(editJobPost.getJobPostTitle());
            jp_desc_input.setText(editJobPost.getJobPostDescription());
            jp_exp_input.setText(editJobPost.getJobPostYearExp());
            jp_salary_input.setText(editJobPost.getJobPostSalary());
            jp_link_input.setText(editJobPost.getJobPostLink());
            jp_company_input.setText(editJobPost.getJobPostCompany());
            jp_company_dets_input.setText(editJobPost.getJobPostCompanyDetails());
            jp_title_input.requestFocus();
            jp_action_btn.setText("EDIT");
        }

        // job field add/edit btn is clicked
        jp_action_btn.setOnClickListener(v -> {

            // get value
            String jobPostTitleVal = jp_title_input.getText().toString().trim();
            String jobPostDescVal = jp_desc_input.getText().toString().trim();
            String jobPostExpVal = jp_exp_input.getText().toString().trim();
            String jobPostSalaryVal = jp_salary_input.getText().toString().trim();
            String jobPostLinkVal = jp_link_input.getText().toString().trim();
            String jobPostCompanyVal = jp_company_input.getText().toString().trim();
            String jobPostCompanyDetsVal = jp_company_dets_input.getText().toString().trim();
            String jobPostFieldsVal = jobFieldSpinner.getSelectedItem().toString().trim();
            String jobPostProvinceVal = provinceSpinner.getSelectedItem().toString().trim();

            // validate inputs
            if (jobPostTitleVal.isEmpty()) {
                jp_title_input.setError("Job Title is required.");
                jp_title_input.requestFocus();
                return;
            }

            if (jobPostDescVal.isEmpty()) {
                jp_desc_input.setError("Job Description is required.");
                jp_desc_input.requestFocus();
                return;
            }

            if (jobPostExpVal.isEmpty()) {
                jp_exp_input.setError("Job Experience is required.");
                jp_exp_input.requestFocus();
                return;
            }

            if (jobPostSalaryVal.isEmpty()) {
                jp_salary_input.setError("Job salary offer is required.");
                jp_salary_input.requestFocus();
                return;
            }

            if (jobPostLinkVal.isEmpty()) {
                jp_link_input.setError("Job link is required.");
                jp_link_input.requestFocus();
                return;
            }

            if (jobPostCompanyVal.isEmpty()) {
                jp_company_input.setError("Company name is required.");
                jp_company_input.requestFocus();
                return;
            }

            if (jobPostCompanyDetsVal.isEmpty()) {
                jp_company_dets_input.setError("Company description is required.");
                jp_company_dets_input.requestFocus();
                return;
            }

            // construct data
            String jobPostPostedVal = getCurrentDate();
            JobPosts jobPostsData = new JobPosts(jobPostTitleVal, jobPostDescVal, jobPostPostedVal, jobPostExpVal,
                    jobPostSalaryVal, jobPostProvinceVal, jobPostCompanyVal, jobPostCompanyDetsVal, jobPostFieldsVal, jobPostLinkVal);

            // update Job post
            if (editJobPost != null && jp_action_btn.getText().equals("EDIT")) {
                // check if  job field title was edited
                if (jobPostTitleVal.equals(editJobPost.getJobPostJobField().trim())) {
                    jp_title_input.setError("Please update the Job Post field/type.");
                    jp_title_input.requestFocus();
                    return;
                }

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("jobPostTitle", jobPostTitleVal);
                hashMap.put("jobPostImg", "https://thumbs.dreamstime.com/b/job-opportunity-24549521.jpg");
                hashMap.put("jobPostDescription", jobPostDescVal);
                hashMap.put("jobPostPosted", jobPostPostedVal);
                hashMap.put("jobPostYearExp", jobPostExpVal);
                hashMap.put("jobPostSalary", jobPostSalaryVal);
                hashMap.put("jobPostCompany", jobPostCompanyVal);
                hashMap.put("jobPostCompanyDetails", jobPostCompanyDetsVal);
                hashMap.put("jobPostJobField", jobPostFieldsVal);
                hashMap.put("jobPostProvince", jobPostProvinceVal);
                hashMap.put("jobPostLink", jobPostLinkVal);
                crudJobPosts.update(editJobPost.getKey(), hashMap).addOnSuccessListener(suc -> {
                    Toast.makeText(ManageJobsPostsActivity.this,
                            "Job post was updated successfully!", Toast.LENGTH_SHORT).show();
                    resetFormInputs();
                    // show main btns
                    addNewBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);
                    // hide form and show newly updated rv
                    add_edit_form.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);

                }).addOnFailureListener(fail -> {
                    Toast.makeText(ManageJobsPostsActivity.this,
                            "Job Field Title updating failed, please try again.", Toast.LENGTH_LONG).show();
                });

            // insert or add new prov
            } else {
                crudJobPosts.add(jobPostsData).addOnSuccessListener(suc -> {
                    Toast.makeText(ManageJobsPostsActivity.this,
                            "Job Field Title was added successfully!", Toast.LENGTH_SHORT).show();
                    jp_title_input.setText("");
                    // show main btns
                    addNewBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);

                }).addOnFailureListener(fail -> {
                    Toast.makeText(ManageJobsPostsActivity.this,
                            "Job Field Title adding failed, please try again.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void castComponents() {
        // form container and form title
        add_edit_form = findViewById(R.id.add_edit_form);
        form_title = findViewById(R.id.form_title);

        // form labels
        jp_title_label = findViewById(R.id.jp_title_label);
        jp_desc_label = findViewById(R.id.jp_desc_label);
        jp_exp_label = findViewById(R.id.jp_exp_label);
        jp_salary_label = findViewById(R.id.jp_salary_label);
        jp_link_label = findViewById(R.id.jp_link_label);
        jp_field_label = findViewById(R.id.jp_field_label);
        jp_address_label = findViewById(R.id.jp_address_label);
        jp_company_label = findViewById(R.id.jp_company_label);
        jp_company_dets_label = findViewById(R.id.jp_company_dets_label);

        // form inputs
        jp_title_input = findViewById(R.id.jp_title_input);
        jp_desc_input = findViewById(R.id.jp_desc_input);
        jp_exp_input = findViewById(R.id.jp_exp_input);
        jp_salary_input = findViewById(R.id.jp_salary_input);
        jp_link_input = findViewById(R.id.jp_link_input);
        jp_field_spinner = findViewById(R.id.jp_field_spinner);
        jp_address_spinner = findViewById(R.id.jp_address_spinner);
        jp_company_input = findViewById(R.id.jp_company_input);
        jp_company_dets_input = findViewById(R.id.jp_company_dets_input);

        // others
        jp_action_btn = findViewById(R.id.jp_action_btn);
        jp_is_empty_list = findViewById(R.id.jp_is_empty_list);
        swipeRefreshLayout = findViewById(R.id.jp_swipe_refresh_layout);
        recyclerView = findViewById(R.id.jp_rv);

        // btns
        cancel_btn = findViewById(R.id.cancel_btn);
        addNewBtn = findViewById(R.id.add_new_btn);
        backBtn = findViewById(R.id.back_btn);

        // add new btn
        addNewBtn.setOnClickListener(v -> {
            resetFormInputs();
            // hide main btns
            addNewBtn.setVisibility(View.INVISIBLE);
            backBtn.setVisibility(View.INVISIBLE);
            // show form and hide rv
            add_edit_form.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
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
            intent = new Intent(ManageJobsPostsActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void spinnerComponents() {
        // job field spinner
        jobFieldSpinner = findViewById(R.id.jp_field_spinner);
        jobFieldSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_job_fields");
        jobFieldSpinnerList = new ArrayList<>();
        jobFieldSpinnerAdapter = new ArrayAdapter<String>(ManageJobsPostsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, jobFieldSpinnerList);
        jobFieldSpinner.setAdapter(jobFieldSpinnerAdapter);

        // province spinner
        provinceSpinner = findViewById(R.id.jp_address_spinner);
        provinceSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_provinces");
        provinceSpinnerList = new ArrayList<>();
        provinceSpinnerAdapter = new ArrayAdapter<String>(ManageJobsPostsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, provinceSpinnerList);
        provinceSpinner.setAdapter(provinceSpinnerAdapter);

    }

    private void setSpinner() {
        // supply spinner data
        jobFieldSpinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uniqueKey: snapshot.getChildren()) {
                    for(DataSnapshot data : uniqueKey.getChildren()){
                        jobFieldSpinnerList.add(data.getValue().toString());
                    }
                }
                jobFieldSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // supply spinner data
        provinceSpinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uniqueKey: snapshot.getChildren()) {
                    for(DataSnapshot data : uniqueKey.getChildren()){
                        provinceSpinnerList.add(data.getValue().toString());
                    }
                }
                provinceSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getCurrentDate() {
        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return df.format(calendar);
    }

    private void loadData() {
        dialog.setTitle("Fetching all Cebu Job Field Title.");
        dialog.show();
        crudJobPosts.getAll(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<JobPosts> jobFieldsList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    JobPosts manageJobFields = data.getValue(JobPosts.class);

                    // set data
                    manageJobFields.setKey(data.getKey());

                    // append to list
                    jobFieldsList.add(manageJobFields);
                    key = data.getKey();
                }

                if (jobFieldsList.isEmpty() == true) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    jp_is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(jobFieldsList);
                    adapter.notifyDataSetChanged();
                    isLoadMore = false;
                    swipeRefreshLayout.setRefreshing(false);
                    jp_is_empty_list.setVisibility(View.GONE);
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

    private void resetFormInputs() {
        // reset form input
        form_title.setText("Addding a Job Post");
        jp_title_input.setText("");
        jp_title_input.requestFocus();
        jp_desc_input.setText("");
        jp_exp_input.setText("");
        jp_salary_input.setText("");
        jp_link_input.setText("");
        jp_company_input.setText("");
        jp_company_dets_input.setText("");
        jp_action_btn.setText("ADD");
    }

    @Override
    public void onBackPressed() {
        // go back to admin homepage
        Intent intent = new Intent(ManageJobsPostsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}