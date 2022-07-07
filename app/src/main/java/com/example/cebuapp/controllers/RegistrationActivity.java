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
import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.controllers.User.Splashs.SplashJobsActivity;
import com.example.cebuapp.controllers.User.Splashs.SplashNewsActivity;
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
    private Boolean errors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        castMyComponents();
        mAuth = FirebaseAuth.getInstance();

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
                    registerUser();
            }
        });

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

    private void registerUser() {
        String photo = "no_propic";
        String fullname = inputFullname.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String passwordConfirm = inputConfirmPassword.getText().toString().trim();
        Integer role = 0; // normal user
        errors = false;

        if (fullname.isEmpty()) {
            inputFullname.setError("Fullname is required.");
            inputFullname.requestFocus();
            errors = true;
            return;
        }

        if (email.isEmpty()) {
            inputEmail.setError("Email is required.");
            inputEmail.requestFocus();
            errors = true;
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Please provide a valid email.");
            inputEmail.requestFocus();
            errors = true;
            return;
        }

        if (phone.isEmpty()) {
            inputPhone.setError("Phone number is required.");
            inputPhone.requestFocus();
            errors = true;
            return;
        }

        if (!HelperUtilities.isValidPhone(phone)) {
            inputPhone.setError("Phone number is invalid.");
            inputPhone.requestFocus();
            errors = true;
            return;
        }

        if (password.isEmpty()) {
            inputPassword.setError("Password is required.");
            inputPassword.requestFocus();
            errors = true;
            return;
        }

        if (password.length() < 6 ) {
            inputPassword.setError("Minimum password length is 6 characters.");
            inputPassword.requestFocus();
            errors = true;
            return;
        }

        if (passwordConfirm.isEmpty()) {
            inputConfirmPassword.setError("Password confirmation is required.");
            inputConfirmPassword.requestFocus();
            errors = true;
            return;
        }

        if (!password.equals(passwordConfirm)) {
            inputConfirmPassword.setError("Passwords must be the same.");
            inputConfirmPassword.requestFocus();
            errors = true;
            return;
        }

        if (!errors && errors.equals(false)) {
            registerBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);

                                if (task.isSuccessful()) {
                                    User user = new User(photo, fullname, email, phone, role);
                                    // saving to realtime database
                                    FirebaseDatabase.getInstance().getReference("cebuapp_users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // alert offline dialog
                                                        new AlertDialog.Builder(RegistrationActivity.this)
                                                                .setTitle("CebuApp")
                                                                .setMessage("Welcome to CEBU APP, "+fullname+"! You are now registered successfully!")
                                                                .setPositiveButton("Get started!", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        startActivity(new Intent(RegistrationActivity.this, SplashNewsActivity.class));
                                                                        finish();
                                                                    }
                                                                })
                                                                .create().show();
                                                    } else {
                                                        Toast.makeText(RegistrationActivity.this,
                                                                "Sorry, there was an error while registering, please try again.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else {
                                    HelperUtilities.showNoInternetAlert(RegistrationActivity.this);
                                }
                                registerBtn.setEnabled(true);
                            }
                        }, 300);
                    }
                });
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