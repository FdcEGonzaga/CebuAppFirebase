package com.example.cebuapp.controllers.User.TouristSpots;

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
import android.util.Patterns;
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
import com.example.cebuapp.controllers.Admin.ManageTouristSpots.CRUDManageTouristSpots;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.controllers.User.JobPosts.JobaPostsActivity;
import com.example.cebuapp.model.TouristSpot;
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

public class SpotsActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseUser firebaseUser;
    private Intent intent;
    private TextView is_empty_list;
    private androidx.appcompat.widget.SearchView searchView;
    private Handler handler;
    private Boolean errors;
    private TouristSpot editTouristSpot;

    // main spinnner
    private Spinner mainProvinceSpinner;
    private DatabaseReference mainProvinceSpinnerRef;
    private ArrayList<String> mainProvinceSpinnerList;
    private ArrayAdapter<String> mainProvinceSpinnerAdapter;

    // online recyclerview
    private RecyclerView recyclerView;
    private LinearLayout rvContainer;
    private DatabaseReference databaseReference;
    private SpotsRVAdapter adapter;
    private String key = null;
    private CRUDManageTouristSpots crudTouristSpots;
    private ProgressDialog dialog;

    // header
    private ImageButton backBtn, addBtn;
    private TextView headerTitle;

    // LinearLayout
    private LinearLayout mainBody;

    // add form
    private ScrollView addFormBody;
    private Button formAddBtn, formCancelBtn;
    private EditText title_input, desc_input, address_input, contact_email_input, contact_num_input;
    private String tpImgVal, tpTitleVal, tpDescVal, tpAddressVal, tpProvinceVal, tpContactEmailVal, tpContactNumVal, tpPostedVal;
    private Boolean tpApproveVal;
    private Spinner province_spinner;

    // Image uploading
    private Uri imageUri;
    private ImageView img_input;
    private StorageReference storageReference;

    // province spinner
    private Spinner provinceSpinner;
    private DatabaseReference provinceSpinnerRef;
    private ArrayList<String> provinceSpinnerList;
    private ArrayAdapter<String> provinceSpinnerAdapter;
    private ArrayList<TouristSpot> spotsList;

    // sort manager
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spots);

        if (isConnectedToInternet()) {
            castComponents();
            setListeners();
            formSpinnerComponents();

            // set main spinner and search form
            mainProvinceSpinner();
            searchWordListener();

            // user
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            // showing the recycler view
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            databaseReference = db.getReference("cebuapp_tourist_spots");
            crudTouristSpots = new CRUDManageTouristSpots();
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(manager);
            adapter = new SpotsRVAdapter(this);
            recyclerView.setAdapter(adapter);

            // check extras
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mainProvinceSpinner.setSelection(extras.getInt("provSpinPos"));
            }

        } else {
            HelperUtilities.showNoInternetAlert(getApplicationContext());
        }
    }

    private void castComponents() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        handler = new Handler();
        spotsList = new ArrayList<>();

        is_empty_list = findViewById(R.id.is_empty_list);
        recyclerView = findViewById(R.id.spots_rv);
        rvContainer = findViewById(R.id.rv_container);
        searchView = findViewById(R.id.search_view);
        addBtn = findViewById(R.id.add_btn);
        backBtn = findViewById(R.id.back_btn);
        is_empty_list.setVisibility(View.INVISIBLE);
        mainProvinceSpinner = findViewById(R.id.food_spinner);

        // form
        img_input = findViewById(R.id.img_input);
        title_input = findViewById(R.id.title_input);
        desc_input = findViewById(R.id.desc_input);
        address_input = findViewById(R.id.address_input);
        contact_email_input = findViewById(R.id.contact_email_input);
        contact_num_input = findViewById(R.id.contact_num_input);
        province_spinner = findViewById(R.id.province_spinner);

        mainBody = findViewById(R.id.main_body);
        addFormBody = findViewById(R.id.add_form);
        addFormBody = findViewById(R.id.add_form);
        formAddBtn = findViewById(R.id.form_add_btn);
        formCancelBtn = findViewById(R.id.form_cancel_btn);

        manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        storageReference = FirebaseStorage.getInstance().getReference("Images");
    }


    private void setListeners() {
        // header back to home btn
        backBtn.setOnClickListener(v-> {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });

        // header add job post
        addBtn.setOnClickListener(v-> {
            PopupMenu popupMenu = new PopupMenu(SpotsActivity.this, addBtn);
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
                        SpotsPostOwnedFragment ownedPosts = new SpotsPostOwnedFragment();
                        fragmentTransaction.replace(R.id.framelayout, ownedPosts);
                        fragmentTransaction.commit();
                        break;

                    case R.id.menu_ownFavs:
                        SpotsFavoritesFragment favSpots = new SpotsFavoritesFragment();
                        fragmentTransaction.replace(R.id.framelayout, favSpots);
                        fragmentTransaction.commit();
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        img_input.setOnClickListener(v-> {
            choosingOfPicture();
        });

        // adding food area
        formAddBtn.setOnClickListener(v -> {
            // get value
            tpTitleVal = title_input.getText().toString().trim();
            tpDescVal = desc_input.getText().toString().trim();
            tpAddressVal = address_input.getText().toString().trim();
            tpProvinceVal = province_spinner.getSelectedItem().toString().trim();
            tpContactEmailVal = contact_email_input.getText().toString().trim();
            tpContactNumVal = contact_num_input.getText().toString().trim();

            // save data ang upload image
            saveDataAndUploadImage(editTouristSpot);
        });

        // cancel form food area
        formCancelBtn.setOnClickListener(v -> {
            isCancelAdding();
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        provinceSpinner.setSelection(provinceSpinner.getSelectedItemPosition());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 300);
            }
        });
    }

    private void formSpinnerComponents() {
        // province spinner
        provinceSpinner = findViewById(R.id.province_spinner);
        provinceSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_provinces");
        provinceSpinnerList = new ArrayList<>();
        provinceSpinnerAdapter = new ArrayAdapter<String>(SpotsActivity.this,
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
        errors = false;

        if (tpTitleVal.isEmpty()) {
            title_input.setError("Tourist Spot title is required.");
            title_input.requestFocus();
            errors = true;
            return;
        }

        if (img_input.getResources().equals(R.drawable.custom_input_field) || imageUri == null) {
            HelperUtilities.showOkAlert(SpotsActivity.this, "Please select an Image.");
            errors = true;
            return;
        }

        if (tpDescVal.isEmpty()) {
            desc_input.setError("Tourist Spot description is required.");
            desc_input.requestFocus();
            errors = true;
            return;
        }

        if (tpContactEmailVal.isEmpty()) {
            contact_email_input.setError("Tourist Spot email is required.");
            contact_email_input.requestFocus();
            errors = true;
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tpContactEmailVal).matches()) {
            contact_email_input.setError("Please provide a valid email.");
            contact_email_input.requestFocus();
            errors = true;
            return;
        }

        if (tpContactNumVal.isEmpty()) {
            contact_num_input.setError("Tourist Spot number is required.");
            contact_num_input.requestFocus();
            errors = true;
            return;
        }

        if (!HelperUtilities.isValidPhone(tpContactNumVal)) {
            contact_num_input.setError("Tourist Spot number is invalid.");
            contact_num_input.requestFocus();
            errors = true;
            return;
        }

        if (tpAddressVal.isEmpty()) {
            address_input.setError("Tourist Spot address or landmark is required.");
            address_input.requestFocus();
            errors = true;
            return;
        }

        if (!errors && errors.equals(false)) {
            dialog.setTitle("Submitting your Tourist Spot entry...");
            dialog.show();

            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() +'.'+ GetFileExtension(imageUri));
            UploadTask uploadTask = storageReference2.putFile(imageUri);
            uploadTask.addOnSuccessListener((OnSuccessListener) (taskSnapshot) -> {
                // get the img uri
                Task<Uri> downloadUri = storageReference2.getDownloadUrl();

                downloadUri.addOnSuccessListener((OnSuccessListener)(uri) -> {
                    // construct img data
                    tpApproveVal = false;
                    tpImgVal = uri.toString();
                    tpPostedVal = getCurrentDate();
                    String tpAuthorVal = firebaseUser.getEmail();

                    TouristSpot touristSpotData = new TouristSpot(tpApproveVal, tpAuthorVal, tpImgVal, tpAddressVal, tpContactEmailVal, tpContactNumVal,
                            tpDescVal, tpPostedVal, tpProvinceVal, tpTitleVal);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();

                            // process adding
                            crudTouristSpots.add(touristSpotData).addOnSuccessListener(suc -> {
                                // reset form
                                resetFormInputs();
                                // delay showing of msg
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        HelperUtilities.showOkAlert(SpotsActivity.this,
                                                "Your Tourist Spot entry was submitted successfully, our Admin will review your entry and post it once approved. Thanks!");
                                    }
                                }, 300);
                            }).addOnFailureListener(fail -> {
                                Toast.makeText(SpotsActivity.this,
                                        "Adding failed, please try again.", Toast.LENGTH_LONG).show();
                            });
                        }
                    }, 300);
                });
            });
        }
    }

    private void resetFormInputs() {
        // reset form
        img_input.setImageResource(R.drawable.custom_input_field);
        title_input.setText("");
        title_input.setError(null);
        desc_input.setText("");
        desc_input.setError(null);
        contact_email_input.setText("");
        contact_email_input.setError(null);
        contact_num_input.setText("");
        contact_num_input.setError(null);
        address_input.setText("");
        address_input.setError(null);

        // hide form and show data
        addFormBody.setVisibility(View.GONE);
        mainBody.setVisibility(View.VISIBLE);
        // show main btns
        addBtn.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
    }

    private String getCurrentDate() {
        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return df.format(calendar);
    }

    private void isCancelAdding() {
        new AlertDialog.Builder(SpotsActivity.this)
                .setTitle("CebuApp")
                .setMessage("Are you sure you want to cancel submitting your Tourist Spot entry?")
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

    // start of main view
    private void mainProvinceSpinner() {
        mainProvinceSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_provinces");
        mainProvinceSpinnerList = new ArrayList<>();
        mainProvinceSpinnerAdapter = new ArrayAdapter<String>(SpotsActivity.this, android.R.layout.simple_spinner_dropdown_item,
                mainProvinceSpinnerList);
        mainProvinceSpinner.setAdapter(mainProvinceSpinnerAdapter);

        // supply data to spinner
        mainProvinceSpinnerList.add("ALL");
        mainProvinceSpinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uniqueKey: snapshot.getChildren()) {
                    for(DataSnapshot data : uniqueKey.getChildren()){
                        mainProvinceSpinnerList.add(data.getValue().toString());
                    }
                }
                mainProvinceSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SpotsActivity.this, "Unable to pull Cebu provinces", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        // spinner listener for province
        mainProvinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get selected position of spinner
                String selectedCategory = mainProvinceSpinner.getSelectedItem().toString();
                dialog.setTitle("Fetching " + selectedCategory + "'s Tourist Spots.");
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
                                getAllSpots();
                            } else {
                                getAllSpotsByProvince(selectedCategory, position);
                            }
                        } else {
                            Toast.makeText(SpotsActivity.this, "Please connect to the Internet.", Toast.LENGTH_SHORT).show();
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
                String selectedProvince = mainProvinceSpinner.getSelectedItem().toString();
                dialog.setTitle("Searching '" + query + "' from '" + selectedProvince + "'");
                dialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        crudTouristSpots.getSearchedWord(query).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                spotsList.clear();
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    TouristSpot spotsData = data.getValue(TouristSpot.class);

                                    // get only approved food areas
                                    if (spotsData.getApproved().equals(true)) {
                                        // get by selected province
                                        if (spotsData.getTouristSpotProvince().equals(selectedProvince)) {
                                            // append to list
                                            spotsData.setKey(data.getKey());
                                            spotsList.add(spotsData);
                                            key = data.getKey();

                                        } else if (selectedProvince == "ALL") {
                                            // append to list
                                            spotsData.setKey(data.getKey());
                                            spotsList.add(spotsData);
                                            key = data.getKey();
                                        }
                                    }
                                }

                                if (spotsList.isEmpty() == true) {
                                    rvContainer.setVisibility(View.GONE);
                                    is_empty_list.setVisibility(View.VISIBLE);
                                } else {
                                    adapter.setItems(spotsList);
                                    adapter.notifyDataSetChanged();
                                    rvContainer.setVisibility(View.VISIBLE);
                                    is_empty_list.setVisibility(View.GONE);
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(SpotsActivity.this, "Error occured.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, 1000);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Integer selectedProvincePos = mainProvinceSpinner.getSelectedItemPosition();
                // if search is emptied
                if (newText.equals("")) {
                    // reselect province
                    if (selectedProvincePos.equals(0)) {
                        mainProvinceSpinner.setSelection(1);
                    } else {
                        mainProvinceSpinner.setSelection(0);
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainProvinceSpinner.setSelection(selectedProvincePos);
                        }
                    }, 50);
                }
                return false;
            }
        });
    }

    private void getAllSpots() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                crudTouristSpots.getAllApproved().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        spotsList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            TouristSpot dataSpots = data.getValue(TouristSpot.class);

                            // set data
                            dataSpots.setKey(data.getKey());

                            // append to list
                            spotsList.add(dataSpots);
                            key = data.getKey();
                        }

                        if (spotsList.isEmpty() == true) {
                            rvContainer.setVisibility(View.GONE);
                            is_empty_list.setVisibility(View.VISIBLE);
                        } else {
                            adapter.setItems(spotsList);
                            adapter.notifyDataSetChanged();
                            rvContainer.setVisibility(View.VISIBLE);
                            is_empty_list.setVisibility(View.GONE);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        return;
                    }
                });
            }
        }, 1000);
    }

    private void getAllSpotsByProvince(String provinceName, Integer spinnerPos) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                crudTouristSpots.getAllApproved().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        spotsList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            TouristSpot dataSpots = data.getValue(TouristSpot.class);

                            if (dataSpots.getTouristSpotProvince().equals(provinceName)) {
                                // set data
                                dataSpots.setKey(data.getKey());

                                // append to list
                                spotsList.add(dataSpots);
                                key = data.getKey();
                            }
                        }

                        if (spotsList.isEmpty() == true) {
                            rvContainer.setVisibility(View.GONE);
                            is_empty_list.setVisibility(View.VISIBLE);
                        } else {
                            adapter.setItems(spotsList);
                            adapter.notifyDataSetChanged();
                            rvContainer.setVisibility(View.VISIBLE);
                            is_empty_list.setVisibility(View.GONE);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        return;
                    }
                });
            }
        }, 1000);
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