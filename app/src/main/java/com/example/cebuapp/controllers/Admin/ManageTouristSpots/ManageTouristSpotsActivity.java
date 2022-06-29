package com.example.cebuapp.controllers.Admin.ManageTouristSpots;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.R;
import com.example.cebuapp.model.TouristSpot;
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
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ManageTouristSpotsActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    // form
    private TextView form_title, is_empty_list;
    private EditText title_input, desc_input, address_input, contact_email_input, contact_num_input;
    private String tpImgVal, tpTitleVal, tpDescVal, tpAddressVal, tpProvinceVal, tpContactEmailVal, tpContactNumVal, tpPostedVal;
    private Boolean tpApproveVal;
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
    private ManageTouristSpotsRVAdapter adapter;
    private CRUDManageTouristSpots crudTouristSpots;

    // others
    private ProgressDialog dialog;
    private ImageButton backBtn, addNewBtn;
    private Intent intent;
    private boolean isLoadMore = false;
    private String key = null;
    private TouristSpot touristSpotEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_touristspots);

        if (isConnectedToInternet()) {
            // cast components
            castComponents();
            setSpinners();
            setListeners();
            loadData();

            // check intent extras
            touristSpotEdit = (TouristSpot) getIntent().getSerializableExtra("touristSpotEdit");
            if (touristSpotEdit != null) {
                // show form and hide rv
                add_edit_form.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
                addNewBtn.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.INVISIBLE);

                // reuse form for editing
                form_title.setText("Editing a Tourist Spot");
                ShowImageUrl showImageUrl = (ShowImageUrl) new ShowImageUrl((ImageView) findViewById(R.id.img_input))
                        .execute(touristSpotEdit.getTouristSpotImg());
                title_input.setText(touristSpotEdit.getTouristSpotTitle());
                desc_input.setText(touristSpotEdit.getTouristSpotDescription());
                address_input.setText(touristSpotEdit.getTouristSpotAddress());
                contact_email_input.setText(touristSpotEdit.getTouristSpotContactEmail());
                contact_num_input.setText(touristSpotEdit.getTouristSpotContactNum());
                title_input.requestFocus();
                action_btn.setText("EDIT");
            }
        } else {
            HelperUtilities.showNoInternetAlert(ManageTouristSpotsActivity.this);
        }
    }

    private void setListeners() {
        img_input.setOnClickListener(v-> {
            choosingOfPicture();
        });

        // job field add/edit btn is clicked
        action_btn.setOnClickListener(v -> {

            // get value
            tpTitleVal = title_input.getText().toString().trim();
            tpDescVal = desc_input.getText().toString().trim();
            tpAddressVal = address_input.getText().toString().trim();
            tpContactEmailVal = contact_email_input.getText().toString().trim();
            tpContactNumVal = contact_num_input.getText().toString().trim();
            tpProvinceVal = provinceSpinner.getSelectedItem().toString().trim();
            String isPublish = approve_spinner.getSelectedItem().toString().trim();
            tpApproveVal = (isPublish.equals("Publish")) ? true : false;
            validateInputs();

            saveDataAndUploadImage(touristSpotEdit);
        });

        // add new btn
        addNewBtn.setOnClickListener(v -> {
            addNewBtn.setVisibility(View.INVISIBLE);
            backBtn.setVisibility(View.INVISIBLE);
            // show form and hide rv
            add_edit_form.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            // reset form input
            form_title.setText("Adding a Tourist Spot");
            title_input.setText("");
            title_input.requestFocus();
            desc_input.setText("");
            address_input.setText("");
            contact_email_input.setText("");
            contact_num_input.setText("");
            action_btn.setText("ADD");
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
            intent = new Intent(ManageTouristSpotsActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void castComponents() {
        // form container and form title
        add_edit_form = findViewById(R.id.add_edit_form);
        form_title = findViewById(R.id.form_title);
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

        // load crudTouristSpots
        crudTouristSpots = new CRUDManageTouristSpots();
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        // showing the recycler view
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ManageTouristSpotsRVAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setSpinners() {
        // province spinner
        provinceSpinner = findViewById(R.id.province_spinner);
        provinceSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_provinces");
        provinceSpinnerList = new ArrayList<>();
        provinceSpinnerAdapter = new ArrayAdapter<String>(ManageTouristSpotsActivity.this,
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
        ArrayList<String> approveSpinner = new ArrayList<>();
        approveSpinner.add("Publish");
        approveSpinner.add("Pending");
        ArrayAdapter<String> approveSpinnerAdapter = new ArrayAdapter<>(ManageTouristSpotsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, approveSpinner);
        approve_spinner.setAdapter(approveSpinnerAdapter);
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

    private void saveDataAndUploadImage(TouristSpot touristSpotEdit) {// construct data
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
                    tpImgVal = uri.toString();
                    tpPostedVal = HelperUtilities.getCurrentDate();
                    String tpAuthorVal = firebaseUser.getEmail();;
                    Integer pos = 0;

                    TouristSpot touristSpotData = new TouristSpot(tpApproveVal, tpAuthorVal, tpImgVal, tpAddressVal, tpContactEmailVal, tpContactNumVal,
                            tpDescVal, tpPostedVal, tpProvinceVal, tpTitleVal, pos);

                    // update
                    if (touristSpotEdit != null && action_btn.getText().equals("EDIT")) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("approved", tpApproveVal);
                        hashMap.put("touristSpotImg", tpImgVal);
                        hashMap.put("touristSpotAddress", tpAddressVal);
                        hashMap.put("touristSpotContactEmail", tpContactEmailVal);
                        hashMap.put("touristSpotContactNum", tpContactNumVal);
                        hashMap.put("touristSpotDescription", tpDescVal);
                        hashMap.put("touristSpotPosted", tpPostedVal);
                        hashMap.put("touristSpotProvince", tpProvinceVal);
                        hashMap.put("touristSpotTitle", tpTitleVal);
                        crudTouristSpots.update(touristSpotEdit.getKey(), hashMap).addOnSuccessListener(suc -> {
                            Toast.makeText(ManageTouristSpotsActivity.this,
                                    "Tourist Spot was updated successfully!", Toast.LENGTH_SHORT).show();
                            resetFormInputs();

                        }).addOnFailureListener(fail -> {
                            Toast.makeText(ManageTouristSpotsActivity.this,
                                    "Updating failed, please try again.", Toast.LENGTH_LONG).show();
                        });

                        // insert or add new prov
                    } else {
                        crudTouristSpots.add(touristSpotData).addOnSuccessListener(suc -> {
                            Toast.makeText(ManageTouristSpotsActivity.this,
                                    "Tourist Spot was added successfully!", Toast.LENGTH_SHORT).show();
                            resetFormInputs();

                        }).addOnFailureListener(fail -> {
                            Toast.makeText(ManageTouristSpotsActivity.this,
                                    "Adding failed, please try again.", Toast.LENGTH_LONG).show();
                        });
                    }
                    // hide form and show data
                    add_edit_form.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    // show main btns
                    addNewBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);
                });
            });
        } else {
            dialog.dismiss();
            Toast.makeText(this, "Please select an Image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetFormInputs() {
        form_title.setText("Adding a Tourist Spot:");
        title_input.setText("");
        desc_input.setText("");
        address_input.setText("");
        contact_email_input.setText("");
        contact_num_input.setText("");
        action_btn.setText("Add");
        add_edit_form.setVisibility(View.GONE);
        // show form and main btns
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        addNewBtn.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
    }

    private void validateInputs() {
        if (tpTitleVal.isEmpty()) {
            title_input.setError("Tourist Spot title is required.");
            title_input.requestFocus();
            return;
        }

        if (tpDescVal.isEmpty()) {
            desc_input.setError("Tourist Spot description is required.");
            desc_input.requestFocus();
            return;
        }

        if (tpAddressVal.isEmpty()) {
            address_input.setError("Tourist Spot landmark is required.");
            address_input.requestFocus();
            return;
        }

        if (tpContactEmailVal.isEmpty()) {
            contact_email_input.setError("Tourist Spot email is required.");
            contact_email_input.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tpContactEmailVal).matches()) {
            contact_email_input.setError("Please provide a valid email.");
            contact_email_input.requestFocus();
            return;
        }

        if (tpContactNumVal.isEmpty()) {
            contact_email_input.setError("Tourist Spot number is required.");
            contact_num_input.requestFocus();
            return;
        }
    }

    private void loadData() {
        dialog.setTitle("Fetching all Cebu Tourist Spots.");
        dialog.show();
        crudTouristSpots.getAll(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TouristSpot> spotList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    TouristSpot spotData = data.getValue(TouristSpot.class);

                    // set data
                    spotData.setKey(data.getKey());

                    // append to list
                    spotList.add(spotData);
                    key = data.getKey();
                }

                if (spotList.isEmpty() == true) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(spotList);
                    adapter.notifyDataSetChanged();
                    isLoadMore = false;
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    is_empty_list.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
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
        Intent intent = new Intent(ManageTouristSpotsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}