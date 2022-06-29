package com.example.cebuapp.controllers.Admin.ManageJobPosts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.R;
import com.example.cebuapp.model.JobPosts;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ManageJobPostsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;
    private String yearsExp;
    private String approveWord;
    private CRUDManageJobPosts crudJobPosts;

    ArrayList<JobPosts> list = new ArrayList<>();

    public ManageJobPostsRVAdapter(Context ctx) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item_admin, parent, false);
        return new ManageJobPostsVH(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ManageJobPostsVH vh = (ManageJobPostsVH) holder;
        JobPosts jobPosts = list.get(position);
        crudJobPosts = new CRUDManageJobPosts();

        yearsExp = jobPosts.getJobPostYearExp();
        if (yearsExp.equals(0) || yearsExp.equals("0")) {
            yearsExp = "No experience required";
        } else if (yearsExp.equals(1) || yearsExp.equals("1")){
            yearsExp = "At least 1 year experience";
        } else {
            yearsExp = "At least " + yearsExp + " years experience";
        }

        // image viewing
        if (jobPosts.getJobPostImg()!= null) {
            Picasso.get().load(jobPosts.getJobPostImg()).into(vh.adminListImg);
        }
        vh.adminListTitle.setText(jobPosts.getJobPostTitle());
        vh.adminListDesc1.setText("P " + jobPosts.getJobPostSalary());
        vh.adminListDesc2.setText(yearsExp);

        // if published/approved
        if (jobPosts.getApproved().equals(false) && jobPosts.getApproved() == false) {
            // is approved
            approveWord = "Hidden";
            vh.adminListDesc3.setText(approveWord);
            // set menu option
            vh.adminListMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, vh.adminListMenu);
                popupMenu.inflate(R.menu.approve_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_edit:
                            intent = new Intent(context, ManageJobsPostsActivity.class);
                            intent.putExtra("jobFieldEdit", (Serializable) jobPosts);
                            context.startActivity(intent);
                            break;

                        // if menu approve is clicked
                        case R.id.menu_publish:
                            new AlertDialog.Builder(context)
                                .setTitle("CebuApp")
                                .setMessage("Are you sure on publishing Job Post '" + jobPosts.getJobPostTitle() + "' ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("approved", true);
                                        hashMap.put("jobPostPosted", HelperUtilities.getCurrentDate());
                                        crudJobPosts.update(jobPosts.getKey(), hashMap).addOnSuccessListener(suc -> {
                                            Toast.makeText(context,
                                                    "Published Job Post successfully!", Toast.LENGTH_LONG).show();
//                                                notifyItemChanged(position);
                                        }).addOnFailureListener(fail -> {
                                            Toast.makeText(context,
                                                    "Failed publishing, please try again." + fail.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).create().show();
                        break;
                    }
                    return false;
                });
                popupMenu.show();
            });
        } else {
            // is Approved
            approveWord = "Published";
            vh.adminListDesc3.setText(approveWord);
            // set menu option
            vh.adminListMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, vh.adminListMenu);
                popupMenu.inflate(R.menu.edit_remove_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_edit:
                            intent = new Intent(context, ManageJobsPostsActivity.class);
                            intent.putExtra("jobFieldEdit", (Serializable) jobPosts);
                            context.startActivity(intent);
                            break;
                        // hide published post
                        case R.id.menu_hide:
                            new AlertDialog.Builder(context)
                                    .setTitle("CebuApp")
                                    .setMessage("Are you sure on hiding Job Post '" + jobPosts.getJobPostTitle() + "' ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("approved", false);
                                            crudJobPosts.update(jobPosts.getKey(), hashMap).addOnSuccessListener(suc -> {
                                                Toast.makeText(context,
                                                        "Hidden Job Post successfully!", Toast.LENGTH_LONG).show();
//                                                notifyItemChanged(position);
                                            }).addOnFailureListener(fail -> {
                                                Toast.makeText(context,
                                                        "Failed hiding, please try again." + fail.getMessage(), Toast.LENGTH_LONG).show();
                                            });
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).create().show();
                            break;
                        // if menu delete is clicked
                        case R.id.menu_remove:
                            new AlertDialog.Builder(context)
                                    .setTitle("CebuApp")
                                    .setMessage("Do you really want to remove Job Post '" + jobPosts.getJobPostTitle() + "' ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            crudJobPosts.delete(jobPosts.getKey()).addOnSuccessListener(suc -> {
                                                Toast.makeText(context,
                                                        "Deleted successfully!", Toast.LENGTH_LONG).show();
                                                notifyItemRemoved(position);
                                            }).addOnFailureListener(fail -> {
                                                Toast.makeText(context,
                                                        "Failed deleting, please try again." + fail.getMessage(), Toast.LENGTH_LONG).show();
                                            });
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).create().show();
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            });
        }

        vh.adminPostCard.setOnClickListener(v -> {
            ManageJobPostFragment jobPostFragment = new ManageJobPostFragment();
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.framelayout, jobPostFragment);
            fragmentTransaction.commit();

            // send data to fragment
            Bundle bundle = new Bundle();
            bundle.putString("jobDetailApproved", approveWord);
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

            // set Fragmentclass Arguments
            jobPostFragment.setArguments(bundle);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ManageJobPostsVH extends RecyclerView.ViewHolder {

        public CardView adminPostCard;
        public ImageView adminListImg;
        public TextView adminListTitle, adminListDesc1, adminListDesc2, adminListDesc3, adminListMenu;
        public ManageJobPostsVH(@NonNull View itemView) {
            super(itemView);
            adminPostCard = itemView.findViewById(R.id.adminPostCard);
            adminListImg = itemView.findViewById(R.id.adminListImg);
            adminListTitle = itemView.findViewById(R.id.adminListTitle);
            adminListDesc1 = itemView.findViewById(R.id.adminListDesc1);
            adminListDesc2 = itemView.findViewById(R.id.adminListDesc2);
            adminListDesc3 = itemView.findViewById(R.id.adminListDesc3);
            adminListMenu = itemView.findViewById(R.id.adminListMenu);
        }
    }
}
