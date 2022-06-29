package com.example.cebuapp.controllers.Admin.ManageFoodAreas;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cebuapp.Helper.DataFetcher;
import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageJobPosts.ManageJobsPostsActivity;
import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.controllers.LoginActivity;
import com.example.cebuapp.model.FoodArea;
import com.example.cebuapp.controllers.HomeActivity;
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
import com.google.firebase.storage.StorageRegistrar;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.internal.StorageReferenceUri;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ManageFoodAreasActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    // form
    private TextView form_title, img_label, title_label, desc_label, address_label, province_label,
            contact_email_label, contact_num_label, is_empty_list;
    private EditText title_input, desc_input, address_input, contact_email_input, contact_num_input;
    private String foodAreaImgVal, foodAreaTitleVal, foodAreaDescVal, foodAreaAddressVal, foodAreaProvinceVal, foodAreaContactEmailVal,
            foodAreaContactNumVal, foodAreaPostedVal;
    private Boolean foodAreaApproveVal;
    private Spinner approve_spinner;

    // Image uploading
    private Uri imageUri;
    private ImageView img_input;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    // province spinner
    private Spinner provinceSpinner;
    private DatabaseReference provinceSpinnerRef;
    private ArrayList<String> provinceSpinnerList;
    private ArrayAdapter<String> provinceSpinnerAdapter;

    // form container and buttons
    private ScrollView add_edit_form;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button action_btn, cancel_btn;

    // recyclerView
    private RecyclerView recyclerView;
    private ManageFoodAreasRVAdapter adapter;
    private CRUDManageFoodAreas crudFoodAreas;

    // others
    private ProgressDialog dialog;
    private ImageButton backBtn, addNewBtn;
    private Intent intent;
    private boolean isLoadMore = false, isApproved = false;
    private String key = null;
    private FoodArea editFoodArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_foodareas);

        if (isConnectedToInternet()) {
            // cast components
            castComponents();
            setSpinners();
            setListeners();
            loadData();

            // check intent extra for editing a food area
            editFoodArea = (FoodArea) getIntent().getSerializableExtra("foodAreaEdit");
            if (editFoodArea != null) {
                // show form and hide rv
                add_edit_form.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
                addNewBtn.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.INVISIBLE);

                // reuse form for editing
                form_title.setText("Editing a Food Area");
                ShowImageUrl showImageUrl = (ShowImageUrl) new ShowImageUrl((ImageView) findViewById(R.id.img_input))
                        .execute(editFoodArea.getFoodImg());
                title_input.setText(editFoodArea.getFoodTitle());
                desc_input.setText(editFoodArea.getFoodDescription());
                address_input.setText(editFoodArea.getFoodAddress());
                contact_email_input.setText(editFoodArea.getFoodContactEmail());
                contact_num_input.setText(editFoodArea.getFoodContactNum());
                title_input.requestFocus();
                action_btn.setText("EDIT");
            }

        } else {
            HelperUtilities.showNoInternetAlert(ManageFoodAreasActivity.this);
        }
    }

    private void castComponents() {
        // form container and form title
        add_edit_form = findViewById(R.id.add_edit_form);
        form_title = findViewById(R.id.form_title);

        // form labels
        title_label = findViewById(R.id.title_label);
        desc_label = findViewById(R.id.desc_label);
        address_label = findViewById(R.id.address_label);
        province_label = findViewById(R.id.province_label);
        contact_email_label = findViewById(R.id.contact_email_label);
        contact_num_label = findViewById(R.id.contact_num_label);
        is_empty_list = findViewById(R.id.is_empty_list);

        // form inputs
        img_input = findViewById(R.id.img_input);
        title_input = findViewById(R.id.title_input);
        desc_input = findViewById(R.id.desc_input);
        address_input = findViewById(R.id.address_input);
        contact_email_input = findViewById(R.id.contact_email_input);
        contact_num_input = findViewById(R.id.contact_num_input);
        provinceSpinner = findViewById(R.id.province_spinner);
        approve_spinner = findViewById(R.id.approve_spinner);

        // others
        action_btn = findViewById(R.id.action_btn);
        is_empty_list = findViewById(R.id.is_empty_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.rv);

        // btns
        cancel_btn = findViewById(R.id.cancel_btn);
        addNewBtn = findViewById(R.id.add_new_btn);
        backBtn = findViewById(R.id.back_btn);

        //user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // init firebase DB
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("cebuapp_food_areas");

        // init img firebase Storage(img)
        storageReference = FirebaseStorage.getInstance().getReference("Images");

        // load crudFoodAreas
        crudFoodAreas = new CRUDManageFoodAreas();
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        // showing the recycler view
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ManageFoodAreasRVAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setSpinners() {
        // province spinner
        provinceSpinner = findViewById(R.id.province_spinner);
        provinceSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_provinces");
        provinceSpinnerList = new ArrayList<>();
        provinceSpinnerAdapter = new ArrayAdapter<String>(ManageFoodAreasActivity.this,
                android.R.layout.simple_spinner_dropdown_item, provinceSpinnerList);
        provinceSpinner.setAdapter(provinceSpinnerAdapter);

        // province spinner data
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
                return;
            }
        });

        // approval spinner
        ArrayList<String> approveList = new ArrayList<>();
        approveList.add("Publish");
        approveList.add("Pending");
        ArrayAdapter<String> approveSpinnerAdapter = new ArrayAdapter<>(ManageFoodAreasActivity.this,
                android.R.layout.simple_spinner_dropdown_item, approveList);
        approve_spinner.setAdapter(approveSpinnerAdapter);
    }

    private void setListeners() {
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

        img_input.setOnClickListener(v-> {
            choosingOfPicture();
        });


        // add/edit food btn is clicked
        action_btn.setOnClickListener(v -> {
            // get value
            foodAreaTitleVal = title_input.getText().toString().trim();
            foodAreaDescVal = desc_input.getText().toString().trim();
            foodAreaAddressVal = address_input.getText().toString().trim();
            foodAreaProvinceVal = provinceSpinner.getSelectedItem().toString().trim();
            foodAreaContactEmailVal = contact_email_input.getText().toString().trim();
            foodAreaContactNumVal = contact_num_input.getText().toString().trim();
            String isPublish = approve_spinner.getSelectedItem().toString().trim();
            foodAreaApproveVal = (isPublish.equals("Publish")) ? true : false;
            validateInputs();

            // save data ang upload image
            saveDataAndUploadImage(editFoodArea);
        });


        // add new btn
        addNewBtn.setOnClickListener(v -> {
            resetFormInputs();
            // hide main btns
            addNewBtn.setVisibility(View.INVISIBLE);
            backBtn.setVisibility(View.INVISIBLE);
            // set default img
            img_input.setImageResource(R.drawable.logo);
            // show form and hide rv
            add_edit_form.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        });

        // cancel btn
        cancel_btn.setOnClickListener(v -> {
            addNewBtn.setVisibility(View.VISIBLE);
            backBtn.setVisibility(View.VISIBLE);
            // hide form and show rv
            add_edit_form.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        });

        // back to home btn
        backBtn.setOnClickListener(v-> {
            intent = new Intent(ManageFoodAreasActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void validateInputs() {
        // validate inputs
        if (foodAreaTitleVal.isEmpty()) {
            title_input.setError("Food Area title is required.");
            title_input.requestFocus();
            return;
        }

        if (foodAreaDescVal.isEmpty()) {
            desc_input.setError("Food Area description is required.");
            desc_input.requestFocus();
            return;
        }

        if (foodAreaAddressVal.isEmpty()) {
            address_input.setError("Food Area landmark is required.");
            address_input.requestFocus();
            return;
        }

        if (foodAreaContactEmailVal.isEmpty()) {
            contact_email_input.setError("Food Area email is required.");
            contact_email_input.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(foodAreaContactEmailVal).matches()) {
            contact_email_input.setError("Please provide a valid email.");
            contact_email_input.requestFocus();
            return;
        }

        if (foodAreaContactNumVal.isEmpty()) {
            contact_email_input.setError("Food Area number is required.");
            contact_num_input.requestFocus();
            return;
        }
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
                img_input.setImageBitmap(bitmap);
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


    private void saveDataAndUploadImage(FoodArea editFoodArea) {
        dialog.setTitle("Saving Food Area data...");
        dialog.show();

        if (imageUri != null) {
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() +'.'+ GetFileExtension(imageUri));
            UploadTask uploadTask = storageReference2.putFile(imageUri);
            uploadTask.addOnSuccessListener((OnSuccessListener) (taskSnapshot) -> {
                // get the img uri
                Task<Uri> downloadUri = storageReference2.getDownloadUrl();

                downloadUri.addOnSuccessListener((OnSuccessListener)(uri) -> {
                    // construct img data
                    foodAreaImgVal = uri.toString();
                    foodAreaPostedVal = getCurrentDate();
                    String foodAuthorVal = firebaseUser.getEmail();;
                    Integer spinnerPos = 0;

                    FoodArea foodAreasData = new FoodArea(foodAreaApproveVal, foodAuthorVal, foodAreaImgVal, foodAreaAddressVal, foodAreaContactEmailVal, foodAreaContactNumVal,
                            foodAreaDescVal, foodAreaPostedVal, foodAreaProvinceVal, foodAreaTitleVal, spinnerPos);

                    dialog.dismiss();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // update
                            if (editFoodArea != null && action_btn.getText().equals("EDIT")) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("approved", foodAreaApproveVal);
                                hashMap.put("foodImg", foodAreaImgVal);
                                hashMap.put("foodTitle", foodAreaTitleVal);
                                hashMap.put("foodAddress", foodAreaAddressVal);
                                hashMap.put("foodContactEmail", foodAreaContactEmailVal);
                                hashMap.put("foodContactNum", foodAreaContactNumVal);
                                hashMap.put("foodDescription", foodAreaDescVal);
                                hashMap.put("foodPosted", foodAreaPostedVal);
                                hashMap.put("foodProvince", foodAreaProvinceVal);
                                crudFoodAreas.update(editFoodArea.getKey(), hashMap).addOnSuccessListener(suc -> {
                                    Toast.makeText(ManageFoodAreasActivity.this,
                                            "Food Area was updated successfully!", Toast.LENGTH_SHORT).show();
                                    resetFormInputs();

                                }).addOnFailureListener(fail -> {
                                    Toast.makeText(ManageFoodAreasActivity.this,
                                            "Updating failed, please try again.", Toast.LENGTH_LONG).show();
                                });

                                // insert or add new prov
                            } else {
                                crudFoodAreas.add(foodAreasData).addOnSuccessListener(suc -> {
                                    Toast.makeText(ManageFoodAreasActivity.this,
                                            "Food Area was added successfully!", Toast.LENGTH_SHORT).show();
                                    resetFormInputs();

                                }).addOnFailureListener(fail -> {
                                    Toast.makeText(ManageFoodAreasActivity.this,
                                            "Adding failed, please try again.", Toast.LENGTH_LONG).show();
                                });
                            }
                        }
                    }, 300);
                });
                // hide form and show data
                add_edit_form.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                // show main btns
                addNewBtn.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
            });
        } else {
            dialog.dismiss();
            Toast.makeText(this, "Please select an Image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetFormInputs() {
        title_label.setText("Add a Food Area:");
        title_input.setText("");
        desc_input.setText("");
        address_input.setText("");
        contact_email_input.setText("");
        contact_num_input.setText("");
        action_btn.setText("Add");
        add_edit_form.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        addNewBtn.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
    }

    private String getCurrentDate() {
        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return df.format(calendar);
    }

    private void loadData() {
        dialog.setTitle("Fetching all Cebu Food Areas.");
        dialog.show();
        crudFoodAreas.getAll(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<FoodArea> foodAreasList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    FoodArea manageFoodAreas = data.getValue(FoodArea.class);

                    // set data
                    manageFoodAreas.setKey(data.getKey());

                    // append to list
                    foodAreasList.add(manageFoodAreas);
                    key = data.getKey();
                }

                if (foodAreasList.isEmpty() == true) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(foodAreasList);
                    adapter.notifyDataSetChanged();
                    isLoadMore = false;
                    swipeRefreshLayout.setRefreshing(false);
                    is_empty_list.setVisibility(View.GONE);
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
        // go back to admin homepage
        Intent intent = new Intent(ManageFoodAreasActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}