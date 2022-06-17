package com.example.cebuapp.controllers.Admin.ManageTouristSpots;

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
import com.example.cebuapp.model.TouristSpot;
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

public class ManageTouristSpotsActivity extends AppCompatActivity {
    // form
    private TextView form_title, title_label, desc_label, address_label, province_label,
            contact_email_label, contact_num_label, is_empty_list;
    private EditText title_input, desc_input, address_input, contact_email_input, contact_num_input;
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
    private DatabaseReference databaseReference;
    private ManageTouristSpotsRVAdapter adapter;
    private CRUDManageTouristSpots crudTouristSpots;

    // others
    private ProgressDialog dialog;
    private ImageButton backBtn, addNewBtn;
    private Intent intent;
    private boolean isLoadMore = false;
    private String key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_touristspots);

        // cast components
        castComponents();

        // spinner
        setProvinceSpinner();

        // init firebase DB
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("cebuapp_food_areas");

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
        TouristSpot touristSpotEdit = (TouristSpot) getIntent().getSerializableExtra("touristSpotEdit");
        if (touristSpotEdit != null) {
            // show form and hide rv
            add_edit_form.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            addNewBtn.setVisibility(View.INVISIBLE);
            backBtn.setVisibility(View.INVISIBLE);

            // reuse form for editing
            form_title.setText("Editing a Tourist Spot");
            title_input.setText(touristSpotEdit.getTouristSpotTitle());
            desc_input.setText(touristSpotEdit.getTouristSpotDescription());
            address_input.setText(touristSpotEdit.getTouristSpotAddress());
            contact_email_input.setText(touristSpotEdit.getTouristSpotContactEmail());
            contact_num_input.setText(touristSpotEdit.getTouristSpotContactNum());
            title_input.requestFocus();
            action_btn.setText("EDIT");
        }

        // job field add/edit btn is clicked
        action_btn.setOnClickListener(v -> {

            // get value
            String tpTitleVal = title_input.getText().toString().trim();
            String tpDescVal = desc_input.getText().toString().trim();
            String tpAddressVal = address_input.getText().toString().trim();
            String tpProvinceVal = provinceSpinner.getSelectedItem().toString().trim();
            String tpEmailVal = contact_email_input.getText().toString().trim();
            String tpContactNumVal = contact_num_input.getText().toString().trim();

            // validate inputs
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

            if (tpEmailVal.isEmpty()) {
                contact_email_input.setError("Tourist Spot email is required.");
                contact_email_input.requestFocus();
                return;
            }

            if (tpContactNumVal.isEmpty()) {
                contact_email_input.setError("Tourist Spot number is required.");
                contact_num_input.requestFocus();
                return;
            }

            // construct data
            String tpPostedVal = getCurrentDate();
            TouristSpot touristSpotData = new TouristSpot(tpAddressVal, tpEmailVal, tpContactNumVal,
                    tpDescVal, tpPostedVal, tpProvinceVal, tpTitleVal);

            // update
            if (touristSpotEdit != null && action_btn.getText().equals("EDIT")) {
                // check if title was edited
                if (tpTitleVal.equals(touristSpotEdit.getTouristSpotTitle().trim())) {
                    title_input.setError("Please update the food area's province.");
                    title_input.requestFocus();
                    return;
                }

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("touristSpotAddress", tpAddressVal);
                hashMap.put("touristSpotContactEmail", tpEmailVal);
                hashMap.put("touristSpotContactNum", tpContactNumVal);
                hashMap.put("touristSpotDescription", tpDescVal);
                hashMap.put("touristSpotPosted", tpPostedVal);
                hashMap.put("touristSpotProvince", tpProvinceVal);
                hashMap.put("touristSpotTitle", tpTitleVal);
                crudTouristSpots.update(touristSpotEdit.getKey(), hashMap).addOnSuccessListener(suc -> {
                    Toast.makeText(ManageTouristSpotsActivity.this,
                            "Tourist Spot was updated successfully!", Toast.LENGTH_SHORT).show();

                    // reset to adding form
                    title_label.setText("Add a Tourist Spot:");
                    title_input.setText("");
                    action_btn.setText("Add");
                    title_input.clearFocus();
                    add_edit_form.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    addNewBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);

                }).addOnFailureListener(fail -> {
                    Toast.makeText(ManageTouristSpotsActivity.this,
                            "Tourist Spot updating failed, please try again.", Toast.LENGTH_LONG).show();
                });

            // insert or add new prov
            } else {
                crudTouristSpots.add(touristSpotData).addOnSuccessListener(suc -> {
                    Toast.makeText(ManageTouristSpotsActivity.this,
                            "Tourist Spot was added successfully!", Toast.LENGTH_SHORT).show();
                    title_input.setText("");
                    // hide form and show data
                    add_edit_form.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    // show main btns
                    addNewBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);

                }).addOnFailureListener(fail -> {
                    Toast.makeText(ManageTouristSpotsActivity.this,
                            "Tourist Spot adding failed, please try again.", Toast.LENGTH_LONG).show();
                });
            }
        });
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
        title_input = findViewById(R.id.title_input);
        desc_input = findViewById(R.id.desc_input);
        address_input = findViewById(R.id.address_input);
        contact_email_input = findViewById(R.id.contact_email_input);
        contact_num_input = findViewById(R.id.contact_num_input);
        provinceSpinner = findViewById(R.id.province_spinner);

        // others
        action_btn = findViewById(R.id.action_btn);
        is_empty_list = findViewById(R.id.is_empty_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.rv);

        // btns
        cancel_btn = findViewById(R.id.cancel_btn);
        addNewBtn = findViewById(R.id.add_new_btn);
        backBtn = findViewById(R.id.back_btn);

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

    private void setProvinceSpinner() {
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
    }

    private String getCurrentDate() {
        Date calendar = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return df.format(calendar);
    }

    private void loadData() {
        dialog.setTitle("Fetching all Cebu Tourist Spots.");
        dialog.show();
        crudTouristSpots.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TouristSpot> foodAreasList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    TouristSpot manageFoodAreas = data.getValue(TouristSpot.class);

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

    @Override
    public void onBackPressed() {
        // go back to admin homepage
        Intent intent = new Intent(ManageTouristSpotsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}