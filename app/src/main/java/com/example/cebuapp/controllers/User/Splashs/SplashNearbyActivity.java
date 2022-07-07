package com.example.cebuapp.controllers.User.Splashs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.Helper.DataFetcher;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.controllers.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashNearbyActivity extends AppCompatActivity {

    private Button splashBackBtn, splashNextBtn;
    private FirebaseAuth mAuth;
    private String userName;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_nearby);

        splashBackBtn = findViewById(R.id.splash_back_btn);
        splashNextBtn = findViewById(R.id.splash_next_btn);

        // get the userName before going to HomeActivity
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            userName = DataFetcher.getUserName(firebaseUser.getUid());
        }

        splashBackBtn.setOnClickListener(view-> {
            startActivity(new Intent(SplashNearbyActivity.this, SplashTouristActivity.class));
            finish();
        });

        splashNextBtn.setOnClickListener(view-> {
            startActivity(new Intent(SplashNearbyActivity.this, HomeActivity.class));
            finish();
        });
    }
}