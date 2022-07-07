package com.example.cebuapp.controllers.User.FoodAreas;

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
import com.example.cebuapp.controllers.Admin.ManageJobPosts.CRUDManageJobPosts;
import com.example.cebuapp.controllers.User.ManageFavorites.CRUDManageFavorites;
import com.example.cebuapp.model.FoodArea;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodPostOwnedRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;

    // favoriting
    private Handler handler;
    private CRUDManageJobPosts crudJobPosts;
    private CRUDManageFavorites crudFavorites;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    private String postCategory;

    ArrayList<FoodArea> list = new ArrayList<>();

    public FoodPostOwnedRVAdapter(Context ctx) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item_food, parent, false);
        return new FoodVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FoodVH myHolder = (FoodVH) holder;
        FoodArea foodAreas = list.get(position);

        if (foodAreas.getFoodImg()!= null) {
            Picasso.get().load(foodAreas.getFoodImg()).into(myHolder.listImg);
        }
        myHolder.listTitle.setText(foodAreas.getFoodTitle());
        myHolder.listDesc1.setText(foodAreas.getFoodAddress());
        myHolder.listDesc2.setText(foodAreas.getFoodProvince());
        myHolder.listDesc3.setText(foodAreas.getApproved() == true ? "Status : Approved" : "Status : Not yet approved");
        myHolder.listMenu.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // View holder
    public class FoodVH extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView listImg;
        public TextView listTitle, listDesc1, listDesc2, listDesc3;
        ImageView listMenu, listMenu2;
        public FoodVH(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            listImg = itemView.findViewById(R.id.listImg);
            listTitle = itemView.findViewById(R.id.listTitle);
            listDesc1 = itemView.findViewById(R.id.listDesc1);
            listDesc2 = itemView.findViewById(R.id.listDesc2);
            listDesc3 = itemView.findViewById(R.id.listDesc3);
            listMenu = itemView.findViewById(R.id.listMenu);
            listMenu2 = itemView.findViewById(R.id.listMenu2);
        }
    }
}
