package com.example.cebuapp.controllers.User.Splashs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.R;


public class SplashScreenActivity extends AppCompatActivity {

    private static int TIME_OUT = 4000;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // request feature must be called first
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // then show the content view
        setContentView(R.layout.activity_splash_screen);

        final View myLayout = findViewById(R.id.mysplash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                intent = new Intent(getApplicationContext(), SplashNewsActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME_OUT);
    }
}