package com.example.cebuapp.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.User.Account.ChangePassActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView linkregister, linkchangepass;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Intent intent;

    @Override
    public void onStart() {
        super.onStart();
        if (isConnectedToInternet()) {
            Toast.makeText(this, "Connected to the internet.", Toast.LENGTH_SHORT).show();
            // Check if user is signed in redirect to homepage
            FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
        } else {
            // alert offline dialog
            new AlertDialog.Builder(LoginActivity.this)
            .setTitle("CebuApp")
            .setMessage("You're offline. Do you want to login as offline?")
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            })
            .setPositiveButton("Yes", null)
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

        btnLogin.setOnClickListener(this);
        linkchangepass.setOnClickListener(this);
        linkregister.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linkchangepass:
                if (!isConnectedToInternet()) {
                    // alert offline dialog
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("CebuApp")
                            .setMessage("To reset your password, please connect to the internet.")
                            .setPositiveButton("Ok", null)
                            .create().show();
                } else {
                    startActivity(new Intent(LoginActivity.this, ChangePassActivity.class));
                }
                break;
            case R.id.linkregister:
                if (!isConnectedToInternet()) {
                    // alert offline dialog
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("CebuApp")
                            .setMessage("To register an account, please connect to the internet.")
                            .setPositiveButton("Ok", null)
                            .create().show();
                } else {
                    startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                }
                break;
            case R.id.btnLogin:
                loginUser();
                break;
        }
    }

    private void loginUser() {
        String loginEmail = inputEmail.getText().toString().trim();
        String loginPass = inputPassword.getText().toString().trim();

        if (loginEmail.isEmpty()) {
            inputEmail.setError("Email is required");
            inputEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
            inputEmail.setError("Please provide a valid email.");
            inputEmail.requestFocus();
            return;
        }

        if (loginPass.isEmpty()) {
            inputPassword.setError("Password is required.");
            inputPassword.requestFocus();
            return;
        }

        if (loginPass.length() < 6) {
            inputPassword.setError("Minimum password length is 6 characters.");
            inputPassword.requestFocus();
            return;
        }

        // check if connected to internet
        if (isConnectedToInternet()) {
            // firebase login
            btnLogin.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

                        btnLogin.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Logged in successfully!",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        btnLogin.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        showOkAlert("Login failed, please try again.");
                    }
                }
            });
        } else {
            // if no internet connection
            btnLogin.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ((loginEmail.equals("elmer@gmail.com") && loginPass.equals("admin123"))
                    || (loginEmail.equals("admin@gmail.com") && loginPass.equals("admin123"))) {
                        btnLogin.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Logged in as offline successfully!",
                                Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("loginEmail", loginEmail);
                        startActivity(intent);
                        finish();
                    } else {
                        btnLogin.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        showOkAlert("Failed offline login, wrong credentials.");
                    }
                }
            }, 1000);
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

    private void showOkAlert(String msg) {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("CebuApp")
                .setMessage(msg)
                .setNegativeButton(android.R.string.ok, null)
                .create().show();
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