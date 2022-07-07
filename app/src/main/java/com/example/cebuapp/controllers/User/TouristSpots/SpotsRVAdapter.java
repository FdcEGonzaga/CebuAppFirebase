package com.example.cebuapp.controllers.User.TouristSpots;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.cebuapp.controllers.Admin.ManageJobPosts.CRUDManageJobPosts;
import com.example.cebuapp.controllers.User.ManageFavorites.CRUDManageFavorites;
import com.example.cebuapp.model.Favorites;
import com.example.cebuapp.model.TouristSpot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SpotsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    ArrayList<TouristSpot> list = new ArrayList<>();
    ArrayList<String> listFav = new ArrayList<String>();

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
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item_spots, parent, false);
        return new SpotsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SpotsVH myHolder = (SpotsVH) holder;
        TouristSpot tourisSpots = list.get(position);

        if (tourisSpots.getTouristSpotImg()!= null) {
            Picasso.get().load(tourisSpots.getTouristSpotImg()).into(myHolder.listImg);
        }
        myHolder.listTitle.setText(tourisSpots.getTouristSpotTitle());
        myHolder.listDesc1.setText(tourisSpots.getTouristSpotAddress());
        myHolder.listDesc2.setText(tourisSpots.getTouristSpotProvince());
        myHolder.listDesc3.setText(tourisSpots.getTouristSpotPosted());

        // for favoriting
        handler = new Handler();
        crudJobPosts = new CRUDManageJobPosts();
        crudFavorites = new CRUDManageFavorites();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        String customPath = "userID_"+currentUser.getUid()+"_faves";
        databaseReference = db.getReference(customPath.trim());
        postCategory = "tourist_spots";

        // check if added to blankHearts already
        crudFavorites.getAllFavorites(customPath, postCategory).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFav.clear();
                for (DataSnapshot uniqueKey: snapshot.getChildren()) {
                    listFav.add(uniqueKey.getKey());
                }

                if (listFav.contains(tourisSpots.getKey())) {
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
            Favorites favData = new Favorites(postCategory, tourisSpots.getTouristSpotImg(), tourisSpots.getTouristSpotTitle(), tourisSpots.getTouristSpotAddress(),
                    tourisSpots.getTouristSpotProvince(), tourisSpots.getTouristSpotPosted());
            databaseReference.child(tourisSpots.getKey()).setValue(favData)
                    .addOnSuccessListener(suc -> {
                        HelperUtilities.showOkAlert(context,
                                "Added to your Favorite Tourist Spot successfully!");
                        myHolder.blankHeart.setVisibility(View.GONE);
                        myHolder.heart.setVisibility(View.VISIBLE);

                    }).addOnFailureListener(fail -> {
                        HelperUtilities.showOkAlert(context,
                                "Failed adding to your Favorite Tourist Spot, please try again.");
                    });
        });

        // remove to favorites
        myHolder.heart.setOnClickListener(v-> {
            new AlertDialog.Builder(context)
                    .setTitle("CebuApp")
                    .setMessage("Do you really want to remove Tourist Spot title '" + tourisSpots.getTouristSpotTitle() + "' from your Favorites?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            crudFavorites.removeToFavorite(customPath, tourisSpots.getKey()).addOnSuccessListener(suc -> {
                                Toast.makeText(context,
                                        "Removed Tourist Spot from your Favorites successfully!", Toast.LENGTH_LONG).show();
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
                SpotsDetailsFragment spotsDetailsFragment = new SpotsDetailsFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, spotsDetailsFragment);
                fragmentTransaction.commit();

                // send data to fragment
                Bundle bundle = new Bundle();
                bundle.putString("detailImg",tourisSpots.getTouristSpotImg());
                bundle.putString("detailTitle",tourisSpots.getTouristSpotTitle());
                bundle.putString("detailLandmark",tourisSpots.getTouristSpotAddress());
                bundle.putString("detailProvince", tourisSpots.getTouristSpotProvince() + ", CEBU");
                bundle.putString("detailDesc",tourisSpots.getTouristSpotDescription());
                bundle.putString("detailContact",tourisSpots.getTouristSpotContactNum());
                bundle.putString("detailEmail",tourisSpots.getTouristSpotContactEmail());
                bundle.putString("detailPosted",tourisSpots.getTouristSpotPosted());

                // set Fragmentclass Arguments
                spotsDetailsFragment.setArguments(bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // View holder
    public class SpotsVH extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView listImg, blankHeart, heart;
        public TextView listTitle, listDesc1, listDesc2, listDesc3;
        public SpotsVH(@NonNull View itemView) {
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
