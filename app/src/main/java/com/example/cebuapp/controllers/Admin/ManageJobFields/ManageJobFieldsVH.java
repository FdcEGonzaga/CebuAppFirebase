package com.example.cebuapp.controllers.Admin.ManageJobFields;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;

public class ManageJobFieldsVH extends RecyclerView.ViewHolder {

    public TextView csv_txt_title, csv_txt_menu;
    public ManageJobFieldsVH(@NonNull View itemView) {
        super(itemView);
        csv_txt_title = itemView.findViewById(R.id.csv_txt_title);
        csv_txt_menu = itemView.findViewById(R.id.csv_txt_menu);
    }
}
