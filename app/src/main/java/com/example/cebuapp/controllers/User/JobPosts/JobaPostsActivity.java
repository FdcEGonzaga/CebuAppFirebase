package com.example.cebuapp.controllers.User.JobPosts;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageJobPosts.CRUDManageJobPosts;
import com.example.cebuapp.controllers.Admin.ManageTouristSpots.ManageTouristSpotsActivity;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.model.JobPosts;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class JobaPostsActivity extends AppCompatActivity {
    private Intent intent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout rvContainer, mainBody;
    private TextView is_empty_list, headerTitle;
    private androidx.appcompat.widget.SearchView searchView;
    private Handler handler;
    private FirebaseUser firebaseUser;
    private String userEmail;
    private Boolean errors;

    // main job field spinnner
    private Spinner mainJobFieldSpinner;
    private DatabaseReference mainJobFieldSpinnerRef;
    private ArrayList<String> mainJobFieldSpinnerList;
    private ArrayAdapter<String> mainJobFieldSpinnerAdapter;

    // form
    private JobPosts jobPostsData;
    private TextView jp_company_label, jp_img_label;
    private EditText jp_title_input, jp_desc_input, jp_exp_input, jp_salary_input, jp_link_input, jp_company_input,
            jp_company_dets_input;
    private String jobPostImgVal, jobPostTitleVal, jobPostDescVal, jobPostPostedVal, jobPostExpVal, jobPostSalaryVal,
            jobPostProvinceVal, jobPostCompanyVal, jobPostCompanyDetsVal, jobPostFieldsVal, jobPostLinkVal;
    private Boolean jobPostApproveVal;
    private Button formAddBtn, formCancelBtn;

    // Image uploading
    private Uri imageUri;
    private ImageView jp_img_input;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    // form job field spinner
    private Spinner jp_field_spinner;
    private DatabaseReference jobFieldSpinnerRef;
    private ArrayList<String> jobFieldSpinnerList;
    private ArrayAdapter<String> jobFieldSpinnerAdapter;

    // province spinner
    private Spinner jp_province_spinner;
    private DatabaseReference provinceSpinnerRef;
    private ArrayList<String> provinceSpinnerList;
    private ArrayAdapter<String> provinceSpinnerAdapter;

    // online recyclerview
    private RecyclerView recyclerView;
    private JobPostsRVAdapter adapter;
    private String key = null;
    private CRUDManageJobPosts crudJobPosts;
    private ProgressDialog dialog;
    private ImageButton backBtn, addBtn;
    private ScrollView addFormBody;

    // sort manager
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        castComponents();

        // hide these
        is_empty_list.setVisibility(View.INVISIBLE);
        addFormBody.setVisibility(View.INVISIBLE);

        if (isConnectedToInternet() && firebaseUser != null) {
            // ONLINE
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            databaseReference = db.getReference("cebuapp_job_posts");
            crudJobPosts = new CRUDManageJobPosts();

            // showing the recycler view
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(manager);
            adapter = new JobPostsRVAdapter(this);
            recyclerView.setAdapter(adapter);

            // set search forms
            mainJobFieldSpinner();
            searchWordListener();
            clickListeners();
            formSpinnerComponents();

            // check extras
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mainJobFieldSpinner.setSelection(extras.getInt("jobFieldSpinPos"));
            }

        } else {
            HelperUtilities.showNoInternetAlert(getApplicationContext());
        }
    }

    private void castComponents() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        handler = new Handler();

        // header
        headerTitle = findViewById(R.id.header_title);
        backBtn = findViewById(R.id.back_btn);
        addBtn = findViewById(R.id.add_btn);
        mainJobFieldSpinner = findViewById(R.id.jobs_spinner);
        rvContainer = findViewById(R.id.rv_container);
        recyclerView = findViewById(R.id.jp_rv);
        searchView = findViewById(R.id.search_view);
        mainBody = findViewById(R.id.main_body);
        is_empty_list = findViewById(R.id.is_empty_list);

        // form
        jp_company_label = findViewById(R.id.jp_company_label);
        jp_company_input = findViewById(R.id.jp_company_input);
        jp_company_dets_input = findViewById(R.id.jp_company_dets_input);
        jp_img_label = findViewById(R.id.jp_img_label);
        jp_img_input = findViewById(R.id.jp_img_input);
        jp_title_input = findViewById(R.id.jp_title_input);
        jp_desc_input = findViewById(R.id.jp_desc_input);
        jp_exp_input = findViewById(R.id.jp_exp_input);
        jp_salary_input = findViewById(R.id.jp_salary_input);
        jp_link_input = findViewById(R.id.jp_link_input);
        jp_field_spinner = findViewById(R.id.jp_field_spinner);
        jp_province_spinner = findViewById(R.id.jp_province_spinner);

        addFormBody = findViewById(R.id.add_form);
        formAddBtn = findViewById(R.id.form_add_btn);
        formCancelBtn = findViewById(R.id.form_cancel_btn);

        manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        // save data ang upload image
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        imageUri = null;
    }

    private void formSpinnerComponents() {
        // job field spinner
        jobFieldSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_job_fields");
        jobFieldSpinnerList = new ArrayList<>();
        jobFieldSpinnerAdapter = new ArrayAdapter<String>(JobaPostsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, jobFieldSpinnerList);
        jp_field_spinner.setAdapter(jobFieldSpinnerAdapter);

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

        // province spinner
        provinceSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_provinces");
        provinceSpinnerList = new ArrayList<>();
        provinceSpinnerAdapter = new ArrayAdapter<String>(JobaPostsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, provinceSpinnerList);
        jp_province_spinner.setAdapter(provinceSpinnerAdapter);

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

    private void clickListeners() {
        // header back to home btn
        backBtn.setOnClickListener(v-> {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });

        // header dropdown
        addBtn.setOnClickListener(v-> {
            PopupMenu popupMenu = new PopupMenu(JobaPostsActivity.this,addBtn);
            popupMenu.inflate(R.menu.user_header_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.menu_addPost:
                        addBtn.setVisibility(View.INVISIBLE);
                        backBtn.setVisibility(View.INVISIBLE);
                        mainBody.setVisibility(View.GONE);
                        addFormBody.setVisibility(View.VISIBLE);
                        break;

                    // if menu approve is clicked
                    case R.id.menu_ownPosts:
                        JobPostsOwnedFragment ownedPosts = new JobPostsOwnedFragment();
                        fragmentTransaction.replace(R.id.framelayout, ownedPosts);
                        fragmentTransaction.commit();
                        break;

                    case R.id.menu_ownFavs:
                        JobPostsFavoritesFragment favPosts = new JobPostsFavoritesFragment();
                        fragmentTransaction.replace(R.id.framelayout, favPosts);
                        fragmentTransaction.commit();
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        jp_img_input.setOnClickListener(v-> {
            choosingOfPicture();
        });

        // form add btn
        formAddBtn.setOnClickListener(v-> {
            // get values
            jobPostTitleVal = jp_title_input.getText().toString().trim();
            jobPostDescVal = jp_desc_input.getText().toString().trim();
            jobPostExpVal = jp_exp_input.getText().toString().trim();
            jobPostSalaryVal = jp_salary_input.getText().toString().trim();
            jobPostLinkVal = jp_link_input.getText().toString().trim();
            jobPostCompanyVal = jp_company_input.getText().toString().trim();
            jobPostCompanyDetsVal = jp_company_dets_input.getText().toString().trim();
            jobPostFieldsVal = jp_field_spinner.getSelectedItem().toString().trim();
            jobPostProvinceVal = jp_province_spinner.getSelectedItem().toString().trim();

            saveDataAndUploadImage();
        });

        // form cancel btn
        formCancelBtn.setOnClickListener(v-> {
            isCancelAdding();
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mainJobFieldSpinner.setSelection(mainJobFieldSpinner.getSelectedItemPosition());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 300);
            }
        });
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

    public void saveDataAndUploadImage() {
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
            HelperUtilities.showOkAlert(JobaPostsActivity.this, "Please select an Image.");
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
            dialog.setTitle("Submitting your Job Post entry...");
            dialog.show();

            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(imageUri));
            UploadTask uploadTask = storageReference2.putFile(imageUri);
            uploadTask.addOnSuccessListener((OnSuccessListener) (taskSnapshot) -> {
                // get the image uri
                Task<Uri> downloadUri = storageReference2.getDownloadUrl();

                downloadUri.addOnSuccessListener((OnSuccessListener) (uri) ->{
                    // construct img uri data
                    jobPostImgVal = uri.toString();
                    jobPostPostedVal = getCurrentDate();
                    jobPostApproveVal = false;
                    userEmail = firebaseUser.getEmail();

                    jobPostsData = new JobPosts(jobPostApproveVal, userEmail, jobPostImgVal, jobPostTitleVal, jobPostDescVal, jobPostPostedVal, jobPostExpVal,
                            jobPostSalaryVal, jobPostCompanyVal, jobPostCompanyDetsVal, jobPostFieldsVal,
                            jobPostProvinceVal, jobPostLinkVal);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();

                            // adding of new job post
                            String jobPostSaveId = databaseReference.push().getKey();
                            databaseReference.child(jobPostSaveId).setValue(jobPostsData)
                                    .addOnSuccessListener(suc -> {
                                        // reset form
                                        resetFormInputs();
                                        // delay showing of msg
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                HelperUtilities.showOkAlert(JobaPostsActivity.this,
                                                        "Your Job Post entry was submitted successfully, our Admin will review your entry and post it once approved. Thanks!");
                                            }
                                        }, 300);
                                    }).addOnFailureListener(fail -> {
                                        HelperUtilities.showOkAlert(JobaPostsActivity.this,
                                                "Adding failed, please try again.");
                                    });
                            dialog.dismiss();
                        }
                    }, 300);
                });
            });
        }
    }

    private String getCurrentDate() {
        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return df.format(calendar);
    }

    private void resetFormInputs() {
        // show main btns
        addBtn.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        // hide form and show newly updated rv
        addFormBody.setVisibility(View.GONE);
        mainBody.setVisibility(View.VISIBLE);

        // reset form input
        jp_company_label.requestFocus();
        jp_company_input.setText("");
        jp_company_input.setError(null);
        jp_company_dets_input.setText("");
        jp_company_dets_input.setError(null);
        jp_title_input.setText("");
        jp_title_input.setError(null);
        jp_desc_input.setText("");
        jp_desc_input.setError(null);
        jp_exp_input.setText("");
        jp_exp_input.setError(null);
        jp_salary_input.setText("");
        jp_salary_input.setError(null);
        jp_link_input.setText("");
        jp_link_input.setError(null);
    }

    private void isCancelAdding() {
        new AlertDialog.Builder(JobaPostsActivity.this)
            .setTitle("CebuApp")
            .setMessage("Are you sure you want to cancel submitting your Job Post entry?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetFormInputs();
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

    private void mainJobFieldSpinner() {
        mainJobFieldSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_job_fields");
        mainJobFieldSpinnerList = new ArrayList<>();
        mainJobFieldSpinnerAdapter = new ArrayAdapter<String>(JobaPostsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, mainJobFieldSpinnerList);
        mainJobFieldSpinner.setAdapter(mainJobFieldSpinnerAdapter);

        // supply data to spinner
        mainJobFieldSpinnerList.add("ALL");
        mainJobFieldSpinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot uniqueKey: snapshot.getChildren()) {
                        for(DataSnapshot data : uniqueKey.getChildren()){
                            mainJobFieldSpinnerList.add(data.getValue().toString());
                        }
                    }
                    mainJobFieldSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JobaPostsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // spinner listener for categories
        mainJobFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get selected position of spinner
                String selectedCategory = mainJobFieldSpinner.getSelectedItem().toString();

                dialog.setTitle("Fetching " + selectedCategory + " Job Post in Cebu.");
                dialog.show();

                // clear searchview value
                if (searchView.getQuery() != "") {
                    searchView.setQuery("", false);
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnectedToInternet()) {
                            // ONLINE
                            if (selectedCategory.equals("ALL")) {
                                getAllJobs();
                            } else {
                                getJobsByCat(selectedCategory, position);
                            }
                        } else {
                            Toast.makeText(JobaPostsActivity.this, "Please connect to the Internet.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1000);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

    private void searchWordListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()  {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String selectedField = mainJobFieldSpinner.getSelectedItem().toString();
                dialog.setTitle("Searching '" + query + "' from '" + selectedField + "' job category");
                dialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        crudJobPosts.getSearchedWord(query).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<JobPosts> jobPostList = new ArrayList<>();
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    JobPosts jobPulledData = data.getValue(JobPosts.class);

                                    // only get approved job posts
                                    if (jobPulledData.getApproved().equals(true)) {
                                        // get by selected field category
                                        if (jobPulledData.getJobPostJobField().equals(selectedField)) {
                                            // append to list
                                            jobPulledData.setKey(data.getKey());
                                            jobPostList.add(jobPulledData);
                                            key = data.getKey();

                                        } else if (selectedField == "ALL") {
                                            // append to list
                                            jobPulledData.setKey(data.getKey());
                                            jobPostList.add(jobPulledData);
                                            key = data.getKey();
                                        }
                                    }
                                }

                                if (jobPostList.isEmpty() == true) {
                                    rvContainer.setVisibility(View.GONE);
                                    is_empty_list.setVisibility(View.VISIBLE);
                                } else {
                                    adapter.setItems(jobPostList);
                                    adapter.notifyDataSetChanged();
                                    rvContainer.setVisibility(View.VISIBLE);
                                    is_empty_list.setVisibility(View.GONE);
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(JobaPostsActivity.this, "Error occured.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, 1000);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Integer selectedProvincePos = mainJobFieldSpinner.getSelectedItemPosition();
                // if search is emptied
                if (newText.equals("")) {
                    // reselect province
                    if (selectedProvincePos.equals(0)) {
                        mainJobFieldSpinner.setSelection(1);
                    } else {
                        mainJobFieldSpinner.setSelection(0);
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainJobFieldSpinner.setSelection(selectedProvincePos);
                        }
                    }, 50);
                }
                return false;
            }
        });
    }

    private void getAllJobs() {
        crudJobPosts.getAllApproved(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<JobPosts> jobPostList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    JobPosts jobPulledData = data.getValue(JobPosts.class);

                    // set data
                    jobPulledData.setKey(data.getKey());

                    // append to list
                    jobPostList.add(jobPulledData);
                    key = data.getKey();
                }

                if (jobPostList.isEmpty() == true) {
                    rvContainer.setVisibility(View.GONE);
                    is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(jobPostList);
                    adapter.notifyDataSetChanged();
                    rvContainer.setVisibility(View.VISIBLE);
                    is_empty_list.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JobaPostsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getJobsByCat(String selectedCategory, Integer spinnerPos) {
        crudJobPosts.getAllApproved(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<JobPosts> jobPostList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    JobPosts jobPulledData = data.getValue(JobPosts.class);

                    if (jobPulledData.getJobPostJobField().equals(selectedCategory)) {
                        // set data
                        jobPulledData.setKey(data.getKey());

                        // append to list
                        jobPostList.add(jobPulledData);
                        key = data.getKey();
                    }
                }

                if (jobPostList.isEmpty() == true) {
                    rvContainer.setVisibility(View.GONE);
                    is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(jobPostList);
                    adapter.notifyDataSetChanged();
                    rvContainer.setVisibility(View.VISIBLE);
                    is_empty_list.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JobaPostsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isConnectedToInternet() {
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentById(R.id.framelayout);
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        } else {
            super.onBackPressed();
            startActivity(new Intent(new Intent(getApplicationContext(), HomeActivity.class)));
            finish();
        }
    }
}