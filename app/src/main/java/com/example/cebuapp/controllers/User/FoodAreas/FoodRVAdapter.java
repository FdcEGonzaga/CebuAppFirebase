package com.example.cebuapp.controllers.User.FoodAreas;

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
import com.example.cebuapp.model.FoodArea;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;

    ArrayList<FoodArea> list = new ArrayList<>();

    public FoodRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<FoodArea> foodAreas) {
        //append all data to arraylist
        if (!list.isEmpty()) {
            list.clear();
        }
        list.addAll(foodAreas);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_food_list_item, parent, false);
        return new FoodVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FoodVH myHolder = (FoodVH) holder;
        FoodArea foodAreas = list.get(position);

        // myHolder.listImg.setText(foodAreas.getJobPostTitle());
        myHolder.listTitle.setText(foodAreas.getFoodTitle());
        myHolder.listDesc1.setText(foodAreas.getFoodAddress());
        myHolder.listDesc2.setText(foodAreas.getFoodProvince());
        myHolder.listDesc3.setText(foodAreas.getFoodPosted());
        // card view click listener
        myHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, FoodDetailsActivity.class);
                intent.putExtra("FIREBASE_DATA", (Serializable) foodAreas);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // View holder
    public class FoodVH extends RecyclerView.ViewHolder {

        public ImageView listImg;
        public CardView cardView;
        public TextView listTitle, listDesc1, listDesc2, listDesc3;
        public FoodVH(@NonNull View itemView) {
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
