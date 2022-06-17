package com.example.cebuapp.controllers.User.TouristSpots;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageTouristSpots.CRUDManageTouristSpots;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.model.TouristSpot;
import com.example.cebuapp.model.TouristSpot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpotsActivity extends AppCompatActivity {
    private Intent intent;
    private TextView is_empty_list;

    // spinnner
    private Spinner provinceSpinner;
    private DatabaseReference provinceSpinnerRef;
    private ArrayList<String> provinceSpinnerList;
    private ArrayAdapter<String> provinceSpinnerAdapter;

    // online recyclerview
    private RecyclerView recyclerView;
    private LinearLayout rvContainer;
    private DatabaseReference databaseReference;
    private SpotsRVAdapter adapter;
    private String key = null;
    private CRUDManageTouristSpots crudTouristSpots;
    private ProgressDialog dialog;
    private ImageButton backBtn;

    @Override
    public void onStart() {
        super.onStart();

        dialog.setTitle("Getting the latest Tourist Spots for you...");
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spots);

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        is_empty_list = findViewById(R.id.is_empty_list);
        recyclerView = findViewById(R.id.spots_rv);
        rvContainer = findViewById(R.id.rv_container);
        backBtn = findViewById(R.id.back_btn);
        is_empty_list.setVisibility(View.INVISIBLE);

        if (isConnectedToInternet()) {
            // ONLINE
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            databaseReference = db.getReference("cebuapp_tourist_spots");
            crudTouristSpots = new CRUDManageTouristSpots();

            // set categories spinner
            onlineProvinceSpinner();

            // showing the recycler view
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(manager);
            adapter = new SpotsRVAdapter(this);
            recyclerView.setAdapter(adapter);

        } else {
            Toast.makeText(this, "Please connect to the Internet.", Toast.LENGTH_SHORT).show();
        }

        // back to home btn
        backBtn.setOnClickListener(v-> {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });
    }

    private void onlineProvinceSpinner() {
        provinceSpinner = findViewById(R.id.food_spinner);
        provinceSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_provinces");
        provinceSpinnerList = new ArrayList<>();
        provinceSpinnerAdapter = new ArrayAdapter<String>(SpotsActivity.this, android.R.layout.simple_spinner_dropdown_item,
                provinceSpinnerList);
        provinceSpinner.setAdapter(provinceSpinnerAdapter);

        // supply data to spinner
        provinceSpinnerList.add("ALL");
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
                Toast.makeText(SpotsActivity.this, "Unable to pull Cebu provinces", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        // spinner listener for province
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get selected position of spinner
                String selectedCategory = provinceSpinner.getSelectedItem().toString();
                dialog.setTitle("Fetching " + selectedCategory + "'s tourist spots.");
                dialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnectedToInternet()) {
                            // ONLINE
                            if (selectedCategory.equals("ALL")) {
                                getAllApprovedSpots();
                            } else {
                                getAllSpotsByProvince(selectedCategory);

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

    private void getAllApprovedSpots() {
        crudTouristSpots.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TouristSpot> spotsList = new ArrayList<>();
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

    private void getAllSpotsByProvince(String provinceName) {
        crudTouristSpots.getByProvince(provinceName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TouristSpot> spotsList = new ArrayList<>();
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

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

}