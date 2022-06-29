package com.example.cebuapp.controllers.Admin.ManageProvinces;

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
import com.example.cebuapp.model.Province;
import com.example.cebuapp.controllers.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageProvincesActivity extends AppCompatActivity {
    private TextView isEmptyList, province_name_label;
    private Button province_action_btn, cancel_btn;
    private LinearLayout add_edit_form;
    private EditText province_input_name;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ManageProvincesRVAdapter adapter;
    private boolean isLoadMore = false;
    private String key = null;
    private CRUDManageProvinces crudProv;
    private ProgressDialog dialog;
    private ImageButton backBtn, addNewBtn;
    private Intent intent;
    private Province editProvince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_provinces);

        if (isConnectedToInternet()) {
            // cast components
            castComponents();
            setListeners();
            loadData();

            // check intent extra for editing province
            editProvince = (Province) getIntent().getSerializableExtra("provinceEdit");
            if (editProvince != null) {
                province_input_name.setText(editProvince.getProvinceName());
                province_input_name.requestFocus();
                province_name_label.setText("Editing Province name:");
                province_action_btn.setText("EDIT");
                // show form
                add_edit_form.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
                // hide main btns
                addNewBtn.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            HelperUtilities.showNoInternetAlert(ManageProvincesActivity.this);
        }
    }

    private void castComponents() {
        province_input_name = findViewById(R.id.province_input_name);
        province_action_btn = findViewById(R.id.province_action_btn);
        province_name_label = findViewById(R.id.province_name_label);
        isEmptyList = findViewById(R.id.is_empty_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.rv);

        // form
        add_edit_form = findViewById(R.id.add_edit_form);

        // btns
        addNewBtn = findViewById(R.id.add_new_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
        backBtn = findViewById(R.id.back_btn);

        // init firebase DB
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("cebuapp_provinces");

        // load crudProv
        crudProv = new CRUDManageProvinces();
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        // showing province recycler view
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ManageProvincesRVAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setListeners() {
        // add new btn
        addNewBtn.setOnClickListener(v -> {
            // hide main btns
            addNewBtn.setVisibility(View.INVISIBLE);
            backBtn.setVisibility(View.INVISIBLE);
            // show form and hide rv
            add_edit_form.setVisibility(View.VISIBLE);
            province_name_label.setText("Add a Province Name:");
            province_input_name.setText("");
            province_input_name.requestFocus();
            province_action_btn.setText("ADD");
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
            intent = new Intent(ManageProvincesActivity.this, HomeActivity.class);
            startActivity(intent);
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

        // province save/edit btn is clicked
        province_action_btn.setOnClickListener(v -> {
            // get value
            String provinceNameVal = province_input_name.getText().toString().trim().toUpperCase();

            // construct data for saving
            Province provinceData = new Province(provinceNameVal);

            // validate input
            if (provinceNameVal.isEmpty()) {
                province_input_name.setError("Province name is required.");
                province_input_name.requestFocus();
                return;
            }

            // update provinceData
            if (editProvince != null && province_action_btn.getText().equals("EDIT")) {
                // check if province name was edited
                if (provinceNameVal.equals(editProvince.getProvinceName().trim())) {
                    province_input_name.setError("Please make changes with the Province Name.");
                    province_input_name.requestFocus();
                    return;
                }

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("provinceName", provinceNameVal);
                crudProv.updateProvinceName(editProvince.getKey(), hashMap).addOnSuccessListener(suc -> {
                    Toast.makeText(ManageProvincesActivity.this,
                            "Province was updated successfully!", Toast.LENGTH_SHORT).show();

                    // reset to adding form
                    province_name_label.setText("Add a Province Name:");
                    province_input_name.setText("");
                    province_action_btn.setText("Add");
                    province_input_name.clearFocus();
                    add_edit_form.setVisibility(View.GONE);
                    // show main btns
                    addNewBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);

                }).addOnFailureListener(fail -> {
                    Toast.makeText(ManageProvincesActivity.this,
                            "Province updating failed, please try again.", Toast.LENGTH_LONG).show();
                });

                // add new prov
            } else {
                crudProv.addProvince(provinceData).addOnSuccessListener(suc -> {
                    Toast.makeText(ManageProvincesActivity.this,
                            "Province name was added successfully!", Toast.LENGTH_SHORT).show();
                    province_input_name.setText("");
                    add_edit_form.setVisibility(View.GONE);
                    // show main btns
                    addNewBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);

                }).addOnFailureListener(fail -> {
                    Toast.makeText(ManageProvincesActivity.this,
                            "Province name adding failed, please try again.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadData() {
        dialog.setTitle("Fetching all Cebu Provinces.");
        dialog.show();
        crudProv.getProvinceData(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Province> provincesList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Province manageProvince = data.getValue(Province.class);

                    // set province data
                    manageProvince.setKey(data.getKey());

                    // append to list
                    provincesList.add(manageProvince);
                    key = data.getKey();
                }

                if (provincesList.isEmpty() == true) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    isEmptyList.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(provincesList);
                    adapter.notifyDataSetChanged();
                    isLoadMore = false;
                    swipeRefreshLayout.setRefreshing(false);
                    isEmptyList.setVisibility(View.GONE);
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
        Intent intent = new Intent(ManageProvincesActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}