package com.example.cebuapp.controllers.User.Account;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.controllers.LoginActivity;
import com.example.cebuapp.controllers.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {
    private LinearLayout layoutsentrequest;
    private EditText inputResetEmail;
    private TextView linklogin;
    private Button btnResetPass;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        layoutsentrequest = findViewById(R.id.layoutsentrequest);
        linklogin = findViewById(R.id.linklogin);
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

        linklogin.setOnClickListener(v-> {
            startActivity(new Intent(ChangePassActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void resetPassword() {
        String email = inputResetEmail.getText().toString().trim();

        if (email.isEmpty()) {
            inputResetEmail.setError("Enter your email to request an account password reset.");
            inputResetEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputResetEmail.setError("Please provide a valid email.");
            inputResetEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        if (isConnectedToInternet()) {
            btnResetPass.setEnabled(false);
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);

                            if (task.isSuccessful()) {
                                HelperUtilities.showOkAlert(ChangePassActivity.this,
                                        "Change password request successful, please check the inbox on your email ''" + email+
                                                "'' and complete your password change.");
                                inputResetEmail.setText("");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        layoutsentrequest.setVisibility(View.VISIBLE);
                                    }
                                }, 500);
                            } else {
                                // alert offline dialog
                                new AlertDialog.Builder(ChangePassActivity.this)
                                        .setTitle("CebuApp")
                                        .setMessage("Failed password reset request, your email is not yet registered to Cebu App.")
                                        .setNegativeButton("OK", null)
                                        .setPositiveButton("REGISTER NOW", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(ChangePassActivity.this, RegistrationActivity.class));
                                                finish();
                                            }
                                        })
                                        .create().show();
                            }
                            btnResetPass.setEnabled(true);
                        }
                    }, 300);
                }
            });
        } else {
            // offline dialog
            progressBar.setVisibility(View.INVISIBLE);
            HelperUtilities.showNoInternetAlert(ChangePassActivity.this);
        }
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