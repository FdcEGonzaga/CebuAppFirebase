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
import com.example.cebuapp.controllers.User.ManageFavorites.CRUDManageFavorites;
import com.example.cebuapp.model.Favorites;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoodFavoritesFragment extends Fragment {

    private FirebaseUser firebaseUser;
    private View view;
    private LinearLayout rvContainer;
    private RecyclerView recyclerView;
    private ImageButton fragmentClose;
    private DatabaseReference databaseReference;
    private String customPath, postCategory;

    private TextView is_empty_list;
    // online recyclerview
    private FoodFavoritesRVAdapter adapter;
    private String key = null;
    private CRUDManageFavorites crudFavorites;

    // sort manager
    private LinearLayoutManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_food_favorites, container, false);
        castComponents();
        setListeners();

        // ONLINE
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        customPath = "userID_"+firebaseUser.getUid()+"_faves";
        databaseReference = db.getReference(customPath);
        crudFavorites = new CRUDManageFavorites();
        postCategory = "food_areas";

        // showing the recycler view
        adapter = new FoodFavoritesRVAdapter(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        getFavorites();
        return view;
    }

    private void setListeners() {
        fragmentClose.setOnClickListener(v-> {
            getActivity().onBackPressed();
        });
    }

    private void castComponents() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fragmentClose = view.findViewById(R.id.fragment_close);
        rvContainer = view.findViewById(R.id.rv_container);
        recyclerView = view.findViewById(R.id.rv_contents);
        is_empty_list = view.findViewById(R.id.is_empty_list);
        manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
    }

    private void getFavorites() {
        crudFavorites.getAllFavorites(customPath, postCategory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Favorites> favList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Favorites favData = data.getValue(Favorites.class);

                    // set data
                    favData.setKey(data.getKey());

                    // append to list
                    favList.add(favData);
                    key = data.getKey();
                }

                if (favList.isEmpty() == true) {
                    recyclerView.setVisibility(View.GONE);
                    is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(favList);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
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