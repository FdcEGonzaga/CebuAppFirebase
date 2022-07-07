package com.example.cebuapp.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.cebuapp.Helper.DataFetcher;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.User.Account.ChangePassActivity;
import com.example.cebuapp.Helper.HelperUtilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout loginForm, noInternet;
    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView linkregister, linkchangepass;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Boolean errors;
    private String userName;
    private FirebaseUser currentUser;
    private int countFail;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getInstance().getCurrentUser();
        countFail = 0;

        if (isConnectedToInternet()) {
            // Check if user is signed before and redirect to homepage
            if (currentUser != null) {
                userName = DataFetcher.getUserName(currentUser.getUid());
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
        } else {
            // alert offline dialog
            new AlertDialog.Builder(LoginActivity.this)
            .setTitle("CebuApp")
            .setMessage("You're offline. Do you want to continue?")
            .setCancelable(false)
            .setPositiveButton("Yes", null)
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            })
            .create().show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        linkchangepass = findViewById(R.id.linkchangepass);
        linkregister = findViewById(R.id.linkregister);
        progressBar = findViewById(R.id.progressBar);
        loginForm = findViewById(R.id.loginForm);
        noInternet = findViewById(R.id.noInternet);

        // others
        btnLogin.setOnClickListener(this);
        linkchangepass.setOnClickListener(this);
        linkregister.setOnClickListener(this);
        
        if (isConnectedToInternet()) {
            Toast.makeText(this, "Connected to the internet.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linkchangepass:
                startActivity(new Intent(LoginActivity.this, ChangePassActivity.class));
                break;
            case R.id.linkregister:
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                break;
            case R.id.btnLogin:
                loginUser();
                break;
        }
    }

    private void loginUser() {
        String loginEmail = inputEmail.getText().toString().trim();
        String loginPass = inputPassword.getText().toString().trim();
        errors = false;

        if (loginEmail.isEmpty()) {
            inputEmail.setError("Email is required");
            inputEmail.requestFocus();
            errors = true;
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
            inputEmail.setError("Please provide a valid email.");
            inputEmail.requestFocus();
            errors = true;
            return;
        }

        if (loginPass.isEmpty()) {
            inputPassword.setError("Password is required.");
            inputPassword.requestFocus();
            errors = true;
            return;
        }

        if (loginPass.length() < 6) {
            inputPassword.setError("Minimum password length is 6 characters.");
            inputPassword.requestFocus();
            errors = true;
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        if (!errors && errors.equals(false)) {
            btnLogin.setEnabled(false);
            // check if connected to internet
            if (isConnectedToInternet()) {
                mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                if (task.isSuccessful()) {

                                    // if (user.isEmailVerified()) {
                                    //     progressBar.setVisibility(View.INVISIBLE);
                                    //     Toast.makeText(getApplicationContext(), "Logged in successfully!",
                                    //                                Toast.LENGTH_SHORT).show();
                                    //     startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    //     finish();
                                    // } else {
                                    //     progressBar.setVisibility(View.INVISIBLE);
                                    //     user.sendEmailVerification();
                                    //      showOkAlert("Email verification pending, please check your email to verify your account.");
                                    // }

                                    Toast.makeText(getApplicationContext(), "Logged in successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    countFail += 1;
                                    if (countFail < 3) {
                                        HelperUtilities.showOkAlert(LoginActivity.this, "Login failed, email or password is incorrect.");
                                    } else {
                                        // alert offline dialog
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("CebuApp")
                                                .setCancelable(false)
                                                .setMessage("Login failed for 3 consecutive times, do you want to change your password?")
                                                .setNegativeButton("No", null)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        inputPassword.setText("");
                                                        startActivity(new Intent(LoginActivity.this, ChangePassActivity.class));
                                                    }
                                                })
                                                .create().show();
                                    }
                                }
                                btnLogin.setEnabled(true);
                            }
                        }, 300);
                    }
                });
            } else {
                // if no internet connection
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        HelperUtilities.showNoInternetAlert(LoginActivity.this);
                    }
                }, 300);
            }

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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(LoginActivity.this)
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
                }).create().show();
    }
}