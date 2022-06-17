package com.example.cebuapp.controllers.User.TouristSpots;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;
import com.example.cebuapp.model.TouristSpot;

import java.io.Serializable;
import java.util.ArrayList;

public class SpotsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;

    ArrayList<TouristSpot> list = new ArrayList<>();

    public SpotsRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<TouristSpot> tourisSpots) {
        //append all data to arraylist
        if (!list.isEmpty()) {
            list.clear();
        }
        list.addAll(tourisSpots);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_spots_list_item, parent, false);
        return new SpotsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SpotsVH myHolder = (SpotsVH) holder;
        TouristSpot tourisSpots = list.get(position);

        // myHolder.listImg.setText(tourisSpots.getJobPostTitle());
        myHolder.listTitle.setText(tourisSpots.getTouristSpotTitle());
        myHolder.listDesc1.setText(tourisSpots.getTouristSpotAddress());
        myHolder.listDesc2.setText(tourisSpots.getTouristSpotProvince());
        myHolder.listDesc3.setText(tourisSpots.getTouristSpotPostedDate());
        // card view click listener
        myHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, SpotsDetailsActivity.class);
                intent.putExtra("FIREBASE_DATA", (Serializable) tourisSpots);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // View holder
    public class SpotsVH extends RecyclerView.ViewHolder {

        public ImageView listImg;
        public CardView cardView;
        public TextView listTitle, listDesc1, listDesc2, listDesc3;
        public SpotsVH(@NonNull View itemView) {
            super(itemView);
            listImg = itemView.findViewById(R.id.listImg);
            listTitle = itemView.findViewById(R.id.listTitle);
            listDesc1 = itemView.findViewById(R.id.listDesc1);
            listDesc2 = itemView.findViewById(R.id.listDesc2);
            listDesc3 = itemView.findViewById(R.id.listDesc3);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
