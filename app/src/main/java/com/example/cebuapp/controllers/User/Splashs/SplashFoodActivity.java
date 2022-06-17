package com.example.cebuapp.controllers.User.Splashs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.R;

public class SplashFoodActivity extends AppCompatActivity {

    private Button splashBackBtn, splashNextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_food);

        splashBackBtn = findViewById(R.id.splash_back_btn);
        splashNextBtn = findViewById(R.id.splash_next_btn);

        splashBackBtn.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), SplashJobsActivity.class));
            finish();
        });

        splashNextBtn.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), SplashTouristActivity.class));
            finish();
        });
    }
}