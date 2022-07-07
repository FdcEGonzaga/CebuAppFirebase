package com.example.cebuapp.controllers.User.TouristSpots;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.R;

public class SpotsDetailsFragment extends Fragment {

    private View view;
    private ImageButton fragmentClose;
    private ImageView detailImg;
    private TextView detailTitle, detailLandmark, detailProvince, detailDescription, detailContact,
            detailEmail, detailDatePosted;
    private String spotsDetailImg, spotsDetailTitle, spotsDetailLandmark, spotsDetailProvince, spotsDetailDescription, spotsDetailContact,
            spotsDetailEmail, spotsDetailPosted;
    private Button callBtn, emailBtn;
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_spots_details, container, false);
        castComponents();
        getValues();
        setValues();
        setListeners();

        return view;
    }

    private void castComponents() {
        fragmentClose = view.findViewById(R.id.fragment_close);
        detailImg = view.findViewById(R.id.detailImg);
        detailTitle = view.findViewById(R.id.detailTitle);
        detailLandmark = view.findViewById(R.id.detailLandmark);
        detailProvince = view.findViewById(R.id.detailProvince);
        detailDescription = view.findViewById(R.id.detailDescription);
        detailContact = view.findViewById(R.id.detailContact);
        detailEmail = view.findViewById(R.id.detailEmail);
        detailDatePosted = view.findViewById(R.id.detailDatePosted);
        callBtn = view.findViewById(R.id.callBtn);
        emailBtn = view.findViewById(R.id.emailBtn);
    }

    private void getValues() {
        spotsDetailImg = getArguments().getString("detailImg");
        spotsDetailTitle = getArguments().getString("detailTitle");
        spotsDetailLandmark = getArguments().getString("detailLandmark");
        spotsDetailProvince = getArguments().getString("detailProvince");
        spotsDetailDescription = getArguments().getString("detailDesc");
        spotsDetailContact = getArguments().getString("detailContact");
        spotsDetailEmail = getArguments().getString("detailEmail");
        spotsDetailPosted = getArguments().getString("detailPosted");
    }

    private void setValues() {
        new ShowImageUrl((ImageView) view.findViewById(R.id.detailImg))
                .execute(spotsDetailImg);
        detailTitle.setText(spotsDetailTitle);
        detailLandmark.setText(spotsDetailLandmark);
        detailProvince.setText(spotsDetailProvince);
        detailDescription.setText(spotsDetailDescription);
        detailContact.setText(spotsDetailContact);;
        detailEmail.setText(spotsDetailEmail);
        detailDatePosted.setText(spotsDetailPosted);
    }

    private void setListeners() {
        fragmentClose.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });

        // call food are btn
        callBtn.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(spotsDetailContact.trim())));
            startActivity(intent);
        });

        // email food are btn
        emailBtn.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {spotsDetailEmail});
            intent.putExtra(Intent.EXTRA_CC, new String[] {"fdc.egonzaga@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "CEBU APP - " + spotsDetailTitle.trim());
            intent.putExtra(Intent.EXTRA_TEXT, "Hi, " + spotsDetailTitle + "! \n\n " +
                    "I found your wonderful Spot Area on CEBU APP, I would like to inquire about the following:\n" +
                    "1. ...");
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        });
    }
}