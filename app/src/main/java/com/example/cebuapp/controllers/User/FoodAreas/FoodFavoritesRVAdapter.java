package com.example.cebuapp.controllers.User.FoodAreas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.User.ManageFavorites.CRUDManageFavorites;
import com.example.cebuapp.model.Favorites;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodFavoritesRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;

    // favoriting
    private Handler handler;
    private CRUDManageFavorites crudFavorites;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    private String postCategory;
    private FoodRVAdapter foodRVAdapter;

    ArrayList<Favorites> list = new ArrayList<>();

    public FoodFavoritesRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<Favorites> favoriteData) {
        //append all data to arraylist
        if (!list.isEmpty()) {
            list.clear();
        }
        list.addAll(favoriteData);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item_food, parent, false);
        return new FoodFavoritesRVAdapter.FavoritesVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FoodFavoritesRVAdapter.FavoritesVH myHolder = (FoodFavoritesRVAdapter.FavoritesVH) holder;
        Favorites favoriteData = list.get(position);

        if (favoriteData.getPostImg()!= null) {
            Picasso.get().load(favoriteData.getPostImg()).into(myHolder.listImg);
        }
        myHolder.listTitle.setText(favoriteData.getPostDesc1());
        myHolder.listDesc1.setText(favoriteData.getPostDesc2());
        myHolder.listDesc2.setText(favoriteData.getPostDesc3());
        myHolder.listDesc3.setText(favoriteData.getPostDesc4());
        myHolder.blankHeart.setVisibility(View.GONE);
        myHolder.heart.setVisibility(View.VISIBLE);

        // card favorite menu listener
        handler = new Handler();
        crudFavorites = new CRUDManageFavorites();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        String customPath = "userID_"+currentUser.getUid()+"_faves";
        databaseReference = db.getReference(customPath.trim());
        postCategory = "food_areas";
        foodRVAdapter = new FoodRVAdapter(context);

        // remove to favorites
        myHolder.heart.setOnClickListener(v-> {
            new AlertDialog.Builder(context)
                    .setTitle("CebuApp")
                    .setMessage("Do you really want to remove Food Area title '" + favoriteData.getPostDesc1() + "' from your Favorites?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            crudFavorites.removeToFavorite(customPath, favoriteData.getKey()).addOnSuccessListener(suc -> {
                                Toast.makeText(context,
                                        "Removed Food Area from your Favorites successfully!", Toast.LENGTH_LONG).show();
                                foodRVAdapter.notifyDataSetChanged();
                            }).addOnFailureListener(fail -> {
                                Toast.makeText(context,
                                        "Failed removing from your Favorites, please try again." + fail.getMessage(), Toast.LENGTH_LONG).show();
                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // View holder
    public class FavoritesVH extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView listImg, blankHeart, heart;
        public TextView listTitle, listDesc1, listDesc2, listDesc3;
        public FavoritesVH(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            listImg = itemView.findViewById(R.id.listImg);
            listTitle = itemView.findViewById(R.id.listTitle);
            listDesc1 = itemView.findViewById(R.id.listDesc1);
            listDesc2 = itemView.findViewById(R.id.listDesc2);
            listDesc3 = itemView.findViewById(R.id.listDesc3);
            blankHeart = itemView.findViewById(R.id.listMenu);
            heart = itemView.findViewById(R.id.listMenu2);
        }
    }
}
