package com.example.cebuapp.controllers.Admin.ManageTouristSpots;

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
import com.example.cebuapp.model.TouristSpot;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ManageTouristSpotsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private Intent intent;
    private String approveWord;
    private CRUDManageTouristSpots crudTouristSpot;

    ArrayList<TouristSpot> list = new ArrayList<>();

    public ManageTouristSpotsRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<TouristSpot> touristSpot) {
        //append all data to arraylist
        if (!list.isEmpty()) {
            list.clear();
        }
        list.addAll(touristSpot);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item_admin, parent, false);
        return new ManageTouristSpotsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ManageTouristSpotsVH vh = (ManageTouristSpotsVH) holder;
        TouristSpot touristSpot = list.get(position);
        crudTouristSpot = new CRUDManageTouristSpots();

        if (touristSpot.getTouristSpotImg()!= null) {
            Picasso.get().load(touristSpot.getTouristSpotImg()).into(vh.adminListImg);
        }
        vh.adminListTitle.setText(touristSpot.getTouristSpotTitle());
        vh.adminListDesc1.setText(touristSpot.getTouristSpotDescription());
        vh.adminListDesc2.setText(touristSpot.getTouristSpotProvince());
        // if published/approved
        if (touristSpot.getApproved().equals(false) && touristSpot.getApproved() == false) {
            // is approved
            approveWord = "Hidden";
            vh.adminListDesc3.setText(approveWord);
            // set menu option
            vh.adminListMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, vh.adminListMenu);
                popupMenu.inflate(R.menu.edit_approve_remove_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_edit:
                            intent = new Intent(context, ManageTouristSpotsActivity.class);
                            intent.putExtra("editTouristSpot", (Serializable) touristSpot);
                            context.startActivity(intent);
                            break;

                        // if menu approve is clicked
                        case R.id.menu_publish:
                            new AlertDialog.Builder(context)
                                    .setTitle("CebuApp")
                                    .setMessage("Are you sure on publishing Tourist Spot '" + touristSpot.getTouristSpotTitle() + "' ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("approved", true);
                                            hashMap.put("touristSpotPosted", HelperUtilities.getCurrentDate());
                                            crudTouristSpot.update(touristSpot.getKey(), hashMap).addOnSuccessListener(suc -> {
                                                Toast.makeText(context,
                                                        "Published Tourist Spot successfully!", Toast.LENGTH_LONG).show();
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

                        // if menu delete is clicked
                        case R.id.menu_remove:
                            new AlertDialog.Builder(context)
                                    .setTitle("CebuApp")
                                    .setMessage("Do you really want to remove Tourist Spot '" + touristSpot.getTouristSpotTitle() + "' ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            CRUDManageTouristSpots crudTouristSpot = new CRUDManageTouristSpots();
                                            crudTouristSpot.delete(touristSpot.getKey()).addOnSuccessListener(suc -> {
                                                Toast.makeText(context,
                                                        "Tourist Spot was deleted successfully!", Toast.LENGTH_LONG).show();
                                                notifyItemRemoved(position);
                                            }).addOnFailureListener(fail -> {
                                                Toast.makeText(context,
                                                        "Tourist Spot deleting failed, " + fail.getMessage(), Toast.LENGTH_LONG).show();
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
                popupMenu.inflate(R.menu.edit_hide_remove_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_edit:
                            intent = new Intent(context, ManageTouristSpotsActivity.class);
                            intent.putExtra("editTouristSpot", (Serializable) touristSpot);
                            context.startActivity(intent);
                            break;

                        // hide published post
                        case R.id.menu_hide:
                            new AlertDialog.Builder(context)
                                    .setTitle("CebuApp")
                                    .setMessage("Are you sure on hiding Tourist Spot '" + touristSpot.getTouristSpotTitle() + "' ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("approved", false);
                                            crudTouristSpot.update(touristSpot.getKey(), hashMap).addOnSuccessListener(suc -> {
                                                Toast.makeText(context,
                                                        "Hidden Tourist Spot successfully!", Toast.LENGTH_LONG).show();
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
                                    .setMessage("Do you really want to remove Tourist Spot '" + touristSpot.getTouristSpotTitle() + "' ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            CRUDManageTouristSpots crudTouristSpot = new CRUDManageTouristSpots();
                                            crudTouristSpot.delete(touristSpot.getKey()).addOnSuccessListener(suc -> {
                                                Toast.makeText(context,
                                                        "Tourist Spot was deleted successfully!", Toast.LENGTH_LONG).show();
                                                notifyItemRemoved(position);
                                            }).addOnFailureListener(fail -> {
                                                Toast.makeText(context,
                                                        "Tourist Spot deleting failed, " + fail.getMessage(), Toast.LENGTH_LONG).show();
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
            ManageTouristSpotsFragment TouristSpotFragment = new ManageTouristSpotsFragment();
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.framelayout, TouristSpotFragment);
            fragmentTransaction.commit();

            // send data to fragment
            Bundle bundle = new Bundle();
            bundle.putString("isApproved",touristSpot.getApproved().equals(true) ? "Published" : "Hidden");
            bundle.putString("detailImg",touristSpot.getTouristSpotImg());
            bundle.putString("detailTitle",touristSpot.getTouristSpotTitle());
            bundle.putString("detailLandmark",touristSpot.getTouristSpotAddress());
            bundle.putString("detailProvince", touristSpot.getTouristSpotProvince());
            bundle.putString("detailDesc",touristSpot.getTouristSpotDescription());
            bundle.putString("detailContact",touristSpot.getTouristSpotContactNum());
            bundle.putString("detailEmail",touristSpot.getTouristSpotContactEmail());
            bundle.putString("detailPosted",touristSpot.getTouristSpotPosted());
            bundle.putString("detailAuthor",touristSpot.getTouristSpotAuthor());

            // set Fragmentclass Arguments
            TouristSpotFragment.setArguments(bundle);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ManageTouristSpotsVH extends RecyclerView.ViewHolder {

        public CardView adminPostCard;
        public ImageView adminListImg;
        public TextView adminListTitle, adminListDesc1, adminListDesc2, adminListDesc3, adminListMenu;
        public ManageTouristSpotsVH(@NonNull View itemView) {
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
