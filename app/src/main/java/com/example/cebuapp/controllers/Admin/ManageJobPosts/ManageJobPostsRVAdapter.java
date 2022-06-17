package com.example.cebuapp.controllers.Admin.ManageJobPosts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;
import com.example.cebuapp.model.JobPosts;

import java.io.Serializable;
import java.util.ArrayList;

public class ManageJobPostsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;

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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ManageJobPostsVH vh = (ManageJobPostsVH) holder;
        JobPosts jobPosts = list.get(position);

        String yearsExp = jobPosts.getJobPostYearExp();
        if (yearsExp.equals(0) || yearsExp.equals("0")) {
            yearsExp = "No experience required";
        } else if (yearsExp.equals(1) || yearsExp.equals("1")){
            yearsExp = "At least 1 year experience";
        } else {
            yearsExp = "At least " + yearsExp + " years experience";
        }

        // vh.adminListImg.setText(jobPosts.getJobPostTitle());
        vh.adminListTitle.setText(jobPosts.getJobPostTitle());
        vh.adminListDesc1.setText("P " + jobPosts.getJobPostSalary());
        vh.adminListDesc2.setText(yearsExp);
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

                    // if menu delete is clicked
                    case R.id.menu_remove:
                        new AlertDialog.Builder(context)
                                .setTitle("CebuApp")
                                .setMessage("Do you really want to remove Job Post title '" + jobPosts.getJobPostTitle() + "' ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        CRUDManageJobPosts crudJobPosts = new CRUDManageJobPosts();
                                        crudJobPosts.delete(jobPosts.getKey()).addOnSuccessListener(suc -> {
                                            Toast.makeText(context,
                                                    "Job Post Title deleted successfully!", Toast.LENGTH_LONG).show();
                                            notifyItemRemoved(position);
                                        }).addOnFailureListener(fail -> {
                                            Toast.makeText(context,
                                                    "Job Post Title name deleting failed, " + fail.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public int getItemCount() {
        return list.size();
    }
}
