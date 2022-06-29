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

import java.io.Serializable;
import java.util.ArrayList;

public class JobPostsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;

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

        String yearsExp = jobPosts.getJobPostYearExp();
        if (yearsExp.equals(0) || yearsExp.equals("0")) {
            yearsExp = "No experience required";
        } else if (yearsExp.equals(1) || yearsExp.equals("1")){
            yearsExp = "At least 1 year experience";
        } else {
            yearsExp = "At least " + yearsExp + " years experience";
        }

        myHolder.listTitle.setText(jobPosts.getJobPostTitle());
        myHolder.listDesc1.setText(yearsExp);
        myHolder.listDesc2.setText("P " + jobPosts.getJobPostSalary() + "+ starting salary");
        // card view click listener
        myHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, JobDetailsActivity.class);
                intent.putExtra("FIREBASE_DATA", (Serializable) jobPosts);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class JobPostsVH extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView listTitle, listDesc1, listDesc2;
        public JobPostsVH(@NonNull View itemView) {
            super(itemView);
            listTitle = itemView.findViewById(R.id.listTitle);
            listDesc1 = itemView.findViewById(R.id.listDesc1);
            listDesc2 = itemView.findViewById(R.id.listDesc2);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
