package com.example.cebuapp.controllers.Admin.ManageTouristSpots;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.R;

public class ManageTouristSpotsFragment extends Fragment {

    private View view;
    private ImageButton fragmentClose;
    private ImageView detailImg;
    private TextView detailIsApproved, detailTitle, detailLandmark, detailProvince, detailDescription, detailContact,
            detailEmail, detailDatePosted;
    private String sApproved, sfoodDetailImg, sfoodDetailTitle, sfoodDetailLandmark, sfoodDetailProvince, sfoodDetailDescription, sfoodDetailContact,
            sfoodDetailEmail, sfoodDetailPosted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_tourist_spots, container, false);
        castComponents();
        getValues();
        setValues();
        setListeners();

        return view;
    }

    private void castComponents() {
        fragmentClose = view.findViewById(R.id.fragment_close);
        detailIsApproved = view.findViewById(R.id.isApproved);
        detailImg = view.findViewById(R.id.detailImg);
        detailTitle = view.findViewById(R.id.detailTitle);
        detailLandmark = view.findViewById(R.id.detailLandmark);
        detailProvince = view.findViewById(R.id.detailProvince);
        detailDescription = view.findViewById(R.id.detailDescription);
        detailContact = view.findViewById(R.id.detailContact);
        detailEmail = view.findViewById(R.id.detailEmail);
        detailDatePosted = view.findViewById(R.id.detailDatePosted);
    }

    private void getValues() {
        sApproved = getArguments().getString("isApproved");
        sfoodDetailImg = getArguments().getString("detailImg");
        sfoodDetailTitle = getArguments().getString("detailTitle");
        sfoodDetailLandmark = getArguments().getString("detailLandmark");
        sfoodDetailProvince = getArguments().getString("detailProvince");
        sfoodDetailDescription = getArguments().getString("detailDesc");
        sfoodDetailContact = getArguments().getString("detailContact");
        sfoodDetailEmail = getArguments().getString("detailEmail");
        sfoodDetailPosted = getArguments().getString("detailPosted");
    }

    private void setValues() {
        new ShowImageUrl((ImageView) view.findViewById(R.id.detailImg))
                .execute(sfoodDetailImg);
        detailIsApproved.setText(sApproved);
        detailTitle.setText(sfoodDetailTitle);
        detailLandmark.setText(sfoodDetailLandmark);
        detailProvince.setText(sfoodDetailProvince);
        detailDescription.setText(sfoodDetailDescription);
        detailContact.setText(sfoodDetailContact);;
        detailEmail.setText(sfoodDetailEmail);
        detailDatePosted.setText(sfoodDetailPosted);
    }

    private void setListeners() {
        fragmentClose.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
    }
}