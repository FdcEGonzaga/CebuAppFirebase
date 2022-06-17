package com.example.cebuapp.controllers.User.FoodAreas;

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
import com.example.cebuapp.model.FoodArea;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FoodDetailsActivity extends AppCompatActivity {
    private Intent intent;
    private ImageButton backBtn;
    private ImageView foodDetailImg;
    private TextView foodDetailTitle, foodDetailLandmark, foodDetailProvince, foodDetailDescription, foodDetailContact,
            foodDetailEmail, foodDetailPosted;
    private Button callBtn, emailBtn;
    private FoodArea foodData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        castComponents();
        getCurrentDate();
        intent = getIntent();

        if (isConnectedToInternet()) {
            // check intent extra for editing food field
            foodData = (FoodArea) getIntent().getSerializableExtra("FIREBASE_DATA");

            if (foodData != null) {
                foodDetailTitle.setText(foodData.getFoodTitle());
                foodDetailLandmark.setText(foodData.getFoodAddress());
                foodDetailProvince.setText(foodData.getFoodProvince() + ", CEBU");
                foodDetailDescription.setText(foodData.getFoodDescription());
                foodDetailContact.setText(foodData.getFoodContactNum());
                foodDetailEmail.setText(foodData.getFoodContactEmail());
                foodDetailPosted.setText(foodData.getFoodPosted());
            }
            // back btn
            backBtn.setOnClickListener(v -> {
                intent = new Intent(FoodDetailsActivity.this, FoodActivity.class);
                startActivity(intent);
            });

            // call food are btn
            callBtn.setOnClickListener(v -> {
                //intent = new Intent(FoodDetailsActivity.this, DialActivity.class);
                //intent.putExtra("CONTACTNUMBER", foodData.getFoodContactNum());
                //startActivity(intent);
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse(Uri.encode(foodData.getFoodContactNum())));
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
        foodDetailImg = findViewById(R.id.detailImg);
        foodDetailTitle = findViewById(R.id.detailTitle);
        foodDetailLandmark = findViewById(R.id.detailLandmark);
        foodDetailProvince = findViewById(R.id.detailProvince);
        foodDetailDescription = findViewById(R.id.detailDescription);
        foodDetailContact = findViewById(R.id.detailContact);
        foodDetailEmail = findViewById(R.id.detailEmail);
        foodDetailPosted = findViewById(R.id.detailDatePosted);
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