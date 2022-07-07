package com.example.cebuapp.controllers.User.FoodAreas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.User.ManageFavorites.CRUDManageFavorites;
import com.example.cebuapp.model.Favorites;
import com.example.cebuapp.model.FoodArea;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    // favoriting
    private Handler handler;
    private CRUDManageFavorites crudFavorites;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    private String postCategory;

    ArrayList<FoodArea> list = new ArrayList<>();
    ArrayList<String> listFav = new ArrayList<String>();

    public FoodRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<FoodArea> foodAreas) {
        // clear the list first
        if (!list.isEmpty()) {
            list.clear();
        }
        // append all data
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
        myHolder.listDesc3.setText(foodAreas.getFoodPosted());

        // card blankHeart menu listener
        handler = new Handler();
        crudFavorites = new CRUDManageFavorites();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        String customPath = "userID_"+currentUser.getUid()+"_faves";
        databaseReference = db.getReference(customPath.trim());
        postCategory = "food_areas";

        // check if added to blankHearts already
        crudFavorites.getAllFavorites(customPath, postCategory).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFav.clear();
                for (DataSnapshot uniqueKey: snapshot.getChildren()) {
                    listFav.add(uniqueKey.getKey());
                }

                if (listFav.contains(foodAreas.getKey())) {
                    myHolder.heart.setVisibility(View.VISIBLE);
                    myHolder.blankHeart.setVisibility(View.GONE);
                } else {
                    myHolder.heart.setVisibility(View.GONE);
                    myHolder.blankHeart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error=" + error, Toast.LENGTH_SHORT).show();
            }
        });

        // add to favorites
        myHolder.blankHeart.setOnClickListener(v-> {
            Favorites favData = new Favorites(postCategory, foodAreas.getFoodImg(), foodAreas.getFoodTitle(), foodAreas.getFoodAddress(),
                    foodAreas.getFoodProvince(), foodAreas.getFoodPosted());
            databaseReference.child(foodAreas.getKey()).setValue(favData)
                    .addOnSuccessListener(suc -> {
                        HelperUtilities.showOkAlert(context,
                                "Added to your Favorite Food Area successfully!");
                        myHolder.blankHeart.setVisibility(View.GONE);
                        myHolder.heart.setVisibility(View.VISIBLE);

                    }).addOnFailureListener(fail -> {
                        HelperUtilities.showOkAlert(context,
                                "Failed adding to your Favorite Food Area, please try again.");
                    });
        });

        // remove to favorites
        myHolder.heart.setOnClickListener(v-> {
            new AlertDialog.Builder(context)
                .setTitle("CebuApp")
                .setMessage("Do you really want to remove Food Area title '" + foodAreas.getFoodTitle() + "' from your Favorites?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        crudFavorites.removeToFavorite(customPath, foodAreas.getKey()).addOnSuccessListener(suc -> {
                            Toast.makeText(context,
                                    "Removed Food Area from your Favorites successfully!", Toast.LENGTH_LONG).show();
                            myHolder.blankHeart.setVisibility(View.VISIBLE);
                            myHolder.heart.setVisibility(View.GONE);

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

        // card view click listener
        myHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodDetailsFragment foodDetailsFragment = new FoodDetailsFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, foodDetailsFragment);
                fragmentTransaction.commit();

                // send data to fragment
                Bundle bundle = new Bundle();
                bundle.putString("detailImg",foodAreas.getFoodImg());
                bundle.putString("detailTitle",foodAreas.getFoodTitle());
                bundle.putString("detailLandmark",foodAreas.getFoodAddress());
                bundle.putString("detailProvince", foodAreas.getFoodProvince() + ", CEBU");
                bundle.putString("detailDesc",foodAreas.getFoodDescription());
                bundle.putString("detailContact",foodAreas.getFoodContactNum());
                bundle.putString("detailEmail",foodAreas.getFoodContactEmail());
                bundle.putString("detailPosted",foodAreas.getFoodPosted());

                // set Fragmentclass Arguments
                foodDetailsFragment.setArguments(bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // View holder
    public class FoodVH extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView listImg, blankHeart, heart;
        public TextView listTitle, listDesc1, listDesc2, listDesc3;
        public FoodVH(@NonNull View itemView) {
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
