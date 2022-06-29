package com.example.cebuapp.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.cebuapp.Helper.DataFetcher;
import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageFoodAreas.ManageFoodAreasActivity;
import com.example.cebuapp.controllers.Admin.ManageJobFields.ManageJobsFieldsActivity;
import com.example.cebuapp.controllers.Admin.ManageJobPosts.ManageJobsPostsActivity;
import com.example.cebuapp.controllers.Admin.ManageProvinces.ManageProvincesActivity;
import com.example.cebuapp.controllers.Admin.ManageTouristSpots.ManageTouristSpotsActivity;
import com.example.cebuapp.controllers.User.Account.AccountActivity;
import com.example.cebuapp.controllers.User.Account.CRUDAccountUsers;
import com.example.cebuapp.controllers.User.FoodAreas.FoodActivity;
import com.example.cebuapp.controllers.User.FoodAreas.FoodDetailsActivity;
import com.example.cebuapp.controllers.User.JobPosts.JobsActivity;
import com.example.cebuapp.controllers.User.LatestNews.NewsActivity;
import com.example.cebuapp.controllers.User.NearbyPlaces.MapsActivity;
import com.example.cebuapp.controllers.User.TouristSpots.SpotsActivity;
import com.example.cebuapp.model.DatabaseHelper;
import com.example.cebuapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout normalUserUi, adminUserUi, manageJobsDropDown;
    private CardView cardNews, cardJobs, cardSpots, cardFoods, cardMap, cardAccount;
    private ImageButton imgNews, imgJobs, imgSpots, imgFoods, imgMap, imgAccount;
    private TextView userGreeting, userLogout, adminGreeting, adminLogout;
    private Button btnManageProvinces, btnManageJobs, btnManageJobFields , btnManageJobPosts, btnManageFoodAreas, btnManageTouristSpots;
    private FirebaseAuth mAuth;
    private CRUDAccountUsers crudUsers;
    private Intent intent;
    private String currentUserEmail, userName;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (isConnectedToInternet()) {
            castComponents();
            greetUserName();
            setOnclickListeners();

            // ADMIN SIDE - check intent extras
            Boolean isFromMngJobPostActivity = getIntent().getBooleanExtra("isFromMngJobPostActivity", false);
            if (isFromMngJobPostActivity) {
                // show Job Post drop down menu
                manageJobsDropDown.setVisibility(View.VISIBLE);
            }

        } else {
            HelperUtilities.showNoInternetAlert(HomeActivity.this);
        }

    }

    private void greetUserName() {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        currentUserEmail = firebaseUser.getEmail();
        userName = DataFetcher.getUserName(firebaseUser.getUid());

        // show ui, check if admin user
        if (currentUserEmail.equals("admin@gmail.com")) {
            adminGreeting.setText("Hi, Admin!");
            normalUserUi.setVisibility(View.GONE);
            adminUserUi.setVisibility(View.VISIBLE);
        } else {
            if (userName == null) {
                userName = DataFetcher.getUserName(firebaseUser.getUid());
            }
            userGreeting.setText("Hi, "+ userName +"!");
            normalUserUi.setVisibility(View.VISIBLE);
            adminUserUi.setVisibility(View.GONE);
        }
    }

    private void castComponents() {
        // normal user
        normalUserUi = findViewById(R.id.normal_user_ui);
        cardNews = findViewById(R.id.cardNews);
        cardJobs = findViewById(R.id.cardJobs);
        cardSpots = findViewById(R.id.cardSpots);
        cardFoods = findViewById(R.id.cardFoods);
        cardMap = findViewById(R.id.cardMap);
        cardAccount = findViewById(R.id.cardAccount);
        userGreeting = findViewById(R.id.user_greeting);
        userLogout = findViewById(R.id.user_logout);
        imgNews = findViewById(R.id.imgNews);
        imgJobs = findViewById(R.id.imgJobs);
        imgSpots = findViewById(R.id.imgSpots);
        imgFoods = findViewById(R.id.imgFoods);
        imgMap = findViewById(R.id.imgMap);
        imgAccount = findViewById(R.id.imgAccount);

        // admin user
        adminUserUi = findViewById(R.id.admin_user_ui);
        manageJobsDropDown = findViewById(R.id.manage_jobs_dropdown);
        btnManageProvinces = findViewById(R.id.manage_provinces);
        btnManageJobs = findViewById(R.id.manage_jobs);
        btnManageJobFields = findViewById(R.id.manage_job_fields);
        btnManageJobPosts = findViewById(R.id.manage_job_posts);
        btnManageFoodAreas = findViewById(R.id.manage_food_areas);
        btnManageTouristSpots = findViewById(R.id.manage_tourist_spots);
        adminGreeting = findViewById(R.id.admin_greeting);
        adminLogout = findViewById(R.id.admin_logout);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, DatabaseHelper.JOBFIELDS);
    }

    private void setOnclickListeners() {
        /**
         * USER btns
         **/
        cardNews.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), NewsActivity.class));
        });
        cardJobs.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), JobsActivity.class));
        });
        cardFoods.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), FoodActivity.class));
        });
        cardSpots.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), SpotsActivity.class));
        });
        cardMap.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        });
        cardAccount.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), AccountActivity.class));
        });
        imgNews.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), NewsActivity.class));
        });
        imgJobs.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), JobsActivity.class));
        });
        imgFoods.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), FoodActivity.class));
        });
        imgSpots.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), SpotsActivity.class));
        });
        imgMap.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        });
        imgAccount.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), AccountActivity.class));
        });
        userLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("CebuApp")
                    .setMessage("Are you sure you want to logout?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            finish();
                        }
                    }).create().show();
            }
        });

        /**
         * ADMIN btns
        **/
        btnManageProvinces.setOnClickListener(view-> {
            intent = new Intent(getApplicationContext(), ManageProvincesActivity.class);
            startActivity(intent);
        });
        btnManageJobs.setOnClickListener(view-> {
            if (manageJobsDropDown.getVisibility() == View.GONE) {
                manageJobsDropDown.setVisibility(View.VISIBLE);
            } else {
                manageJobsDropDown.setVisibility(View.GONE);
            }
        });
        btnManageJobFields.setOnClickListener(view-> {
            intent = new Intent(getApplicationContext(), ManageJobsFieldsActivity.class);
            startActivity(intent);
        });
        btnManageJobPosts.setOnClickListener(view-> {
            intent = new Intent(getApplicationContext(), ManageJobsPostsActivity.class);
            startActivity(intent);
        });
        btnManageFoodAreas.setOnClickListener(view-> {
            intent = new Intent(getApplicationContext(), ManageFoodAreasActivity.class);
            startActivity(intent);
        });
        btnManageTouristSpots.setOnClickListener(view-> {
            intent = new Intent(getApplicationContext(), ManageTouristSpotsActivity.class);
            startActivity(intent);
        });
        adminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("CebuApp")
                        .setMessage("Are you sure you want to logout?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {

                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).create().show();
            }
        });
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
        new AlertDialog.Builder(HomeActivity.this)
        .setTitle("CebuApp")
        .setMessage("Do you want to exit CebuApp?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        })
        .create().show();
    }
}