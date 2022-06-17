package com.example.cebuapp.controllers.User.TouristSpots;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PackageManagerCompat;

import com.example.cebuapp.R;
import com.example.cebuapp.model.TouristSpot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SpotsDetailsActivity extends AppCompatActivity {
    private Intent intent;
    private ImageButton backBtn;
    private ImageView spotsDetailImg;
    private TextView spotsDetailTitle, spotsDetailLandmark, spotsDetailProvince, spotsDetailDescription, spotsDetailContact,
            spotsDetailEmail, spotsDetailPosted;
    private Button callBtn, emailBtn;
    private TouristSpot spotsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spots_details);

        castComponents();
        getCurrentDate();
        intent = getIntent();

        if (isConnectedToInternet()) {
            // check intent extra for editing food field
            spotsData = (TouristSpot) getIntent().getSerializableExtra("FIREBASE_DATA");

            if (spotsData != null) {
                spotsDetailTitle.setText(spotsData.getTouristSpotTitle());
                spotsDetailLandmark.setText(spotsData.getTouristSpotAddress());
                spotsDetailProvince.setText(spotsData.getTouristSpotProvince() + ", CEBU");
                spotsDetailDescription.setText(spotsData.getTouristSpotDescription());
                spotsDetailContact.setText(spotsData.getTouristSpotContactNum());
                spotsDetailEmail.setText(spotsData.getTouristSpotContactEmail());
                spotsDetailPosted.setText(spotsData.getTouristSpotPostedDate());
            }
            // back btn
            backBtn.setOnClickListener(v -> {
                intent = new Intent(SpotsDetailsActivity.this, SpotsActivity.class);
                startActivity(intent);
            });

            // call food are btn
            callBtn.setOnClickListener(v -> {
                //intent = new Intent(FoodDetailsActivity.this, DialActivity.class);
                //intent.putExtra("CONTACTNUMBER", spotsData.getFoodContactNum());
                //startActivity(intent);
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse(Uri.encode(spotsData.getTouristSpotContactNum())));
                startActivity(intent);
            });

            // email food are btn
            emailBtn.setOnClickListener(v -> {
                Toast.makeText(this, "Email", Toast.LENGTH_SHORT).show();
            });

        } else {
            Toast.makeText(this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void castComponents() {
        spotsDetailImg = findViewById(R.id.detailImg);
        spotsDetailTitle = findViewById(R.id.detailTitle);
        spotsDetailLandmark = findViewById(R.id.detailLandmark);
        spotsDetailProvince = findViewById(R.id.detailProvince);
        spotsDetailDescription = findViewById(R.id.detailDescription);
        spotsDetailContact = findViewById(R.id.detailContact);
        spotsDetailEmail = findViewById(R.id.detailEmail);
        spotsDetailPosted = findViewById(R.id.detailDatePosted);
        backBtn = findViewById(R.id.back_btn);
        callBtn = findViewById(R.id.callBtn);
        emailBtn = findViewById(R.id.emailBtn);
    }

    private void getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy H:m", Locale.getDefault());
        String formattedDate = df.format(c);
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }
}