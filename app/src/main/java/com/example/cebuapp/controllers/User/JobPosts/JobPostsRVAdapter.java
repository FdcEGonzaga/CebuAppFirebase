package com.example.cebuapp.controllers.User.JobPosts;

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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.User.ManageFavorites.CRUDManageFavorites;
import com.example.cebuapp.model.FavoriteJobs;
import com.example.cebuapp.model.JobPosts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class JobPostsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String yearsExp;

    // favoriting
    private Handler handler;
    private CRUDManageFavorites crudFavorites;
    private FirebaseUser currentUser;
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    private String postCategory;
    ArrayList<String> listFav = new ArrayList<String>();

    ArrayList<JobPosts> list = new ArrayList<>();

    public JobPostsRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<JobPosts> jobPosts) {
        //append all data to arraylist
        if (!list.isEmpty()) {
            list.clear();
        }
        list.addAll(jobPosts);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item_jobs, parent, false);
        return new JobPostsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        JobPostsVH myHolder = (JobPostsVH) holder;
        JobPosts jobPosts = list.get(position);

        yearsExp = jobPosts.getJobPostYearExp();
        if (yearsExp.equals(0) || yearsExp.equals("0")) {
            yearsExp = "No experience required";
        } else if (yearsExp.equals(1) || yearsExp.equals("1")){
            yearsExp = "At least 1 year experience";
        } else {
            yearsExp = "At least " + yearsExp + " years experience";
        }

        if (jobPosts.getJobPostImg()!= null) {
            Picasso.get().load(jobPosts.getJobPostImg()).into(myHolder.listCompanyImg);
        }
        myHolder.listCompanyName.setText(jobPosts.getJobPostCompany());
        myHolder.listTitle.setText(jobPosts.getJobPostTitle());
        myHolder.listDesc1.setText(yearsExp);
        myHolder.listDesc2.setText("P " + jobPosts.getJobPostSalary() + "+ starting salary");
        myHolder.listDesc3.setText(jobPosts.getJobPostPosted());

        // card heart menu listener
        handler = new Handler();
        crudFavorites = new CRUDManageFavorites();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        String customPath = "userID_"+currentUser.getUid()+"_faves";
        databaseReference = db.getReference(customPath.trim());
        postCategory = "job_posts";

        // check if added to blankHearts already
        crudFavorites.getAllFavorites(customPath, postCategory).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFav.clear();
                for (DataSnapshot uniqueKey: snapshot.getChildren()) {
                    listFav.add(uniqueKey.getKey());
                }

                if (listFav.contains(jobPosts.getKey())) {
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
            FavoriteJobs favData = new FavoriteJobs(postCategory, jobPosts.getJobPostImg(), jobPosts.getJobPostCompany(), jobPosts.getJobPostTitle(),
                    jobPosts.getJobPostYearExp(), jobPosts.getJobPostSalary(), jobPosts.getJobPostPosted());
            databaseReference.child(jobPosts.getKey()).setValue(favData)
                    .addOnSuccessListener(suc -> {
                        HelperUtilities.showOkAlert(context,
                                "Added to your Favorite Job Post successfully!");
                        myHolder.blankHeart.setVisibility(View.GONE);
                        myHolder.heart.setVisibility(View.VISIBLE);

                    }).addOnFailureListener(fail -> {
                        HelperUtilities.showOkAlert(context,
                                "Failed adding to your Favorite Job Post, please try again.");
                    });
        });

        // remove to favorites
        myHolder.heart.setOnClickListener(v-> {
            new AlertDialog.Builder(context)
                    .setTitle("CebuApp")
                    .setMessage("Do you really want to remove Job Post title '" + jobPosts.getJobPostTitle() + "' from your Favorites?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            crudFavorites.removeToFavorite(customPath, jobPosts.getKey()).addOnSuccessListener(suc -> {
                                Toast.makeText(context,
                                        "Removed Job Post from your Favorites successfully!", Toast.LENGTH_LONG).show();
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
                JobDetailsFragment jobDetailsFragment = new JobDetailsFragment();
                JobaPostsActivity activity = (JobaPostsActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, jobDetailsFragment);
                fragmentTransaction.commit();

                // send data to fragment
                Bundle bundle = new Bundle();
                bundle.putString("jobDetailImg",jobPosts.getJobPostImg());
                bundle.putString("jobDetailCompany",jobPosts.getJobPostCompany());
                bundle.putString("jobDetailTitle",jobPosts.getJobPostTitle());
                bundle.putString("jobDetailYears",yearsExp);
                bundle.putString("jobDetailSalary","P" + jobPosts.getJobPostSalary() + " salary offer");
                bundle.putString("jobDetailDesc",jobPosts.getJobPostDescription());
                bundle.putString("jobDetailDesc2",jobPosts.getJobPostCompanyDetails());
                bundle.putString("jobDetailField",jobPosts.getJobPostProvince());
                bundle.putString("jobDetailDate",jobPosts.getJobPostPosted());
                bundle.putString("jobUrlString",jobPosts.getJobPostLink());
                bundle.putString("jobUrlAuthor",jobPosts.getJobAuthor());

                // set Fragmentclass Arguments
                jobDetailsFragment.setArguments(bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class JobPostsVH extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView listCompanyImg, blankHeart, heart;
        public TextView listTitle, listDesc1, listDesc2, listDesc3, listCompanyName;
        public JobPostsVH(@NonNull View itemView) {
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
