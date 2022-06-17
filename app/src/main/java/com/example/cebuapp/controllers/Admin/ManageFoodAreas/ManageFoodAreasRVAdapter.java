package com.example.cebuapp.controllers.Admin.ManageFoodAreas;

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
import com.example.cebuapp.model.FoodArea;

import java.io.Serializable;
import java.util.ArrayList;

public class ManageFoodAreasRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private Intent intent;

    ArrayList<FoodArea> list = new ArrayList<>();

    public ManageFoodAreasRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<FoodArea> foodAreas) {
        //append all data to arraylist
        if (!list.isEmpty()) {
            list.clear();
        }
        list.addAll(foodAreas);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item_admin, parent, false);
        return new ManageFoodAreasVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ManageFoodAreasVH vh = (ManageFoodAreasVH) holder;
        FoodArea foodAreas = list.get(position);

        // vh.adminListImg.setText(foodAreas.getJobPostTitle());
        vh.adminListTitle.setText(foodAreas.getFoodTitle());
        vh.adminListDesc1.setText(foodAreas.getFoodDescription());
        vh.adminListDesc2.setText(foodAreas.getFoodProvince());
        vh.adminListMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, vh.adminListMenu);
            popupMenu.inflate(R.menu.edit_remove_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_edit:
                        intent = new Intent(context, ManageFoodAreasActivity.class);
                        intent.putExtra("foodAreaEdit", (Serializable) foodAreas);
                        context.startActivity(intent);
                        break;

                    // if menu delete is clicked
                    case R.id.menu_remove:
                        new AlertDialog.Builder(context)
                                .setTitle("CebuApp")
                                .setMessage("Do you really want to remove Food Area title '" + foodAreas.getFoodTitle() + "' ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        CRUDManageFoodAreas crudFoodAreas = new CRUDManageFoodAreas();
                                        crudFoodAreas.delete(foodAreas.getKey()).addOnSuccessListener(suc -> {
                                            Toast.makeText(context,
                                                    "Food Area was deleted successfully!", Toast.LENGTH_LONG).show();
                                            notifyItemRemoved(position);
                                        }).addOnFailureListener(fail -> {
                                            Toast.makeText(context,
                                                    "Food Area deleting failed, " + fail.getMessage(), Toast.LENGTH_LONG).show();
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
