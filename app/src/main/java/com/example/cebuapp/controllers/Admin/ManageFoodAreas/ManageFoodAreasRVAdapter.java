package com.example.cebuapp.controllers.Admin.ManageFoodAreas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.R;
import com.example.cebuapp.model.FoodArea;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ManageFoodAreasRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private Intent intent;
    private String approveWord;
    private CRUDManageFoodAreas crudFoodAreas;

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
        crudFoodAreas = new CRUDManageFoodAreas();

        if (foodAreas.getFoodImg()!= null) {
            Picasso.get().load(foodAreas.getFoodImg()).into(vh.adminListImg);
        }
        vh.adminListTitle.setText(foodAreas.getFoodTitle());
        vh.adminListDesc1.setText(foodAreas.getFoodAddress());
        vh.adminListDesc2.setText(foodAreas.getFoodProvince());

        // if published/approved
        if (foodAreas.getApproved().equals(false) && foodAreas.getApproved() == false) {
            // is Approved
            approveWord = "Hidden";
            vh.adminListDesc3.setText(approveWord);
            vh.adminListMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, vh.adminListMenu);
                popupMenu.inflate(R.menu.approve_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_edit:
                            intent = new Intent(context, ManageFoodAreasActivity.class);
                            intent.putExtra("foodAreaEdit", (Serializable) foodAreas);
                            context.startActivity(intent);
                            break;

                        // if menu approve is clicked
                        case R.id.menu_publish:
                            new AlertDialog.Builder(context)
                                    .setTitle("CebuApp")
                                    .setMessage("Are you sure on publishing Food Area '" + foodAreas.getFoodTitle() + "' ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("approved", true);
                                            hashMap.put("foodPosted", HelperUtilities.getCurrentDate());
                                            crudFoodAreas.update(foodAreas.getKey(), hashMap).addOnSuccessListener(suc -> {
                                                Toast.makeText(context,
                                                        "Published Food Area successfully!", Toast.LENGTH_LONG).show();
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
                        // hide published post
                        case R.id.menu_hide:
                            new AlertDialog.Builder(context)
                                    .setTitle("CebuApp")
                                    .setMessage("Are you sure on hiding Food Area '" + foodAreas.getFoodTitle() + "' ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("approved", false);
                                            crudFoodAreas.update(foodAreas.getKey(), hashMap).addOnSuccessListener(suc -> {
                                                Toast.makeText(context,
                                                        "Hidden Food Area successfully!", Toast.LENGTH_LONG).show();
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
                                    .setMessage("Do you really want to remove Food Area '" + foodAreas.getFoodTitle() + "' ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            crudFoodAreas.delete(foodAreas.getKey()).addOnSuccessListener(suc -> {
                                                Toast.makeText(context,
                                                        "Deleted successfully!", Toast.LENGTH_LONG).show();
                                                notifyItemRemoved(position);
                                            }).addOnFailureListener(fail -> {
                                                Toast.makeText(context,
                                                        "Deleting failed, " + fail.getMessage(), Toast.LENGTH_LONG).show();
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
            ManageFoodAreasFragment FoodAreaFragment = new ManageFoodAreasFragment();
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.framelayout, FoodAreaFragment);
            fragmentTransaction.commit();

            // send data to fragment
            Bundle bundle = new Bundle();
            bundle.putString("isApproved", approveWord);
            bundle.putString("detailImg",foodAreas.getFoodImg());
            bundle.putString("detailTitle",foodAreas.getFoodTitle());
            bundle.putString("detailLandmark",foodAreas.getFoodAddress());
            bundle.putString("detailProvince", foodAreas.getFoodProvince());
            bundle.putString("detailDesc",foodAreas.getFoodDescription());
            bundle.putString("detailContact",foodAreas.getFoodContactNum());
            bundle.putString("detailEmail",foodAreas.getFoodContactEmail());
            bundle.putString("detailPosted",foodAreas.getFoodPosted());

            // set Fragmentclass Arguments
            FoodAreaFragment.setArguments(bundle);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ManageFoodAreasVH extends RecyclerView.ViewHolder {

        public CardView adminPostCard;
        public ImageView adminListImg;
        public TextView adminListTitle, adminListDesc1, adminListDesc2, adminListDesc3, adminListMenu;
        public ManageFoodAreasVH(@NonNull View itemView) {
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
