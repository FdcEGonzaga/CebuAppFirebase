package com.example.cebuapp.controllers.Admin.ManageTouristSpots;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cebuapp.R;

public class ManageTouristSpotsVH extends RecyclerView.ViewHolder {

    public ImageView adminListImg;
    public TextView adminListTitle, adminListDesc1, adminListDesc2, adminListMenu;
    public ManageTouristSpotsVH(@NonNull View itemView) {
        super(itemView);
        adminListImg = itemView.findViewById(R.id.adminListImg);
        adminListTitle = itemView.findViewById(R.id.adminListTitle);
        adminListDesc1 = itemView.findViewById(R.id.adminListDesc1);
        adminListDesc2 = itemView.findViewById(R.id.adminListDesc2);
        adminListMenu = itemView.findViewById(R.id.adminListMenu);
    }
}
