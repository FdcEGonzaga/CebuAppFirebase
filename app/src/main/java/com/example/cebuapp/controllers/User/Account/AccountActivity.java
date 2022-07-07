package com.example.cebuapp.controllers.User.Account;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageTouristSpots.ManageTouristSpotsActivity;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.controllers.LoginActivity;
import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.controllers.User.FoodAreas.FoodActivity;
import com.example.cebuapp.controllers.User.FoodAreas.FoodFavoritesFragment;
import com.example.cebuapp.controllers.User.FoodAreas.FoodPostOwnedFragment;
import com.example.cebuapp.controllers.User.TouristSpots.SpotsActivity;
import com.example.cebuapp.model.TouristSpot;
import com.example.cebuapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import io.grpc.LoadBalancer;

public class AccountActivity extends AppCompatActivity {
    // Image uploading
    private Uri imageUri;
    private ImageView accountPhoto, img_input;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String editImgVal;
    private String noPropic = "no_propic";

    // others
    private Button btnEditProfileSave, btnEditProfileCancel, btnChangeEmailSave, btnChangeEmailCancel, btnLogout;
    private LinearLayout profileView, profileEdit, emailChange;
    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private String userID;
    private TextView accountFullname, accountEmail, accountPhone;
    private EditText editFullname, editEmail, editPhone;
    private ProgressDialog dialog;
    private FirebaseUser loggedUser;
    private ImageButton backBtn, settingsBtn;
    private Intent intent;
    private Boolean errors, hasOldPhoto;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        castComponents();
        // init alert dialog
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        handler = new Handler();

        // init img firebase Storage(img)
        storageReference = FirebaseStorage.getInstance().getReference("Images");

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
                            String photo = userProfile.photo;
                            String fullname = userProfile.fullname;
                            String email = userProfile.email;
                            String phone = userProfile.phone;

                            if (photo != noPropic && !photo.equals(noPropic)) {
                                Picasso.get().load(photo).into(accountPhoto);
                            }
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
            HelperUtilities.showNoInternetAlert(AccountActivity.this);
        }

        // listeners
        setListeners();
    }

    private void castComponents() {
        profileView = findViewById(R.id.profile_view);
        profileEdit = findViewById(R.id.profile_edit);
        emailChange = findViewById(R.id.email_change);
        settingsBtn = findViewById(R.id.settings_btn);
        backBtn = findViewById(R.id.back_btn);
        btnLogout = findViewById(R.id.btn_logout);

        // viewed profile
        accountFullname = findViewById(R.id.account_fullname);
        accountEmail = findViewById(R.id.account_email);
        accountPhone = findViewById(R.id.account_phone);
        accountPhoto = findViewById(R.id.account_photo);

        // editing email
        editEmail = findViewById(R.id.edit_email);
        btnChangeEmailSave = findViewById(R.id.btn_change_email_save);
        btnChangeEmailCancel = findViewById(R.id.btn_change_email_cancel);

        // editing profile
        img_input = findViewById(R.id.img_input);
        editFullname = findViewById(R.id.edit_fullname);
        editPhone = findViewById(R.id.edit_phone);
        btnEditProfileSave = findViewById(R.id.btn_edit_profile_save);
        btnEditProfileCancel = findViewById(R.id.btn_edit_profile_cancel);
    }

    private void setListeners() {
        backBtn.setOnClickListener(v-> {
            intent = new Intent(AccountActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // setting btn
        settingsBtn.setOnClickListener(v-> {
            PopupMenu popupMenu = new PopupMenu(AccountActivity.this, settingsBtn);
            popupMenu.inflate(R.menu.user_account_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.menu_edit_profile:
                        menuEditProfile();
                        break;

                    case R.id.menu_change_email:
                        menuChangeEmail();
                        break;

                    case R.id.menu_change_pass:
                        menuChangePass();
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        img_input.setOnClickListener(v-> {
            choosingOfPicture();
        });

        btnEditProfileCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AccountActivity.this)
                        .setTitle("CebuApp")
                        .setMessage("Are you sure you want to cancel editing your account profile?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                backBtn.setVisibility(View.VISIBLE);
                                settingsBtn.setVisibility(View.VISIBLE);
                                profileView.setVisibility(View.VISIBLE);
                                btnLogout.setVisibility(View.VISIBLE);
                                profileEdit.setVisibility(View.GONE);
                                img_input.setImageResource(R.drawable.custom_input_field);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
        });

        btnChangeEmailCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AccountActivity.this)
                        .setTitle("CebuApp")
                        .setMessage("Are you sure you want to cancel editing your account email?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                backBtn.setVisibility(View.VISIBLE);
                                settingsBtn.setVisibility(View.VISIBLE);
                                profileView.setVisibility(View.VISIBLE);
                                profileEdit.setVisibility(View.GONE);
                                btnLogout.setVisibility(View.VISIBLE);
                                emailChange.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
        });

        btnLogout.setOnClickListener(v-> {
            new AlertDialog.Builder(AccountActivity.this)
                    .setTitle("CebuApp")
                    .setMessage("Are you sure you want to logout?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(AccountActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                            finish();
                        }
                    }).create().show();
        });
    }

    private void menuChangePass() {
        if (isConnectedToInternet()) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            String email = firebaseUser.getEmail();
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        HelperUtilities.showOkAlert(AccountActivity.this,
                                "Change password request successful, please check your email and complete your password change.");
                    } else {
                        Toast.makeText(AccountActivity.this,
                                "Failed reset password request, please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            HelperUtilities.showNoInternetAlert(AccountActivity.this);
        }
    }

    private void menuChangeEmail() {
        backBtn.setVisibility(View.INVISIBLE);
        settingsBtn.setVisibility(View.INVISIBLE);
        profileView.setVisibility(View.GONE);
        profileEdit.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);
        emailChange.setVisibility(View.VISIBLE);

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
                            String oldEmail = userProfile.email.trim();

                            // set input values for editing
                            editEmail.setText(oldEmail);
                            editEmail.requestFocus();

                            // get edit values then save
                            onClickChangeEmailProcess(userID, oldEmail);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AccountActivity.this, "Unable to connect to the internet.", Toast.LENGTH_LONG).show();
                    }
                });
            }

        } else {
            HelperUtilities.showOkAlert(AccountActivity.this,
                    "Please connect to the internet to edit your profile.");
        }
    }

    private void menuEditProfile() {
        backBtn.setVisibility(View.INVISIBLE);
        settingsBtn.setVisibility(View.INVISIBLE);
        profileView.setVisibility(View.GONE);
        emailChange.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);
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
                            String oldPhoto = userProfile.photo.trim();
                            String oldFullname = userProfile.fullname.trim();
                            String oldPhone = userProfile.phone.trim();
                            hasOldPhoto = false;

                            if (oldPhoto != noPropic && !oldPhoto.equals(noPropic)) {
                                hasOldPhoto = true;
                                Picasso.get().load(oldPhoto).into(img_input);
                            }
                            // set input values for editing
                            editFullname.requestFocus();
                            editFullname.setText(oldFullname);
                            editPhone.setText(oldPhone);

                            // get edit values then save
                            validateEditProfileChanges(userID, oldPhoto, oldFullname, oldPhone);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AccountActivity.this, "Unable to connect to the internet.", Toast.LENGTH_LONG).show();
                    }
                });
            }

        } else {
            HelperUtilities.showOkAlert(AccountActivity.this,
                    "Please connect to the internet to edit your profile.");
        }
    }

    private void onClickChangeEmailProcess(String userID,  String oldEmail) {
        btnChangeEmailSave.setOnClickListener(v-> {
            String editEmailVal = editEmail.getText().toString().trim();

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

            if (oldEmail.equals(editEmailVal)) {
                HelperUtilities.showOkAlert(AccountActivity.this, "Please make email changes.");
                return;
            } else {
                // update user's authentication db email
                FirebaseAuth.getInstance().getCurrentUser().updateEmail(editEmailVal)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // update Realtime DB table cebuapp_users
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("email", editEmailVal);
                                    updateAccountProfile(userID, hashMap).addOnSuccessListener(suc -> {
                                        new AlertDialog.Builder(AccountActivity.this)
                                                .setTitle("CebuApp")
                                                .setMessage("Your account email was updated successfully.")
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        intent = new Intent(AccountActivity.this, AccountActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }).create().show();

                                    }).addOnFailureListener(fail -> {
                                        HelperUtilities.showOkAlert(AccountActivity.this,
                                                "An error occured while updating your email, please try again.");
                                    });
                                } else {
                                    new AlertDialog.Builder(AccountActivity.this)
                                            .setTitle("CebuApp")
                                            .setMessage("You need to re-login your account to change your email and ensure that you're the one updating your profile." +
                                                    "\n\nDo you want to logout?")
                                            .setNegativeButton("Cancel", null)
                                            .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    FirebaseAuth.getInstance().signOut();
                                                    Toast.makeText(AccountActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                                                    finish();
                                                }
                                            }).create().show();
                                }
                            }
                        });
            }

        });
    }

    private void choosingOfPicture() {
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an Image"), 7);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                img_input.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    private void validateEditProfileChanges(String userID, String oldPhoto, String oldFullname, String oldPhone) {
        btnEditProfileSave.setOnClickListener(v-> {
            String editFullnameVal = editFullname.getText().toString().trim();
            String editPhoneVal = editPhone.getText().toString().trim();

            errors = false;
            if (img_input.getResources().equals(R.drawable.custom_input_field) || imageUri == null && hasOldPhoto == false) {
                HelperUtilities.showOkAlert(AccountActivity.this, "Please select a profile photo.");
                errors = true;
                return;
            }

            if (editFullnameVal.isEmpty()) {
                editFullname.setError("Your fullname is required");
                editFullname.requestFocus();
                errors = true;
                return;
            }

            if (editPhoneVal.isEmpty()) {
                editFullname.setError("Your contact number is required");
                editFullname.requestFocus();
                errors = true;
                return;
            }

            if (!HelperUtilities.isValidPhone(editPhoneVal)) {
                editPhone.setError("Phone number is invalid.");
                editPhone.requestFocus();
                errors = true;
                return;
            }

            if (imageUri != null) {
                dialog.setTitle("Saving Account Profile info...");
                dialog.show();

                StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() +'.'+ GetFileExtension(imageUri));
                UploadTask uploadTask = storageReference2.putFile(imageUri);
                uploadTask.addOnSuccessListener((OnSuccessListener) (taskSnapshot) -> {
                    // get the img uri
                    Task<Uri> downloadUri = storageReference2.getDownloadUrl();

                    downloadUri.addOnSuccessListener((OnSuccessListener)(uri) -> {
                        // construct img data
                        editImgVal = uri.toString();

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                // update Realtime DB table cebuapp_users
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("photo", editImgVal);
                                hashMap.put("fullname", editFullnameVal);
                                hashMap.put("phone", editPhoneVal);
                                updateAccountProfile(userID, hashMap).addOnSuccessListener(suc -> {
                                    new AlertDialog.Builder(AccountActivity.this)
                                            .setTitle("CebuApp")
                                            .setMessage("Your account profile was updated successfully.")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    hasOldPhoto = false;
                                                    intent = new Intent(AccountActivity.this, AccountActivity.class);
                                                    startActivity(intent);
                                                }
                                            }).create().show();

                                }).addOnFailureListener(fail -> {
                                    HelperUtilities.showOkAlert(AccountActivity.this,
                                            "An error occured while updating your profile, please try again.");
                                });
                            }
                        }, 300);
                    });
                });
            }
        });
    }

    public Task<Void> updateAccountProfile(String key, HashMap<String, Object> hashMap) {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        reference = fdb.getReference("cebuapp_users");
        return reference.child(key).updateChildren(hashMap);
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
        startActivity(new Intent(new Intent(AccountActivity.this, HomeActivity.class)));
        finish();
    }
}