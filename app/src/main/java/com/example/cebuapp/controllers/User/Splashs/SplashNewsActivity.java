package com.example.cebuapp.controllers.User.Splashs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.R;

public class SplashNewsActivity extends AppCompatActivity {

    private Intent intent;
    private Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_news);

        startBtn = findViewById(R.id.startBtn);

        startBtn.setOnClickListener(view-> {
            startActivity(new Intent(SplashNewsActivity.this, SplashJobsActivity.class));
            finish();
        });

    }
}