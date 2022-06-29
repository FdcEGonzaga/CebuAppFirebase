package com.example.cebuapp.controllers.User.FoodAreas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageFoodAreas.CRUDManageFoodAreas;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.controllers.User.TouristSpots.SpotsActivity;
import com.example.cebuapp.model.FoodArea;
import com.example.cebuapp.Helper.HelperUtilities;
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
import java.util.ArrayList;

public class FoodActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private Intent intent;
    private TextView is_empty_list;
    private androidx.appcompat.widget.SearchView searchView;
    private Handler handler;
    private Boolean errors;

    // spinnner
    private Spinner mainProvinceSpinner;
    private DatabaseReference mainProvinceSpinnerRef;
    private ArrayList<String> mainProvinceSpinnerList;
    private ArrayAdapter<String> mainProvinceSpinnerAdapter;

    // online recyclerview
    private RecyclerView recyclerView;
    private LinearLayout rvContainer;
    private DatabaseReference databaseReference;
    private FoodRVAdapter adapter;
    private String key = null, selectedCategory;
    private CRUDManageFoodAreas crudFoodAreas;
    private ProgressDialog dialog;

    // header
    private ImageButton backBtn, addBtn;
    private Boolean isAdding = false;

    // LinearLayout
    private LinearLayout mainBody;

    // add form
    private FoodArea editFoodArea;
    private ScrollView addFormBody;
    private Button formAddBtn, formCancelBtn;
    private EditText title_input, desc_input, address_input, contact_email_input, contact_num_input;
    private String foodAreaImgVal, foodAreaTitleVal, foodAreaDescVal, foodAreaAddressVal, foodAreaProvinceVal, foodAreaContactEmailVal,
            foodAreaContactNumVal, foodAreaPostedVal;
    private Boolean foodAreaApproveVal;

    // province spinner
    private Spinner province_spinner;
    private DatabaseReference provinceSpinnerRef;
    private ArrayList<String> provinceSpinnerList;
    private ArrayAdapter<String> provinceSpinnerAdapter;

    // Image uploading
    private Uri imageUri;
    private ImageView img_input;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

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
            databaseReference = db.getReference("cebuapp_food_areas");
            crudFoodAreas = new CRUDManageFoodAreas();
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(manager);
            adapter = new FoodRVAdapter(this);
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

    private void formSpinnerComponents() {
        // province spinner
        provinceSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_provinces");
        provinceSpinnerList = new ArrayList<>();
        provinceSpinnerAdapter = new ArrayAdapter<String>(FoodActivity.this,
                android.R.layout.simple_spinner_dropdown_item, provinceSpinnerList);
        province_spinner.setAdapter(provinceSpinnerAdapter);

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

    private void castComponents() {
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        handler = new Handler();

        // form inputs
        img_input = findViewById(R.id.img_input);
        title_input = findViewById(R.id.title_input);
        desc_input = findViewById(R.id.desc_input);
        address_input = findViewById(R.id.address_input);
        contact_email_input = findViewById(R.id.contact_email_input);
        contact_num_input = findViewById(R.id.contact_num_input);
        province_spinner = findViewById(R.id.province_spinner);

        addFormBody = findViewById(R.id.add_form);
        formAddBtn = findViewById(R.id.form_add_btn);
        formCancelBtn = findViewById(R.id.form_cancel_btn);

        backBtn = findViewById(R.id.back_btn);
        addBtn = findViewById(R.id.add_btn);
        mainBody = findViewById(R.id.main_body);
        is_empty_list = findViewById(R.id.is_empty_list);
        recyclerView = findViewById(R.id.food_rv);
        rvContainer = findViewById(R.id.rv_container);
        searchView = findViewById(R.id.search_view);
        is_empty_list.setVisibility(View.INVISIBLE);
    }

    private void setListeners() {
        img_input.setOnClickListener(v-> {
            choosingOfPicture();
        });


        // adding food area
        formAddBtn.setOnClickListener(v -> {
            // get value
            foodAreaTitleVal = title_input.getText().toString().trim();
            foodAreaDescVal = desc_input.getText().toString().trim();
            foodAreaAddressVal = address_input.getText().toString().trim();
            foodAreaProvinceVal = province_spinner.getSelectedItem().toString().trim();
            foodAreaContactEmailVal = contact_email_input.getText().toString().trim();
            foodAreaContactNumVal = contact_num_input.getText().toString().trim();
            validateInputs();

            if (errors.equals(false)) {
                // save data ang upload image
                storageReference = FirebaseStorage.getInstance().getReference("Images");
                saveDataAndUploadImage(editFoodArea);
            }
        });

        // cancel form food area
        formCancelBtn.setOnClickListener(v -> {
            isCancelAdding();
        });

        // header add tourist spot
        addBtn.setOnClickListener(v-> {
            addBtn.setVisibility(View.INVISIBLE);
            backBtn.setVisibility(View.INVISIBLE);
            mainBody.setVisibility(View.GONE);
            addFormBody.setVisibility(View.VISIBLE);
            isAdding = true;
        });

        // header back to home btn
        backBtn.setOnClickListener(v-> {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });


    }

    private void validateInputs() {
        errors = false;

        // validate inputs
        if (foodAreaTitleVal.isEmpty()) {
            title_input.setError("Food Area title is required.");
            title_input.requestFocus();
            errors = true;
            return;
        }

        if (foodAreaDescVal.isEmpty()) {
            desc_input.setError("Food Area description is required.");
            desc_input.requestFocus();
            errors = true;
            return;
        }

        if (foodAreaAddressVal.isEmpty()) {
            address_input.setError("Food Area landmark is required.");
            address_input.requestFocus();
            errors = true;
            return;
        }

        if (foodAreaContactEmailVal.isEmpty()) {
            contact_email_input.setError("Food Area email is required.");
            contact_email_input.requestFocus();
            errors = true;
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(foodAreaContactEmailVal).matches()) {
            contact_email_input.setError("Please provide a valid email.");
            contact_email_input.requestFocus();
            errors = true;
            return;
        }

        if (foodAreaContactNumVal.isEmpty()) {
            contact_num_input.setError("Food Area number is required.");
            contact_num_input.requestFocus();
            errors = true;
            return;
        }

        if (!HelperUtilities.isValidPhone(foodAreaContactNumVal)) {
            contact_num_input.setError("Food Area number is invalid.");
            contact_num_input.requestFocus();
            errors = true;
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
        dialog.setTitle("Submitting your Food Area entry...");
        dialog.show();

        if (imageUri != null) {
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() +'.'+ GetFileExtension(imageUri));
            UploadTask uploadTask = storageReference2.putFile(imageUri);
            uploadTask.addOnSuccessListener((OnSuccessListener) (taskSnapshot) -> {
                // get the img uri
                Task<Uri> downloadUri = storageReference2.getDownloadUrl();

                downloadUri.addOnSuccessListener((OnSuccessListener)(uri) -> {
                    // construct img data
                    foodAreaApproveVal = false;
                    foodAreaImgVal = uri.toString();
                    foodAreaPostedVal = HelperUtilities.getCurrentDate();
                    String foodAuthorVal = firebaseUser.getEmail();;
                    Integer spinnerPos = 0;

                    FoodArea foodAreasData = new FoodArea(foodAreaApproveVal, foodAuthorVal, foodAreaImgVal, foodAreaAddressVal, foodAreaContactEmailVal, foodAreaContactNumVal,
                            foodAreaDescVal, foodAreaPostedVal, foodAreaProvinceVal, foodAreaTitleVal, spinnerPos);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            crudFoodAreas.add(foodAreasData).addOnSuccessListener(suc -> {
                                // reset form
                                resetFormInputs();
                                // delay showing of msg
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        HelperUtilities.showOkAlert(FoodActivity.this,
                                            "Your Food Area entry was submitted successfully, our Admin will review your entry and post it once approved. Thanks!");
                                    }
                                }, 300);

                            }).addOnFailureListener(fail -> {
                                HelperUtilities.showOkAlert(FoodActivity.this,
                                        "Adding failed, please try again.");
                            });
                            dialog.dismiss();
                        }
                    }, 300);
                });
            });
        } else {
            dialog.dismiss();
            Toast.makeText(this, "Please select an Image.", Toast.LENGTH_SHORT).show();
            img_input.setFocusable(true);
            img_input.requestFocus();
        }
    }

    private void resetFormInputs() {
        // reset form
        title_input.setText("");
        title_input.setError(null);
        desc_input.setText("");
        desc_input.setError(null);
        address_input.setText("");
        address_input.setError(null);
        contact_email_input.setText("");
        contact_email_input.setError(null);
        contact_num_input.setText("");
        contact_num_input.setError(null);

        // hide form and show data
        addFormBody.setVisibility(View.GONE);
        mainBody.setVisibility(View.VISIBLE);

        // show main btns
        addBtn.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
    }

    private void isCancelAdding() {
        new AlertDialog.Builder(FoodActivity.this)
                .setTitle("CebuApp")
                .setMessage("Are you sure you want to cancel submitting your Food Area entry?")
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

    private void mainProvinceSpinner() {
        mainProvinceSpinner = findViewById(R.id.food_spinner);
        mainProvinceSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_provinces");
        mainProvinceSpinnerList = new ArrayList<>();
        mainProvinceSpinnerAdapter = new ArrayAdapter<String>(FoodActivity.this, android.R.layout.simple_spinner_dropdown_item,
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
                Toast.makeText(FoodActivity.this, "Unable to pull Cebu provinces", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        // spinner listener for province
        mainProvinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get selected position of spinner
                selectedCategory = mainProvinceSpinner.getSelectedItem().toString();
                dialog.setTitle("Fetching " + selectedCategory + " Food Area in Cebu.");
                dialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnectedToInternet()) {
                            // ONLINE
                            if (selectedCategory.equals("ALL")) {
                                getAllFoodAreas();
                            } else {
                                getFoodAreasByProvince(selectedCategory, position);
                            }
                        } else {
                            Toast.makeText(FoodActivity.this, "Please connect to the Internet.", Toast.LENGTH_SHORT).show();
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
                dialog.setTitle("Searching '" + query + "' from '" + selectedProvince + "' job category");
                dialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        crudFoodAreas.getSearchedWord(query).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<FoodArea> foodList = new ArrayList<>();
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    FoodArea searchData = data.getValue(FoodArea.class);

                                    // get only approved food areas
                                    if (
                                            searchData.getApproved().equals(true)
                                                    &&  searchData.getFoodProvince().equals(selectedProvince)
                                    ) {
                                        // set data
                                        searchData.setKey(data.getKey());

                                        // append to list
                                        foodList.add(searchData);
                                        key = data.getKey();
                                    }
                                }

                                if (foodList.isEmpty() == true) {
                                    rvContainer.setVisibility(View.GONE);
                                    is_empty_list.setVisibility(View.VISIBLE);
                                } else {
                                    adapter.setItems(foodList);
                                    adapter.notifyDataSetChanged();
                                    rvContainer.setVisibility(View.VISIBLE);
                                    is_empty_list.setVisibility(View.GONE);
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(FoodActivity.this, "Error occured.", Toast.LENGTH_SHORT).show();
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
                    }, 300);
                }
                return false;
            }
        });
    }

    private void getAllFoodAreas() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                crudFoodAreas.getAllApproved().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<FoodArea> foodList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            FoodArea foodAreaData = data.getValue(FoodArea.class);

                            // set data
                            foodAreaData.setKey(data.getKey());

                            // append to list
                            foodList.add(foodAreaData);
                            key = data.getKey();
                        }

                        if (foodList.isEmpty() == true) {
                            rvContainer.setVisibility(View.GONE);
                            is_empty_list.setVisibility(View.VISIBLE);
                        } else {
                            adapter.setItems(foodList);
                            adapter.notifyDataSetChanged();
                            rvContainer.setVisibility(View.VISIBLE);
                            is_empty_list.setVisibility(View.GONE);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FoodActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 1000);

    }

    private void getFoodAreasByProvince(String provinceName, Integer spinnerPos) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                crudFoodAreas.getAllApproved().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<FoodArea> foodList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            FoodArea foodAreaData = data.getValue(FoodArea.class);

                            if (foodAreaData.getFoodProvince().equals(provinceName)) {
                                // set data
                                foodAreaData.setKey(data.getKey());
                                foodAreaData.setSpinnerPos(spinnerPos);

                                // append to list
                                foodList.add(foodAreaData);
                                key = data.getKey();
                            }
                        }

                        if (foodList.isEmpty() == true) {
                            rvContainer.setVisibility(View.GONE);
                            is_empty_list.setVisibility(View.VISIBLE);
                        } else {
                            adapter.setItems(foodList);
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
        super.onBackPressed();
        startActivity(new Intent(new Intent(getApplicationContext(), HomeActivity.class)));
        finish();
    }
}
