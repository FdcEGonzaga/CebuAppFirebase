package com.example.cebuapp.controllers.User.TouristSpots;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageTouristSpots.CRUDManageTouristSpots;
import com.example.cebuapp.controllers.User.ManageFavorites.CRUDManageFavorites;
import com.example.cebuapp.model.TouristSpot;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SpotsPostOwnedRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;

    // favoriting
    private Handler handler;
    private CRUDManageTouristSpots crudTouristSpots;
    private CRUDManageFavorites crudFavorites;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    private String postCategory;

    ArrayList<TouristSpot> list = new ArrayList<>();

    public SpotsPostOwnedRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<TouristSpot> touristSpot) {
        //append all data to arraylist
        if (!list.isEmpty()) {
            list.clear();
        }
        list.addAll(touristSpot);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item_spots, parent, false);
        return new SpotVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SpotVH myHolder = (SpotVH) holder;
        TouristSpot touristSpot = list.get(position);

        if (touristSpot.getTouristSpotImg()!= null) {
            Picasso.get().load(touristSpot.getTouristSpotImg()).into(myHolder.listImg);
        }
        myHolder.listTitle.setText(touristSpot.getTouristSpotTitle());
        myHolder.listDesc1.setText(touristSpot.getTouristSpotAddress());
        myHolder.listDesc2.setText(touristSpot.getTouristSpotProvince());
        myHolder.listDesc3.setText(touristSpot.getApproved() == true ? "Status : Approved" : "Status : Not yet approved");
        myHolder.favorite.setVisibility(View.GONE);
        myHolder.unfavorite.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // View holder
    public class SpotVH extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView listImg, favorite, unfavorite;
        public TextView listTitle, listDesc1, listDesc2, listDesc3;
        public SpotVH(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            listImg = itemView.findViewById(R.id.listImg);
            listTitle = itemView.findViewById(R.id.listTitle);
            listDesc1 = itemView.findViewById(R.id.listDesc1);
            listDesc2 = itemView.findViewById(R.id.listDesc2);
            listDesc3 = itemView.findViewById(R.id.listDesc3);
            favorite = itemView.findViewById(R.id.listMenu);
            unfavorite = itemView.findViewById(R.id.listMenu2);
        }
    }
}
