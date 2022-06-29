package com.example.cebuapp.controllers.User.TouristSpots;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.HomeActivity;
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
    private ProgressDialog dialog;
    private ScrollView spotContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spots_details);

        castComponents();
        getCurrentDate();

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        spotContainer.setVisibility(View.GONE);

        intent = getIntent();

        if (isConnectedToInternet()) {
            // check intent extra for editing food field
            spotsData = (TouristSpot) getIntent().getSerializableExtra("FIREBASE_DATA");

            // load img first
            new ShowImageUrl((ImageView) findViewById(R.id.detailImg))
                    .execute(spotsData.getTouristSpotImg());

            // load other data
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (spotsData != null) {
                        spotsDetailTitle.setText(spotsData.getTouristSpotTitle());
                        spotsDetailLandmark.setText(spotsData.getTouristSpotAddress());
                        spotsDetailProvince.setText(spotsData.getTouristSpotProvince() + ", CEBU");
                        spotsDetailDescription.setText(spotsData.getTouristSpotDescription());
                        spotsDetailContact.setText(spotsData.getTouristSpotContactNum());
                        spotsDetailEmail.setText(spotsData.getTouristSpotContactEmail());
                        spotsDetailPosted.setText(spotsData.getTouristSpotPosted());

                        // show container
                        spotContainer.setVisibility(View.VISIBLE);
                    }
                }
            }, 1000);

            // back btn
            backBtn.setOnClickListener(v -> {
                intent = new Intent(new Intent(getApplicationContext(), SpotsActivity.class));
                intent.putExtra("provSpinPos", spotsData.getPos());
                startActivity(intent);
                finish();
            });

            // call food are btn
            callBtn.setOnClickListener(v -> {
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(spotsData.getTouristSpotContactNum().trim())));
                startActivity(intent);
            });

            // email food are btn
            emailBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"fdc.egonzaga@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "CEBU APP - " + spotsData.getTouristSpotTitle().trim());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            });

        } else {
            HelperUtilities.showNoInternetAlert(SpotsDetailsActivity.this);
        }
    }

    private void castComponents() {
        spotContainer = findViewById(R.id.spotContainer);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(new Intent(getApplicationContext(), SpotsActivity.class));
        intent.putExtra("provSpinPos", spotsData.getPos());
        startActivity(intent);
        finish();
    }
}