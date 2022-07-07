package com.example.cebuapp.controllers.Admin.ManageTouristSpots;

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

public class ManageTouristSpotsFragment extends Fragment {

    private View view;
    private ImageButton fragmentClose;
    private ImageView detailImg;
    private TextView detailIsApproved, detailTitle, detailLandmark, detailProvince, detailDescription, detailContact,
            detailEmail, detailDatePosted, detailAuthor;
    private String sApproved, sfoodDetailImg, sfoodDetailTitle, sfoodDetailLandmark, sfoodDetailProvince, sfoodDetailDescription, sfoodDetailContact,
            sfoodDetailEmail, sfoodDetailPosted, sfoodDetailAuthor;
    private Button detailEmailAuthor;
    private Intent intent;

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
        detailAuthor = view.findViewById(R.id.detailAuthor);
        detailEmailAuthor = view.findViewById(R.id.detailEmailAuthor);
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
        sfoodDetailAuthor = getArguments().getString("detailAuthor");
    }

    private void setValues() {
        new ShowImageUrl((ImageView) view.findViewById(R.id.detailImg))
                .execute(sfoodDetailImg);
        detailIsApproved.setText(sApproved);
        detailTitle.setText(sfoodDetailTitle);
        detailLandmark.setText(sfoodDetailLandmark);
        detailProvince.setText(sfoodDetailProvince);
        detailDescription.setText(sfoodDetailDescription);
        detailEmail.setText(sfoodDetailEmail);
        detailContact.setText(sfoodDetailContact);
        detailDatePosted.setText(sfoodDetailPosted);
        if (sfoodDetailAuthor.equals("admin@gmail.com")) {
            // if author is admin, hide email billing
            sfoodDetailAuthor = "CEBU-APP";
            detailEmailAuthor.setVisibility(View.GONE);
        } else {
            // if author is user ang already published, hide email billing
            if (sApproved.equals("Published")) {
                detailEmailAuthor.setVisibility(View.GONE);
            }
        }
        detailAuthor.setText(sfoodDetailAuthor);
    }

    private void setListeners() {
        fragmentClose.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });

        // email spot post author
        detailEmailAuthor.setOnClickListener(v -> {
            if (!sfoodDetailAuthor.equals("admin@gmail.com")) {
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {sfoodDetailAuthor});
                intent.putExtra(Intent.EXTRA_CC, new String[] {"fdc.egonzaga@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "CEBU APP - ADMIN");
                intent.putExtra(Intent.EXTRA_TEXT, "Hi, " + sfoodDetailAuthor + "! \n\n " +
                        "Thanks for posting your Tourist Spot on CEBU APP. We would like to inform you that in order for your Tourist Spot to get published, you will need to pay " +
                        "P500.00 for a month-long feature of your spot, you may pay it via Card or Gcash. In addition, you can definitely extend the feature of your spot " +
                        "for another month for only P450.00.\n\n" +
                        "Please reach us if you have questions or concerns  via phone 09322702597 or email us at fdc.egonzaga@gmail.com.\n\n\n" +
                        "All the best,\nCEBU APP - ADMIN");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}