package com.example.cebuapp.controllers.Admin.ManageJobFields;

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
import com.example.cebuapp.model.JobFields;

import java.io.Serializable;
import java.util.ArrayList;

public class ManageJobFieldsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;

    ArrayList<JobFields> list = new ArrayList<>();

    public ManageJobFieldsRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<JobFields> jobFields) {
        //append all data to arraylist
        if (!list.isEmpty()) {
            list.clear();
        }
        list.addAll(jobFields);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_single_value_list_item, parent, false);
        return new ManageJobFieldsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ManageJobFieldsVH vh = (ManageJobFieldsVH) holder;
        JobFields jobFields = list.get(position);

        vh.csv_txt_title.setText(jobFields.getJobFieldTitle());
        vh.csv_txt_menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, vh.csv_txt_menu);
            popupMenu.inflate(R.menu.edit_remove_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_edit:
                        intent = new Intent(context, ManageJobsFieldsActivity.class);
                        intent.putExtra("jobFieldEdit", (Serializable) jobFields);
                        context.startActivity(intent);
                        break;

                    // if menu delete is clicked
                    case R.id.menu_remove:
                        new AlertDialog.Builder(context)
                                .setTitle("CebuApp")
                                .setMessage("Do you really want to remove Job Field title '" + jobFields.getJobFieldTitle() + "' ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        CRUDManageJobFields crudJobFields = new CRUDManageJobFields();
                                        crudJobFields.removeJobFields(jobFields.getKey()).addOnSuccessListener(suc -> {
                                            Toast.makeText(context,
                                                    "Job Field Title deleted successfully!", Toast.LENGTH_LONG).show();
                                            notifyItemRemoved(position);
                                        }).addOnFailureListener(fail -> {
                                            Toast.makeText(context,
                                                    "Job Field Title name deleting failed, " + fail.getMessage(), Toast.LENGTH_LONG).show();
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
