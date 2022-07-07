package com.example.cebuapp.controllers.User.FoodAreas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageFoodAreas.CRUDManageFoodAreas;
import com.example.cebuapp.controllers.User.FoodAreas.FoodPostOwnedRVAdapter;
import com.example.cebuapp.model.DatabaseHelper;
import com.example.cebuapp.model.FoodArea;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoodPostOwnedFragment extends Fragment {

    private FirebaseUser firebaseUser;
    private View view;
    private LinearLayout rvContainer;
    private RecyclerView recyclerView;
    private ImageButton fragmentClose;
    private DatabaseReference databaseReference;

    private TextView is_empty_list;
    // online recyclerview
    private FoodPostOwnedRVAdapter adapter;
    private String key = null;
    private CRUDManageFoodAreas crudFoodAreas;

    // sort manager
    private LinearLayoutManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_food_posts_owned, container, false);
        castComponents();
        setListeners();

        // ONLINE
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("cebuapp_food_areas");
        crudFoodAreas = new CRUDManageFoodAreas();

        // showing the recycler view
        adapter = new FoodPostOwnedRVAdapter(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        getOwnedFoodAreas();
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
        manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

    }

    private void getOwnedFoodAreas() {
        crudFoodAreas.getOwnedFoodAreas(firebaseUser.getEmail().trim()).addValueEventListener(new ValueEventListener() {
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
                    is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(foodList);
                    adapter.notifyDataSetChanged();
                    is_empty_list.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}