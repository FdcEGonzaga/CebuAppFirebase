package com.example.cebuapp.controllers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText inputFullname;
    private EditText inputEmail;
    private EditText inputPhone;
    private EditText inputConfirmPassword;
    private EditText inputPassword;
    private Button registerBtn;
    private TextView linkLogin;
    private ProgressBar progressBar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        castMyComponents();

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnectedToInternet()) {
                    registerUser();
                } else {
                    // alert offline dialog
                    new AlertDialog.Builder(RegistrationActivity.this)
                        .setTitle("CebuApp")
                        .setMessage("Please connect to the internet to register.")
                        .setPositiveButton("Ok", null)
                        .create().show();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }


    private void castMyComponents() {
        inputFullname = (EditText) findViewById(R.id.txtfullname);
        inputEmail = (EditText) findViewById(R.id.txtEmail);
        inputPhone = (EditText) findViewById(R.id.txtPhone);
        inputPassword = (EditText) findViewById(R.id.txtPassword);
        inputConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        linkLogin = (TextView) findViewById(R.id.linklogin);
        registerBtn = (Button) findViewById(R.id.btnRegister);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void showOkAlert(String msg) {
        new AlertDialog.Builder(RegistrationActivity.this)
                .setTitle("CebuApp")
                .setMessage(msg)
                .setNegativeButton(android.R.string.ok, null)
                .create().show();
    }

    private void registerUser() {
        String fullname = inputFullname.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String passwordConfirm = inputConfirmPassword.getText().toString().trim();
        Integer role = 0; // normal user

        if (fullname.isEmpty()) {
            inputFullname.setError("Fullname is required.");
            inputFullname.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            inputEmail.setError("Email is required.");
            inputEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Please provide a valid email.");
            inputEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            inputPhone.setError("Phone number is required.");
            inputPhone.requestFocus();
            return;
        }

        if (!HelperUtilities.isValidPhone(phone)) {
            inputPhone.setError("Phone number is invalid.");
            inputPhone.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            inputPassword.setError("Password is required.");
            inputPassword.requestFocus();
            return;
        }

        if (passwordConfirm.isEmpty()) {
            inputConfirmPassword.setError("Password confirmation is required.");
            inputConfirmPassword.requestFocus();
            return;
        }

        if (password.length() < 6 ) {
            inputPassword.setError("Minimum password length is 6 characters.");
            inputPassword.requestFocus();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            inputConfirmPassword.setError("Passwords must be the same.");
            inputConfirmPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            User user = new User(fullname, email, phone, role);

                            FirebaseDatabase.getInstance().getReference("cebuapp_users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegistrationActivity.this,
                                                    "Registration successful!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegistrationActivity.this,
                                                    "Failed registering, please try again.", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                        } else {
                            Toast.makeText(RegistrationActivity.this,
                                    "Failed registering, no internet access.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
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