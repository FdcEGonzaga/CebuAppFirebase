package com.example.cebuapp.controllers.User.JobPosts;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.Admin.ManageJobPosts.CRUDManageJobPosts;
import com.example.cebuapp.model.JobPosts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JobPostsOwnedFragment extends Fragment {

    private FirebaseUser firebaseUser;
    private View view;
    private LinearLayout rvContainer;
    private RecyclerView recyclerView;
    private ImageButton fragmentClose;;
    private DatabaseReference databaseReference;

    private TextView is_empty_list;
    // online recyclerview
    private JobPostsOwnedRVAdapter adapter;
    private String key = null;
    private CRUDManageJobPosts crudJobPosts;

    // sort manager
    private LinearLayoutManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_job_posts_owned, container, false);
        castComponents();
        setListeners();

        // ONLINE
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("cebuapp_job_posts");
        crudJobPosts = new CRUDManageJobPosts();

        // showing the recycler view
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapter = new JobPostsOwnedRVAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        getOwnedJobPosts();
        return view;
    }


    private void getOwnedJobPosts() {
        crudJobPosts.getOwnedJobPosts(firebaseUser.getEmail().trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<JobPosts> jobPostList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    JobPosts jobPulledData = data.getValue(JobPosts.class);

                    // set data
                    jobPulledData.setKey(data.getKey());

                    // append to list
                    jobPostList.add(jobPulledData);
                    key = data.getKey();
                }

                if (jobPostList.isEmpty() == true) {
                    is_empty_list.setVisibility(View.VISIBLE);
                } else {
                    is_empty_list.setVisibility(View.GONE);
                    adapter.setItems(jobPostList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void castComponents() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fragmentClose = view.findViewById(R.id.fragment_close);
        rvContainer = view.findViewById(R.id.rv_container);
        recyclerView = view.findViewById(R.id.jp_rv);
        is_empty_list = view.findViewById(R.id.is_empty_list);
        manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
    }
    private void setListeners() {
        fragmentClose.setOnClickListener(v-> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }
}