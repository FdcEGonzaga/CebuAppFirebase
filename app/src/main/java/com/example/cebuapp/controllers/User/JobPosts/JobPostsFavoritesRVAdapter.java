package com.example.cebuapp.controllers.User.JobPosts;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.cebuapp.controllers.User.FoodAreas.FoodRVAdapter;
import com.example.cebuapp.model.FavoriteJobs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class JobPostsFavoritesRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;
    private String yearsExp;

    // favoriting
    private CRUDManageFavorites crudFavorites;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    private String postCategory;
    private FoodRVAdapter foodRVAdapter;

    private Dialog mDialog;

    ArrayList<FavoriteJobs> list = new ArrayList<>();

    public JobPostsFavoritesRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<FavoriteJobs> favoriteData) {
        //append all data to arraylist
        if (!list.isEmpty()) {
            list.clear();
        }
        list.addAll(favoriteData);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item_jobs, parent, false);
        return new JobPostsFavoritesRVAdapter.FavoritesVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FavoritesVH myHolder = (FavoritesVH) holder;
        FavoriteJobs favoriteData = list.get(position);

        yearsExp = favoriteData.getPostYearExp();
        if (yearsExp.equals(0) || yearsExp.equals("0")) {
            yearsExp = "No experience required";
        } else if (yearsExp.equals(1) || yearsExp.equals("1")){
            yearsExp = "At least 1 year experience";
        } else {
            yearsExp = "At least " + yearsExp + " years experience";
        }

        if (favoriteData.getPostCompanyImg()!= null) {
            Picasso.get().load(favoriteData.getPostCompanyImg()).into(myHolder.listCompanyImg);
        }
        myHolder.listCompanyName.setText(favoriteData.getPostCompanyName());
        myHolder.listTitle.setText(favoriteData.getPostTitle());
        myHolder.listDesc1.setText(yearsExp);
        myHolder.listDesc2.setText("P " + favoriteData.getPostSalary() + "+ starting salary");
        myHolder.listDesc3.setText(favoriteData.getPostDate());
        myHolder.blankHeart.setVisibility(View.GONE);
        myHolder.heart.setVisibility(View.VISIBLE);

        // card heart menu listener
        crudFavorites = new CRUDManageFavorites();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        String customPath = "userID_"+currentUser.getUid()+"_faves";
        databaseReference = db.getReference(customPath.trim());
        postCategory = "job_posts";
        foodRVAdapter = new FoodRVAdapter(context);

        // remove to favorites
        myHolder.heart.setOnClickListener(v-> {
            new AlertDialog.Builder(context)
                    .setTitle("CebuApp")
                    .setMessage("Do you really want to remove Job Post title '" + favoriteData.getPostTitle() + "' from your Favorites?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            crudFavorites.removeToFavorite(customPath, favoriteData.getKey()).addOnSuccessListener(suc -> {
                                Toast.makeText(context,
                                        "Removed Job Post from your Favorites successfully!", Toast.LENGTH_LONG).show();
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
        public ImageView listCompanyImg, blankHeart, heart;
        public TextView listTitle, listDesc1, listDesc2, listDesc3, listCompanyName;
        public FavoritesVH(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            listCompanyImg = itemView.findViewById(R.id.listCompanyImg);
            listCompanyName = itemView.findViewById(R.id.listCompanyName);
            listTitle = itemView.findViewById(R.id.listTitle);
            listDesc1 = itemView.findViewById(R.id.listDesc1);
            listDesc2 = itemView.findViewById(R.id.listDesc2);
            listDesc3 = itemView.findViewById(R.id.listDesc3);
            blankHeart = itemView.findViewById(R.id.listMenu);
            heart = itemView.findViewById(R.id.listMenu2);
        }
    }
}
