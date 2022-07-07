package com.example.cebuapp.controllers.User.JobPosts;

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
import com.example.cebuapp.model.JobPosts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class JobPostsOwnedRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;
    private String yearsExp;

    ArrayList<JobPosts> list = new ArrayList<>();

    public JobPostsOwnedRVAdapter(Context ctx) {
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
        myHolder.listDesc3.setText(jobPosts.getApproved() == true ? "Status : Approved" : "Status : Not yet approved");
        myHolder.favorite.setVisibility(View.GONE);
        myHolder.unfavorite.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class JobPostsVH extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView listCompanyImg, favorite, unfavorite;
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
            favorite = itemView.findViewById(R.id.listMenu);
            unfavorite = itemView.findViewById(R.id.listMenu2);
        }
    }
}
