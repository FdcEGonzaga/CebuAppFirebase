package com.example.cebuapp.controllers.User.JobPosts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageJobPosts.CRUDManageJobPosts;
import com.example.cebuapp.controllers.Admin.ManageJobPosts.ManageJobsPostsActivity;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.model.DatabaseHelper;
import com.example.cebuapp.model.JobPosts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JobsActivity extends AppCompatActivity {
    private Intent intent;
    private ListView jobsList;
    private TextView is_empty_list;
    private int jobID;

    // spinnner
    private Spinner jobsSpinner;
    private Button btnFindJobs;
    private DatabaseReference jobsSpinnerRef;
    private ArrayList<String> spinnerList;
    private ArrayAdapter<String> spinnerAdapter;

    // online recyclerview
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private JobPostsRVAdapter adapter;
    private boolean isLoadMore = false;
    private String key = null;
    private CRUDManageJobPosts crudJobPosts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog dialog;
    private ImageButton backBtn;

    @Override
    public void onStart() {
        super.onStart();

        dialog.setTitle("Getting the latest jobs for you...");
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        is_empty_list = findViewById(R.id.is_empty_list);
        swipeRefreshLayout = findViewById(R.id.jp_swipe_refresh_layout);
        recyclerView = findViewById(R.id.jp_rv);
        backBtn = findViewById(R.id.back_btn);
        is_empty_list.setVisibility(View.INVISIBLE);

        if (isConnectedToInternet()) {
            // ONLINE
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            databaseReference = db.getReference("cebuapp_job_posts");
            crudJobPosts = new CRUDManageJobPosts();

            // set categories spinner
            mainOnlineCategorySpinner();

            // showing the recycler view
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(manager);
            adapter = new JobPostsRVAdapter(this);
            recyclerView.setAdapter(adapter);

        } else {
            Toast.makeText(this, "Please connect to the Internet.", Toast.LENGTH_SHORT).show();
        }

        // back to home btn
        backBtn.setOnClickListener(v-> {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });
    }

    private void mainOnlineCategorySpinner() {
        jobsList = findViewById(R.id.jobslist);
        jobsSpinner = findViewById(R.id.jobs_spinner);
        jobsSpinnerRef = FirebaseDatabase.getInstance().getReference("cebuapp_job_fields");
        spinnerList = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<String>(JobsActivity.this, android.R.layout.simple_spinner_dropdown_item,
                spinnerList);
        jobsSpinner.setAdapter(spinnerAdapter);

        // supply data to spinner
        spinnerList.add("ALL");
        jobsSpinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot uniqueKey: snapshot.getChildren()) {
                        for(DataSnapshot data : uniqueKey.getChildren()){
                            spinnerList.add(data.getValue().toString());
                        }
                    }
                    spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // spinner listener for categories
        jobsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get selected position of spinner
                String selectedCategory = jobsSpinner.getSelectedItem().toString();
                dialog.setTitle("Fetching " + selectedCategory + " jobs in cebu.");
                dialog.show();
                getSelectedCategoryDatas(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

    private void getSelectedCategoryDatas(String selectedCategory) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnectedToInternet()) {
                    // ONLINE
                    if (selectedCategory.equals("ALL")) {
                        getAllOnlineJobs();
                    } else {
                        getAllOnlineJobsByCat(selectedCategory);

                    }
                } else {
                    Toast.makeText(JobsActivity.this, "Please connect to the Internet.", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000);
    }

    private void getAllOnlineJobsByCat(String selectedCategory) {
        crudJobPosts.getByCategory(selectedCategory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<JobPosts> jobFieldsList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    JobPosts manageJobFields = data.getValue(JobPosts.class);

                    // set data
                    manageJobFields.setKey(data.getKey());

                    // append to list
                    jobFieldsList.add(manageJobFields);
                    key = data.getKey();
                }

                if (jobFieldsList.isEmpty() == true) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(jobFieldsList);
                    adapter.notifyDataSetChanged();
                    isLoadMore = false;
                    swipeRefreshLayout.setRefreshing(false);
                    is_empty_list.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getAllOnlineJobs() {
        crudJobPosts.getAllApproved(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<JobPosts> jobFieldsList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    JobPosts manageJobFields = data.getValue(JobPosts.class);

                    // set data
                    manageJobFields.setKey(data.getKey());

                    // append to list
                    jobFieldsList.add(manageJobFields);
                    key = data.getKey();
                }

                if (jobFieldsList.isEmpty() == true) {
                    swipeRefreshLayout.setVisibility(View.GONE);
                    is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    adapter.setItems(jobFieldsList);
                    adapter.notifyDataSetChanged();
                    isLoadMore = false;
                    swipeRefreshLayout.setRefreshing(false);
                    is_empty_list.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
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