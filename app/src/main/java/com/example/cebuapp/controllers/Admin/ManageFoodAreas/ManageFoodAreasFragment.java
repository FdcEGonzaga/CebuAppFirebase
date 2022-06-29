package com.example.cebuapp.controllers.Admin.ManageFoodAreas;

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

public class ManageFoodAreasFragment extends Fragment {

    private View view;
    private ImageButton fragmentClose;
    private ImageView detailImg;
    private TextView detailIsApproved, detailTitle, detailLandmark, detailProvince, detailDescription, detailContact,
            detailEmail, detailDatePosted;
    private String isApproved, foodDetailImg, foodDetailTitle, foodDetailLandmark, foodDetailProvince, foodDetailDescription, foodDetailContact,
            foodDetailEmail, foodDetailPosted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_food_areas, container, false);
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
        isApproved = getArguments().getString("isApproved");
        foodDetailImg = getArguments().getString("detailImg");
        foodDetailTitle = getArguments().getString("detailTitle");
        foodDetailLandmark = getArguments().getString("detailLandmark");
        foodDetailProvince = getArguments().getString("detailProvince");
        foodDetailDescription = getArguments().getString("detailDesc");
        foodDetailContact = getArguments().getString("detailContact");
        foodDetailEmail = getArguments().getString("detailEmail");
        foodDetailPosted = getArguments().getString("detailPosted");
    }

    private void setValues() {
        new ShowImageUrl((ImageView) view.findViewById(R.id.detailImg))
                .execute(foodDetailImg);
        detailIsApproved.setText(isApproved);
        detailTitle.setText(foodDetailTitle);
        detailLandmark.setText(foodDetailLandmark);
        detailProvince.setText(foodDetailProvince);
        detailDescription.setText(foodDetailDescription);
        detailContact.setText(foodDetailContact);;
        detailEmail.setText(foodDetailEmail);
        detailDatePosted.setText(foodDetailPosted);
    }

    private void setListeners() {
        fragmentClose.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
    }
}