package com.example.cebuapp.controllers.Admin.ManageFoodAreas;

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

public class ManageFoodAreasFragment extends Fragment {

    private View view;
    private ImageButton fragmentClose;
    private ImageView detailImg;
    private TextView detailIsApproved, detailTitle, detailLandmark, detailProvince, detailDescription, detailContact,
            detailEmail, detailDatePosted, detailAuthor;
    private String isApproved, foodDetailImg, foodDetailTitle, foodDetailLandmark, foodDetailProvince, foodDetailDescription, foodDetailContact,
            foodDetailEmail, foodDetailPosted, foodDetailAuthor;
    private Intent intent;
    private Button detailEmailAuthor;

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
        detailAuthor = view.findViewById(R.id.detailAuthor);
        detailEmailAuthor = view.findViewById(R.id.detailEmailAuthor);
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
        foodDetailAuthor = getArguments().getString("detailAuthor");
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
        detailDatePosted.setText(foodDetailPosted);
        detailEmail.setText(foodDetailEmail);

        if (foodDetailAuthor.equals("admin@gmail.com") ) {
            // if author is admin, hide email billing
            foodDetailAuthor = "CEBU-APP";
            detailEmailAuthor.setVisibility(View.GONE);
        } else {
            // if author is user ang already published, hide email billing
            if (isApproved.equals("Published")) {
                detailEmailAuthor.setVisibility(View.GONE);
            }
        }
        detailAuthor.setText(foodDetailAuthor);
    }

    private void setListeners() {
        fragmentClose.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });

        // email food area post author
        detailEmailAuthor.setOnClickListener(v -> {
            if (!foodDetailAuthor.equals("admin@gmail.com")) {
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {foodDetailAuthor});
                intent.putExtra(Intent.EXTRA_CC, new String[] {"fdc.egonzaga@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "CEBU APP - ADMIN");
                intent.putExtra(Intent.EXTRA_TEXT, "Hi, " + foodDetailAuthor + "! \n\n " +
                        "Thanks for posting your Food Area on CEBU APP. We would like to inform you that in order for your Food Area to get published, you will need to pay " +
                        "P400.00 for a month-long feature of your Food Area, you may pay it via Card or Gcash. In addition, you can definitely extend the featuring of your " +
                        "Food Area for another month for only P350.00.\n\n" +
                        "Please reach us if you have questions or concerns  via phone 09322702597 or email us at fdc.egonzaga@gmail.com.\n\n\n" +
                        "All the best,\nCEBU APP - ADMIN");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}