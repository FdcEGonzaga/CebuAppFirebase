package com.example.cebuapp.controllers.Admin.ManageJobPosts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.R;

public class ManageJobPostFragment extends Fragment {
    private View view;
    private ImageButton fragmentClose;
    private ImageView jobDetailImg;
    private TextView jobDetailApproved, jobDetailTitle, jobDetailCompany, jobDetailYears, jobDetailField, jobDetailDate,
            jobDetailSalary, jobDetailDesc, jobDetailDesc2, jobUrlString;
    private String sjobDetailApproved, sjobDetailImg, sjobDetailCompany, sjobDetailTitle, sjobDetailYears, sjobDetailSalary, sjobDetailDesc,
            sjobDetailDesc2, sjobDetailField, sjobDetailDate, sjobUrlString;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manage_job_post, container, false);
        castComponents();
        getValues();
        setValues();
        setListeners();
        return view;
    }

    private void castComponents() {
        fragmentClose = view.findViewById(R.id.fragment_close);
        jobDetailApproved = view.findViewById(R.id.jobDetailApproved);
        jobDetailImg = view.findViewById(R.id.jobDetailImg);
        jobDetailTitle = view.findViewById(R.id.jobDetailTitle);
        jobDetailYears = view.findViewById(R.id.jobDetailYears);
        jobDetailDesc = view.findViewById(R.id.jobDetailDesc);
        jobDetailDesc2 = view.findViewById(R.id.jobDetailDesc2);
        jobDetailSalary = view.findViewById(R.id.jobDetailSalary);
        jobDetailField = view.findViewById(R.id.jobDetailField);
        jobDetailCompany = view.findViewById(R.id.jobDetailCompany);
        jobDetailDate = view.findViewById(R.id.jobDetailDate);
        jobUrlString = view.findViewById(R.id.jobUrlString);
    }

    private void getValues() {
        sjobDetailImg = getArguments().getString("jobDetailImg");
        sjobDetailApproved = getArguments().getString("jobDetailApproved");
        sjobDetailCompany = getArguments().getString("jobDetailCompany");
        sjobDetailTitle = getArguments().getString("jobDetailTitle");
        sjobDetailYears = getArguments().getString("jobDetailYears");
        sjobDetailSalary = getArguments().getString("jobDetailSalary");
        sjobDetailDesc = getArguments().getString("jobDetailDesc");
        sjobDetailDesc2 = getArguments().getString("jobDetailDesc2");
        sjobDetailField = getArguments().getString("jobDetailField");
        sjobDetailDate = getArguments().getString("jobDetailDate");
        sjobUrlString = getArguments().getString("jobUrlString");
    }

    private void setValues() {
        new ShowImageUrl((ImageView) view.findViewById(R.id.jobDetailImg))
                .execute(sjobDetailImg);
        jobDetailApproved.setText(sjobDetailApproved);
        jobDetailCompany.setText(sjobDetailCompany);
        jobDetailTitle.setText(sjobDetailTitle);
        jobDetailYears.setText(sjobDetailYears);
        jobDetailSalary.setText(sjobDetailSalary);
        jobDetailDesc.setText(sjobDetailDesc);
        jobDetailDesc2.setText(sjobDetailDesc2);
        jobDetailField.setText(sjobDetailField);
        jobDetailDate.setText(sjobDetailDate);
        jobUrlString.setText(sjobUrlString);
    }

    private void setListeners() {
        fragmentClose.setOnClickListener(v-> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
    }
}