package com.example.cebuapp.controllers.User.TouristSpots;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageTouristSpots.CRUDManageTouristSpots;
import com.example.cebuapp.controllers.User.TouristSpots.SpotsPostOwnedRVAdapter;
import com.example.cebuapp.model.TouristSpot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpotsPostOwnedFragment extends Fragment {

    private FirebaseUser firebaseUser;
    private View view;
    private LinearLayout rvContainer;
    private RecyclerView recyclerView;
    private ImageButton fragmentClose;
    private DatabaseReference databaseReference;

    private TextView is_empty_list;
    // online recyclerview
    private SpotsPostOwnedRVAdapter adapter;
    private String key = null;
    private CRUDManageTouristSpots crudTouristSpots;

    // sort manager
    private LinearLayoutManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_spots_post_owned, container, false);
        castComponents();
        setListeners();

        // ONLINE
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("cebuapp_tourist_spots");
        crudTouristSpots = new CRUDManageTouristSpots();

        // showing the recycler view
        adapter = new SpotsPostOwnedRVAdapter(getActivity().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        getAllSpots();
        return view;
    }

    private void setListeners() {
        fragmentClose.setOnClickListener(v-> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
    }

    private void castComponents() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fragmentClose = view.findViewById(R.id.fragment_close);
        rvContainer = view.findViewById(R.id.rv_container);
        recyclerView = view.findViewById(R.id.jp_rv);
        is_empty_list = view.findViewById(R.id.is_empty_list);
        manager = new LinearLayoutManager(getActivity().getApplicationContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

    }

    private void getAllSpots() {
        crudTouristSpots.getOwnedTouristSpots(firebaseUser.getEmail().trim()).addValueEventListener(new ValueEventListener() {
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
                    is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    is_empty_list.setVisibility(View.GONE);
                    adapter.setItems(spotsList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;
            }
        });
    }
}