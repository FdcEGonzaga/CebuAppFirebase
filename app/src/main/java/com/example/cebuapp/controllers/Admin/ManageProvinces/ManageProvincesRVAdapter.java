package com.example.cebuapp.controllers.Admin.ManageProvinces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;
import com.example.cebuapp.model.Province;

import java.io.Serializable;
import java.util.ArrayList;

public class ManageProvincesRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Intent intent;

    ArrayList<Province> list = new ArrayList<>();

    public ManageProvincesRVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<Province> provinces) {
        //append all data to arraylist
        if (!list.isEmpty()) {
            list.clear();
        }
        list.addAll(provinces);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_single_value_list_item, parent, false);
        return new ManageProvincesVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ManageProvincesVH vh = (ManageProvincesVH) holder;
        Province province = list.get(position);

        vh.csv_txt_title.setText(province.getProvinceName());
        // option menu
        vh.csv_txt_menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, vh.csv_txt_menu);
            // inflate custom province option menu
            popupMenu.inflate(R.menu.edit_remove_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    // if menu edit is clicked
                    case R.id.menu_edit:
                        intent = new Intent(context, ManageProvincesActivity.class);
                        intent.putExtra("provinceEdit", (Serializable) province);
                        context.startActivity(intent);
                        break;

                    // if menu delete is clicked
                    case R.id.menu_remove:
                        new AlertDialog.Builder(context)
                            .setTitle("CebuApp")
                            .setMessage("Do you really want to remove province name '" + province.getProvinceName() + "' ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    CRUDManageProvinces crudProv = new CRUDManageProvinces();
                                    crudProv.removeProvinceName(province.getKey()).addOnSuccessListener(suc -> {
                                        Toast.makeText(context,
                                                "Province name deleted successfully!", Toast.LENGTH_LONG).show();
                                        notifyItemRemoved(position);
                                    }).addOnFailureListener(fail -> {
                                        Toast.makeText(context,
                                                "Province name deleting failed, " + fail.getMessage(), Toast.LENGTH_LONG).show();
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

    public class ManageProvincesVH extends RecyclerView.ViewHolder {

        public TextView csv_txt_title, csv_txt_menu;
        public ManageProvincesVH(@NonNull View itemView) {
            super(itemView);
            csv_txt_title = itemView.findViewById(R.id.csv_txt_title);
            csv_txt_menu = itemView.findViewById(R.id.csv_txt_menu);
        }
    }
}
