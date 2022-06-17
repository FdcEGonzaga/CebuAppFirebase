package com.example.cebuapp.controllers.User.JobPosts;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.User.LatestNews.NewsActivity;
import com.example.cebuapp.controllers.User.LatestNews.NewsDetailsActivity;
import com.example.cebuapp.model.DatabaseHelper;
import com.example.cebuapp.model.JobPosts;

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
    private String formattedDate, jobUrlString;
    private Button applyJobBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_details);

        castComponents();
        getCurrentDate();
        intent = getIntent();

        if (isConnectedToInternet()) {
            // check intent extra for editing job field
            JobPosts jobData = (JobPosts) getIntent().getSerializableExtra("FIREBASE_DATA");

            String yearsExp = jobData.getJobPostYearExp();
            if (yearsExp.equals(0) || yearsExp.equals("0")) {
                yearsExp = "No experience required";
            } else if (yearsExp.equals(1) || yearsExp.equals("1")){
                yearsExp = "At least 1 year experience";
            } else {
                yearsExp = "At least " + yearsExp + " years experience";
            }

            if (jobData != null) {
                jobDetailCompany.setText(jobData.getJobPostCompany());
                jobDetailTitle.setText(jobData.getJobPostTitle());
                jobDetailYears.setText(yearsExp);
                jobDetailSalary.setText("P" + jobData.getJobPostSalary() + " salary offer");
                jobDetailDesc.setText(jobData.getJobPostDescription());
                jobDetailDesc2.setText(jobData.getJobPostCompanyDetails());
                jobDetailField.setText(jobData.getJobPostProvince());
                jobDetailDate.setText(jobData.getJobPostPosted());
                jobUrlString = jobData.getJobPostLink();
            }
        } else {
            Toast.makeText(this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
        }

        // back btn
        backBtn.setOnClickListener(v -> {
            intent = new Intent(JobDetailsActivity.this, JobsActivity.class);
            startActivity(intent);
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
        System.out.println("Current time => " + c);

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
}