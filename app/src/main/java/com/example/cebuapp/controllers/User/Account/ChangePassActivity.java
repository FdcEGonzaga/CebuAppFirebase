package com.example.cebuapp.controllers.User.Account;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePassActivity extends AppCompatActivity {
    private EditText inputResetEmail;
    private Button btnResetPass;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        inputResetEmail = findViewById(R.id.inputResetEmail);
        btnResetPass = findViewById(R.id.btnResetPass);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = inputResetEmail.getText().toString().trim();

        if (email.isEmpty()) {
            inputResetEmail.setError("Email is required");
            inputResetEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputResetEmail.setError("Please provide a valid email.");
            inputResetEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ChangePassActivity.this,
                            "Please check your email to reset your password.", Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ChangePassActivity.this,
                            "Failed reset password request, please try again.", Toast.LENGTH_LONG).show();
                }
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
}