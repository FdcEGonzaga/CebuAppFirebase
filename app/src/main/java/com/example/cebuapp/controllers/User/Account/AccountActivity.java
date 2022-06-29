package com.example.cebuapp.controllers.User.Account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.controllers.LoginActivity;
import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AccountActivity extends AppCompatActivity {
    private Button btnEditProfile, btnEditProfileSave, btnEditProfileCancel, btnChangePass, btnLogout;
    private LinearLayout profileView, profileEdit, accountViewMainBtns;
    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private String userID;
    private TextView accountFullname, accountEmail, accountPhone, labelFullname, labelEmail, labelPhone;
    private EditText editFullname, editEmail, editPhone;
    private ProgressDialog dialog;
    private FirebaseUser loggedUser;
    private ImageButton backBtn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        castComponents();
        // init alert dialog
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        // hide profile editing
        profileEdit.setVisibility(View.GONE);

        // show user data
        if (isConnectedToInternet()) {
            mAuth = FirebaseAuth.getInstance();
            reference = FirebaseDatabase.getInstance().getReference("cebuapp_users");
            user = mAuth.getCurrentUser();
            userID = user.getUid();

            dialog.setTitle("Fetching your account's profile.");
            dialog.show();
            if (userID != null) {
                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);

                        if (userProfile != null) {
                            String fullname = userProfile.fullname;
                            String email = userProfile.email;
                            String phone = userProfile.phone;

                            accountFullname.setText(fullname);
                            accountEmail.setText(email);
                            accountPhone.setText(phone);
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AccountActivity.this, "Unable to connect to the internet.", Toast.LENGTH_LONG).show();
                        dialog.dismiss();}
                });
            }

        } else {
            // offline account data
            accountFullname.setText("Elmer Gonzaga");
            accountEmail.setText("elmer@gmail.com");
            accountPhone.setText("09123123123");
        }

        // editing profile
        onClickEditingProfile();

        // changing password request
        onClickChangePassword();

        // back btn
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v-> {
            intent = new Intent(AccountActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // logging out
        onClickLogout();
    }

    private void castComponents() {
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangePass = findViewById(R.id.btn_change_pass);
        accountViewMainBtns = findViewById(R.id.account_view_main_btns);
        btnLogout = findViewById(R.id.btn_logout);
        profileView = findViewById(R.id.profile_view);
        profileEdit = findViewById(R.id.profile_edit);

        // viewed profile
        accountFullname = findViewById(R.id.account_fullname);
        accountEmail = findViewById(R.id.account_email);
        accountPhone = findViewById(R.id.account_phone);

        // editing profile
        editFullname = findViewById(R.id.edit_fullname);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        btnEditProfileSave = findViewById(R.id.btn_edit_profile_save);
        btnEditProfileCancel = findViewById(R.id.btn_edit_profile_cancel);
    }

    // editing profile
    private void onClickEditingProfile() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBtn.setVisibility(View.INVISIBLE);
                accountViewMainBtns.setVisibility(View.GONE);
                profileView.setVisibility(View.GONE);
                profileEdit.setVisibility(View.VISIBLE);

                if (isConnectedToInternet()) {
                    mAuth = FirebaseAuth.getInstance();
                    user = mAuth.getCurrentUser();
                    userID = user.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("cebuapp_users");

                    if (userID != null) {
                        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User userProfile = snapshot.getValue(User.class);

                                if (userProfile != null) {
                                    String oldFullname = userProfile.fullname.trim();
                                    String oldEmail = userProfile.email.trim();
                                    String oldPhone = userProfile.phone.trim();

                                    // set input values for editing
                                    editFullname.setText(oldFullname);
                                    editFullname.requestFocus();
                                    editEmail.setText(oldEmail);
                                    editPhone.setText(oldPhone);

                                    // get edit values then save
                                    onClickEditingProfileSave(userID, oldFullname, oldEmail, oldPhone);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(AccountActivity.this, "Unable to connect to the internet.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } else {
                    new AlertDialog.Builder(AccountActivity.this)
                            .setTitle("CebuApp")
                            .setMessage("Please connect to the internet to edit your profile.")
                            .setPositiveButton("Ok", null).create().show();
                }
            }
        });

        btnEditProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save profile edit here
                Toast.makeText(AccountActivity.this, "No edit save code yet", Toast.LENGTH_SHORT).show();
            }
        });

        btnEditProfileCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountViewMainBtns.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                profileView.setVisibility(View.VISIBLE);
                profileEdit.setVisibility(View.GONE);
            }
        });
    }

    private void onClickEditingProfileSave(String userID, String oldFullname, String oldEmail, String oldPhone) {
        btnEditProfileSave.setOnClickListener(v-> {

            String editFullnameVal = editFullname.getText().toString().trim();
            String editEmailVal = editEmail.getText().toString().trim();
            String editPhoneVal = editPhone.getText().toString().trim();

            if (editFullnameVal.isEmpty()) {
                editFullname.setError("Your fullname is required");
                editFullname.requestFocus();
                return;
            }

            if (oldFullname.equals(editFullnameVal)) {
                editFullname.setError("Please make changes with your fullname.");
                editFullname.requestFocus();
                return;
            }

            if (editEmailVal.isEmpty()) {
                editEmail.setError("Your email is required.");
                editEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(editEmailVal).matches()) {
                editEmail.setError("Please provide a valid email.");
                editEmail.requestFocus();
                return;
            }

            if (!HelperUtilities.isValidPhone(editPhoneVal)) {
                editPhone.setError("Phone number is invalid.");
                editPhone.requestFocus();
                return;
            }

            // update user's authentication db email
            FirebaseAuth.getInstance().getCurrentUser().updateEmail(editEmailVal)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // update Realtime DB table cebuapp_users
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("email", editEmailVal);
                                hashMap.put("fullname", editFullnameVal);
                                hashMap.put("phone", editPhoneVal);
                                updateAccountProfile(userID, hashMap).addOnSuccessListener(suc -> {
                                    new AlertDialog.Builder(AccountActivity.this)
                                            .setTitle("CebuApp")
                                            .setMessage("Your account profile was updated successfully.")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    intent = new Intent(getApplicationContext(), AccountActivity.class);
                                                    startActivity(intent);
                                                }
                                            }).create().show();

                                }).addOnFailureListener(fail -> {
                                    Toast.makeText(AccountActivity.this,
                                            "Please re-login your account to ensure that you're the one updating your profile.", Toast.LENGTH_LONG).show();
                                });
                            } else {
                                Toast.makeText(AccountActivity.this,
                                        "An error occured while updating your email, please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }

    public Task<Void> updateAccountProfile(String key, HashMap<String, Object> hashMap) {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        reference = fdb.getReference("cebuapp_users");
        return reference.child(key).updateChildren(hashMap);
    }

    private void onClickChangePassword() {
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnectedToInternet()) {
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String email = firebaseUser.getEmail();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                new AlertDialog.Builder(AccountActivity.this)
                                        .setTitle("CebuApp")
                                        .setMessage("Change password request successful, please check your email to reset it.")
                                        .setPositiveButton("Ok", null).create().show();
                            } else {
                                Toast.makeText(AccountActivity.this,
                                        "Failed reset password request, please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    new AlertDialog.Builder(AccountActivity.this)
                            .setTitle("CebuApp")
                            .setMessage("Please connect to the internet to change your password.")
                            .setPositiveButton("Ok", null).create().show();
                }
            }
        });
    }

    private void onClickLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AccountActivity.this)
                        .setTitle("CebuApp")
                        .setMessage("Are you sure you want to logout?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(AccountActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).create().show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(new Intent(getApplicationContext(), HomeActivity.class)));
        finish();
    }
}