package com.example.cebuapp.controllers.User.FoodAreas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.User.TouristSpots.SpotsDetailsActivity;
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
    private ProgressDialog dialog;
    private ScrollView foodContainer;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        castComponents();
        getCurrentDate();

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        foodContainer.setVisibility(View.GONE);

        intent = getIntent();

        dialog.setTitle("Loading Food Area details...");
        dialog.show();

        if (isConnectedToInternet()) {
            // check intent extra for editing food field
            foodData = (FoodArea) getIntent().getSerializableExtra("FIREBASE_DATA");

            // load img first
            new ShowImageUrl((ImageView) findViewById(R.id.detailImg))
                    .execute(foodData.getFoodImg());

            // load other data
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (foodData != null) {
                        foodDetailTitle.setText(foodData.getFoodTitle());
                        foodDetailLandmark.setText(foodData.getFoodAddress());
                        foodDetailProvince.setText(foodData.getFoodProvince() + ", CEBU");
                        foodDetailDescription.setText(foodData.getFoodDescription());
                        foodDetailContact.setText(foodData.getFoodContactNum());
                        foodDetailEmail.setText(foodData.getFoodContactEmail());
                        foodDetailPosted.setText(foodData.getFoodPosted());
                        dialog.dismiss();

                        // show container
                        foodContainer.setVisibility(View.VISIBLE);
                    }
                }
            }, 1000);

            // back btn
            backBtn.setOnClickListener(v -> {
                intent = new Intent(new Intent(getApplicationContext(), FoodActivity.class));
                intent.putExtra("provSpinPos", foodData.getSpinnerPos());
                startActivity(intent);
                finish();
            });

            // call food are btn
            callBtn.setOnClickListener(v -> {
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(foodData.getFoodContactNum().trim())));
                startActivity(intent);
            });

            // email food are btn
            emailBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"fdc.egonzaga@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "CEBU APP - " + foodData.getFoodTitle().trim());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            });

        } else {
            HelperUtilities.showNoInternetAlert(FoodDetailsActivity.this);
        }
    }

    private void castComponents() {
        frameLayout = findViewById(R.id.framelayout);
        foodContainer = findViewById(R.id.food_container);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(new Intent(getApplicationContext(), FoodActivity.class));
        intent.putExtra("provSpinPos", foodData.getSpinnerPos());
        startActivity(intent);
        finish();
    }
}