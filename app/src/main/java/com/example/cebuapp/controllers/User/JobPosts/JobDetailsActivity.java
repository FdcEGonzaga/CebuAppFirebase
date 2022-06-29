package com.example.cebuapp.controllers.User.JobPosts;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageJobPosts.ManageJobsPostsActivity;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.controllers.User.FoodAreas.FoodActivity;
import com.example.cebuapp.controllers.User.LatestNews.NewsActivity;
import com.example.cebuapp.controllers.User.LatestNews.NewsDetailsActivity;
import com.example.cebuapp.controllers.User.TouristSpots.SpotsDetailsActivity;
import com.example.cebuapp.model.DatabaseHelper;
import com.example.cebuapp.model.JobPosts;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class JobDetailsActivity extends AppCompatActivity {
    private Intent intent;
    private ImageButton backBtn;
    private ImageView jobDetailImg;
    private TextView jobDetailTitle, jobDetailCompany, jobDetailYears, jobDetailField, jobDetailDate,
            jobDetailSalary, jobDetailDesc, jobDetailDesc2;
    private String formattedDate, jobUrlString, yearsExp;
    private Button applyJobBtn;
    private ProgressDialog dialog;
    private ScrollView jobDetailContainer;
    private JobPosts jobData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_details);

        castComponents();
        getCurrentDate();

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        jobDetailContainer.setVisibility(View.GONE);

        intent = getIntent();

        dialog.setTitle("Loading Job Post details...");
        dialog.show();

        if (isConnectedToInternet()) {
            // check intent extra for editing job field
            jobData = (JobPosts) getIntent().getSerializableExtra("FIREBASE_DATA");

            if (jobData != null) {
                // load img first
                new ShowImageUrl((ImageView) findViewById(R.id.jobDetailImg))
                        .execute(jobData.getJobPostImg());

                // filter year experience
                yearsExp = jobData.getJobPostYearExp();
                if (yearsExp.equals(0) || yearsExp.equals("0")) {
                    yearsExp = "No experience required";
                } else if (yearsExp.equals(1) || yearsExp.equals("1")){
                    yearsExp = "At least 1 year experience";
                } else {
                    yearsExp = "At least " + yearsExp + " years experience";
                }

                // load other data
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            jobDetailCompany.setText(jobData.getJobPostCompany());
                            jobDetailTitle.setText(jobData.getJobPostTitle());
                            jobDetailYears.setText(yearsExp);
                            jobDetailSalary.setText("P" + jobData.getJobPostSalary() + " salary offer");
                            jobDetailDesc.setText(jobData.getJobPostDescription());
                            jobDetailDesc2.setText(jobData.getJobPostCompanyDetails());
                            jobDetailField.setText(jobData.getJobPostProvince());
                            jobDetailDate.setText(jobData.getJobPostPosted());
                            jobUrlString = jobData.getJobPostLink();
                            dialog.dismiss();

                            // show container
                            jobDetailContainer.setVisibility(View.VISIBLE);
                        }
                }, 1000);
            }

        } else {
            HelperUtilities.showNoInternetAlert(JobDetailsActivity.this);
        }

        // back btn
        backBtn.setOnClickListener(v -> {
            intent = new Intent(new Intent(getApplicationContext(), JobsActivity.class));
            intent.putExtra("jobFieldSpinPos", jobData.getSpinnerPos());
            startActivity(intent);
            finish();
        });

        // apply job btn
        applyJobBtn.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobUrlString));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            try {
                getApplicationContext().startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // Chrome browser presumably not installed so allow user to choose instead
                intent.setPackage(null);
                getApplicationContext().startActivity(intent);
            }
        });
    }

    private void castComponents() {
        jobDetailContainer = findViewById(R.id.jobDetailContainer);
        jobDetailImg = findViewById(R.id.jobDetailImg);
        jobDetailTitle = findViewById(R.id.jobDetailTitle);
        jobDetailYears = findViewById(R.id.jobDetailYears);
        jobDetailDesc = findViewById(R.id.jobDetailDesc);
        jobDetailDesc2 = findViewById(R.id.jobDetailDesc2);
        jobDetailSalary = findViewById(R.id.jobDetailSalary);
        jobDetailField = findViewById(R.id.jobDetailField);
        jobDetailCompany = findViewById(R.id.jobDetailCompany);
        jobDetailDate = findViewById(R.id.jobDetailDate);
        backBtn = findViewById(R.id.back_btn);
        applyJobBtn = findViewById(R.id.applyJobBtn);
    }

    private void getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy H:m", Locale.getDefault());
        formattedDate = df.format(c);
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
        intent = new Intent(new Intent(getApplicationContext(), JobsActivity.class));
        intent.putExtra("jobFieldSpinPos", jobData.getSpinnerPos());
        startActivity(intent);
        finish();
    }
}