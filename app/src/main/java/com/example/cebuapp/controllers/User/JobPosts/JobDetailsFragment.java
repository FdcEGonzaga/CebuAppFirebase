package com.example.cebuapp.controllers.User.JobPosts;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.HomeActivity;

public class JobDetailsFragment extends Fragment {
    private View view;
    private ImageButton fragmentClose;
    private Button emailBtn, applyJobBtn;
    private ImageView jobDetailImg;
    private TextView jobDetailTitle, jobDetailCompany, jobDetailYears, jobDetailField, jobDetailDate,
            jobDetailSalary, jobDetailDesc, jobDetailDesc2, jobUrlAuthor;
    private String sjobDetailImg, sjobDetailCompany, sjobDetailTitle, sjobDetailYears, sjobDetailSalary, sjobDetailDesc,
            sjobDetailDesc2, sjobDetailField, sjobDetailDate, sjobUrlString, sjobUrlAuthor;
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_job_details, container, false);
        castComponents();
        getValues();
        setValues();
        setListeners();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    private void castComponents() {
        fragmentClose = view.findViewById(R.id.fragment_close);
        jobDetailImg = view.findViewById(R.id.jobDetailImg);
        jobDetailTitle = view.findViewById(R.id.jobDetailTitle);
        jobDetailYears = view.findViewById(R.id.jobDetailYears);
        jobDetailDesc = view.findViewById(R.id.jobDetailDesc);
        jobDetailDesc2 = view.findViewById(R.id.jobDetailDesc2);
        jobDetailSalary = view.findViewById(R.id.jobDetailSalary);
        jobDetailField = view.findViewById(R.id.jobDetailField);
        jobDetailCompany = view.findViewById(R.id.jobDetailCompany);
        jobDetailDate = view.findViewById(R.id.jobDetailDate);
        applyJobBtn = view.findViewById(R.id.applyJobBtn);
        emailBtn = view.findViewById(R.id.emailBtn);
    }

    private void getValues() {
        sjobDetailImg = getArguments().getString("jobDetailImg");
        sjobDetailCompany = getArguments().getString("jobDetailCompany");
        sjobDetailTitle = getArguments().getString("jobDetailTitle");
        sjobDetailYears = getArguments().getString("jobDetailYears");
        sjobDetailSalary = getArguments().getString("jobDetailSalary");
        sjobDetailDesc = getArguments().getString("jobDetailDesc");
        sjobDetailDesc2 = getArguments().getString("jobDetailDesc2");
        sjobDetailField = getArguments().getString("jobDetailField");
        sjobDetailDate = getArguments().getString("jobDetailDate");
        sjobUrlString = getArguments().getString("jobUrlString");
        sjobUrlAuthor = getArguments().getString("jobUrlAuthor");
    }

    private void setValues() {
        new ShowImageUrl((ImageView) view.findViewById(R.id.jobDetailImg))
                .execute(sjobDetailImg);
        jobDetailCompany.setText(sjobDetailCompany);
        jobDetailTitle.setText(sjobDetailTitle);
        jobDetailYears.setText(sjobDetailYears);
        jobDetailSalary.setText(sjobDetailSalary);
        jobDetailDesc.setText(sjobDetailDesc);
        jobDetailDesc2.setText(sjobDetailDesc2);
        jobDetailField.setText(sjobDetailField);
        jobDetailDate.setText(sjobDetailDate);
    }

    private void setListeners() {
        fragmentClose.setOnClickListener(v-> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });

        // email job post
        emailBtn.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {sjobUrlAuthor});
            intent.putExtra(Intent.EXTRA_CC, new String[] {"fdc.egonzaga@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "CEBU APP - " + sjobDetailTitle.trim());
            intent.putExtra(Intent.EXTRA_TEXT, "Hi, " + sjobDetailCompany + "! \n\n " +
                    "This letter is in reponse to your open position ''" + sjobDetailTitle.trim() + "'' which has been posted on CEBU APP.\n\n" +
                    "I would like to ...");
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        // apply job btn
        applyJobBtn.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sjobUrlString));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            try {
                getActivity().startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // Chrome browser presumably not installed so allow user to choose instead
                intent.setPackage(null);
                getActivity().startActivity(intent);
            }
        });
    }
}