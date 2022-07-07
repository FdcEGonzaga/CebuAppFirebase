package com.example.cebuapp.controllers.Admin.ManageJobPosts;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cebuapp.Helper.DataFetcher;
import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.User.JobPosts.JobaPostsActivity;
import com.example.cebuapp.controllers.User.TouristSpots.SpotsActivity;
import com.example.cebuapp.model.JobPosts;
import com.example.cebuapp.controllers.HomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class ManageJobsPostsActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    // form
    private TextView form_title, jp_img_label, jp_title_label, jp_desc_label, jp_exp_label, jp_salary_label, jp_link_label,
            jp_field_label, jp_province_label, jp_company_label, jp_company_dets_label, jp_is_empty_list, jp_approve_label;
    private EditText jp_title_input, jp_desc_input, jp_exp_input, jp_salary_input, jp_link_input, jp_company_input,
            jp_company_dets_input;
    private String jobPostImgVal, jobPostTitleVal, jobPostDescVal, jobPostPostedVal, jobPostExpVal, jobPostSalaryVal,
            jobPostProvinceVal, jobPostCompanyVal, jobPostCompanyDetsVal, jobPostFieldsVal, jobPostLinkVal;
    private Boolean jobPostApproveVal, errors;
    private JobPosts jobPostsData;
    private Spinner jp_approve_spinner;
    private ScrollView add_edit_form;
    private Button jp_action_btn, cancel_btn;
    private JobPosts editJobPost;

    // Image uploading
    private Uri imageUri;
    private ImageView jp_img_input;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

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

    private RecyclerView recyclerView;
    private ManageJobPostsRVAdapter adapter;
    private boolean isLoadMore = false;
    private String key = null;
    private CRUDManageJobPosts crudJobPosts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton backBtn, addNewBtn;
    private Intent intent;
    private ProgressDialog dialog;

    // manager
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_job_posts);

        if (isConnectedToInternet()) {
            editJobPost = (JobPosts) getIntent().getSerializableExtra("jobFieldEdit");

            castComponents();
            spinnerComponents();
            setSpinner();
            setListeners();
            loadAllJobPosts();

            // check intent extra for editing job field
            if (editJobPost != null) {
                // show form and hide rv
                add_edit_form.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
                // hide main btns
                addNewBtn.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.INVISIBLE);

                // reuse form
                form_title.setText("Editing a Job Post");
                ShowImageUrl showImageUrl = (ShowImageUrl) new ShowImageUrl((ImageView) findViewById(R.id.jp_img_input))
                        .execute(editJobPost.getJobPostImg());
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
        } else {
            HelperUtilities.showNoInternetAlert(ManageJobsPostsActivity.this);
        }
    }

    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // check recyclerview contents
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                manager.setReverseLayout(true);
                manager.setStackFromEnd(true);
                int totalItem = manager.getItemCount();
                int lastVisible = manager.findLastCompletelyVisibleItemPosition();
                if (totalItem < lastVisible+3) {
                    if (!isLoadMore) {
                        isLoadMore = true;
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // add/edit job post btn is clicked
        jp_action_btn.setOnClickListener(v -> {
            // get values
            jobPostTitleVal = jp_title_input.getText().toString().trim();
            jobPostDescVal = jp_desc_input.getText().toString().trim();
            jobPostExpVal = jp_exp_input.getText().toString().trim();
            jobPostSalaryVal = jp_salary_input.getText().toString().trim();
            jobPostLinkVal = jp_link_input.getText().toString().trim();
            jobPostCompanyVal = jp_company_input.getText().toString().trim();
            jobPostCompanyDetsVal = jp_company_dets_input.getText().toString().trim();
            jobPostFieldsVal = jobFieldSpinner.getSelectedItem().toString().trim();
            jobPostProvinceVal = provinceSpinner.getSelectedItem().toString().trim();
            String isPublish = jp_approve_spinner.getSelectedItem().toString().trim();
            jobPostApproveVal = (isPublish.equals("Publish")) ? true : false;

            // save data ang upload image
            saveDataAndUploadImage(editJobPost);
        });

        jp_img_input.setOnClickListener(v-> {
            choosingOfPicture();
        });

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
            String actionVal = "";
            if (jp_action_btn.getText().equals("ADD")) {
                actionVal = "adding";
            } else {
                actionVal = "editing";
            }
            isCancelAdding(actionVal);
        });

        // back to home btn
        backBtn.setOnClickListener(v-> {
            intent = new Intent(new Intent(ManageJobsPostsActivity.this, HomeActivity.class));
            intent.putExtra("isFromMngJobPostActivity", true);
            startActivity(intent);
            finish();
        });
    }

    private void castComponents() {
        // form labels
        jp_img_label = findViewById(R.id.jp_img_label);
        jp_title_label = findViewById(R.id.jp_title_label);
        jp_desc_label = findViewById(R.id.jp_desc_label);
        jp_exp_label = findViewById(R.id.jp_exp_label);
        jp_salary_label = findViewById(R.id.jp_salary_label);
        jp_link_label = findViewById(R.id.jp_link_label);
        jp_field_label = findViewById(R.id.jp_field_label);
        jp_province_label = findViewById(R.id.jp_province_label);
        jp_company_label = findViewById(R.id.jp_company_label);
        jp_company_dets_label = findViewById(R.id.jp_company_dets_label);
        jp_approve_label = findViewById(R.id.jp_approve_label);

        // form inputs
        jp_img_input = findViewById(R.id.jp_img_input);
        jp_title_input = findViewById(R.id.jp_title_input);
        jp_desc_input = findViewById(R.id.jp_desc_input);
        jp_exp_input = findViewById(R.id.jp_exp_input);
        jp_salary_input = findViewById(R.id.jp_salary_input);
        jp_link_input = findViewById(R.id.jp_link_input);
        jp_company_input = findViewById(R.id.jp_company_input);
        jp_company_dets_input = findViewById(R.id.jp_company_dets_input);
        jobFieldSpinner = findViewById(R.id.jp_field_spinner);
        provinceSpinner = findViewById(R.id.jp_province_spinner);
        jp_approve_spinner = findViewById(R.id.jp_approve_spinner);

        // others
        add_edit_form = findViewById(R.id.add_edit_form);
        form_title = findViewById(R.id.form_title);
        jp_action_btn = findViewById(R.id.jp_action_btn);
        jp_is_empty_list = findViewById(R.id.jp_is_empty_list);
        swipeRefreshLayout = findViewById(R.id.jp_swipe_refresh_layout);
        recyclerView = findViewById(R.id.jp_rv);

        // btns
        cancel_btn = findViewById(R.id.cancel_btn);
        addNewBtn = findViewById(R.id.add_new_btn);
        backBtn = findViewById(R.id.back_btn);

        //user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // showing the recycler view
        manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ManageJobPostsRVAdapter(this);
        recyclerView.setAdapter(adapter);

        // init firebase DB
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        databaseReference = fb.getReference("cebuapp_job_posts");

        // init img firebase Storage(img)
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        // load crudJobPosts
        crudJobPosts = new CRUDManageJobPosts();
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

    }

    private void choosingOfPicture() {
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an Image"), 7);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                jp_img_input.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    public void saveDataAndUploadImage(JobPosts editJobPost) {
        // validate inputs
        errors = false;
        if (jobPostCompanyVal.isEmpty()) {
            jp_company_input.setError("Company name is required.");
            jp_company_input.requestFocus();
            errors = true;
            return;
        }

        if (jobPostCompanyDetsVal.isEmpty()) {
            jp_company_dets_input.setError("Company description is required.");
            jp_company_dets_input.requestFocus();
            errors = true;
            return;
        }

        if (jp_img_input.getResources().equals(R.drawable.custom_input_field) || imageUri == null) {
            HelperUtilities.showOkAlert(ManageJobsPostsActivity.this, "Please select an Image.");
            errors = true;
            return;
        }

        if (jobPostTitleVal.isEmpty()) {
            jp_title_input.setError("Job Title is required.");
            jp_title_input.requestFocus();
            errors = true;
            return;
        }

        if (jobPostDescVal.isEmpty()) {
            jp_desc_input.setError("Job Description is required.");
            jp_desc_input.requestFocus();
            errors = true;
            return;
        }

        if (jobPostExpVal.isEmpty()) {
            jp_exp_input.setError("Job Experience is required.");
            jp_exp_input.requestFocus();
            errors = true;
            return;
        }

        if (jobPostSalaryVal.isEmpty()) {
            jp_salary_input.setError("Job salary offer is required.");
            jp_salary_input.requestFocus();
            errors = true;
            return;
        }

        if (jobPostLinkVal.isEmpty()) {
            jp_link_input.setError("Job link is required.");
            jp_link_input.requestFocus();
            errors = true;
            return;
        }

        if (!errors && errors.equals(false)) {
            dialog.setTitle("Saving Job Post data...");
            dialog.show();

            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(imageUri));
            UploadTask uploadTask = storageReference2.putFile(imageUri);
            uploadTask.addOnSuccessListener((OnSuccessListener) (taskSnapshot) -> {
                // get the image uri
                Task<Uri> downloadUri = storageReference2.getDownloadUrl();

                downloadUri.addOnSuccessListener((OnSuccessListener) (uri) ->{
                    // construct img uri data
                    jobPostImgVal = uri.toString();
                    jobPostPostedVal = HelperUtilities.getCurrentDate();
                    String jobAuthorVal = firebaseUser.getEmail();

                    jobPostsData = new JobPosts(jobPostApproveVal, jobAuthorVal, jobPostImgVal, jobPostTitleVal, jobPostDescVal, jobPostPostedVal, jobPostExpVal,
                            jobPostSalaryVal, jobPostCompanyVal, jobPostCompanyDetsVal, jobPostFieldsVal,
                            jobPostProvinceVal, jobPostLinkVal);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            // editing Job post
                            if (editJobPost != null && jp_action_btn.getText().equals("EDIT")) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("approved", jobPostApproveVal);
                                hashMap.put("jobPostTitle", jobPostTitleVal);
                                hashMap.put("jobPostImg", jobPostImgVal);
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
                                            "Job post was edited successfully!", Toast.LENGTH_SHORT).show();
                                    resetFormInputs();

                                }).addOnFailureListener(fail -> {
                                    Toast.makeText(ManageJobsPostsActivity.this,
                                            "Editing failed, please try again.", Toast.LENGTH_LONG).show();
                                });

                                // adding or inserting job post
                            } else {
                                String jobPostSaveId = databaseReference.push().getKey();
                                databaseReference.child(jobPostSaveId).setValue(jobPostsData)
                                        .addOnSuccessListener(suc -> {
                                            Toast.makeText(ManageJobsPostsActivity.this,
                                                    "Job Post was added successfully!", Toast.LENGTH_SHORT).show();
                                            resetFormInputs();

                                        }).addOnFailureListener(fail -> {
                                            Toast.makeText(ManageJobsPostsActivity.this,
                                                    "Adding failed, please try again.", Toast.LENGTH_LONG).show();
                                        });
                            }
                            // after editing or adding, show main btns
                            addNewBtn.setVisibility(View.VISIBLE);
                            backBtn.setVisibility(View.VISIBLE);
                            // hide form and show newly updated rv
                            add_edit_form.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                        }
                    }, 300);
                });
            });
        }
    }

    private void spinnerComponents() {
        // job field spinner
        jobFieldSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_job_fields");
        jobFieldSpinnerList = new ArrayList<>();
        jobFieldSpinnerAdapter = new ArrayAdapter<String>(ManageJobsPostsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, jobFieldSpinnerList);
        jobFieldSpinner.setAdapter(jobFieldSpinnerAdapter);

        // province spinner
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
                if (editJobPost != null && editJobPost.getJobPostJobField() != null) {
                    int fpos = jobFieldSpinnerAdapter.getPosition(editJobPost.getJobPostJobField());
                    jobFieldSpinner.setSelection(fpos);
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
                if (editJobPost != null && editJobPost.getJobPostProvince() != null) {
                    int ppos = provinceSpinnerAdapter.getPosition(editJobPost.getJobPostProvince());
                    provinceSpinner.setSelection(ppos);
                }
                provinceSpinnerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // approval spinner
        ArrayList<String> approveList = new ArrayList<>();
        approveList.add("Publish");
        approveList.add("Hidden");
        ArrayAdapter<String> approveSpinnerAdapter = new ArrayAdapter<String>(ManageJobsPostsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, approveList);
        jp_approve_spinner.setAdapter(approveSpinnerAdapter);
        if (editJobPost != null) {
            String isPublished = (editJobPost.getApproved().equals(true)) ? "Publish" : "Hidden";
            int apos = approveSpinnerAdapter.getPosition(isPublished);
            jp_approve_spinner.setSelection(apos);
        }

    }

    private void loadAllJobPosts() {
        dialog.setTitle("Fetching all Cebu Job Posts.");
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
                    jp_is_empty_list.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
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
        jp_img_input.requestFocus();
        jp_title_input.setText("");
        jp_desc_input.setText("");
        jp_exp_input.setText("");
        jp_salary_input.setText("");
        jp_link_input.setText("");
        jp_company_input.setText("");
        jp_company_dets_input.setText("");
        jp_action_btn.setText("ADD");
    }

    private void isCancelAdding(String actionVal) {
        new AlertDialog.Builder(ManageJobsPostsActivity.this)
            .setTitle("CebuApp")
            .setMessage("Are you sure you want to cancel " + actionVal + " Job Post?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetFormInputs();
                            // show main btns
                            addNewBtn.setVisibility(View.VISIBLE);
                            backBtn.setVisibility(View.VISIBLE);
                            // hide form and show rv
                            add_edit_form.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                        }
                    }, 300);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
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
        intent = new Intent(new Intent(ManageJobsPostsActivity.this, HomeActivity.class));
        intent.putExtra("isFromMngJobPostActivity", true);
        startActivity(intent);
        finish();
    }
}