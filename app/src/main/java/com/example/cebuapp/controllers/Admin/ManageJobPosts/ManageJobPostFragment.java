package com.example.cebuapp.controllers.Admin.ManageJobPosts;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cebuapp.Helper.ShowImageUrl;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.LoginActivity;

public class ManageJobPostFragment extends Fragment {
    private View view;
    private ImageButton fragmentClose;
    private ImageView jobDetailImg;
    private TextView jobDetailApproved, jobDetailTitle, jobDetailCompany, jobDetailYears, jobDetailField, jobDetailDate,
            jobDetailSalary, jobDetailDesc, jobDetailDesc2, jobUrlString, jobDetailAuthor;
    private String sjobDetailApproved, sjobDetailImg, sjobDetailCompany, sjobDetailTitle, sjobDetailYears, sjobDetailSalary, sjobDetailDesc,
            sjobDetailDesc2, sjobDetailField, sjobDetailDate, sjobUrlString, sjobDetailAuthor;
    private Button detailEmailAuthor;
    private ImageButton openLinkBtn;
    private Intent intent;


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
        jobDetailAuthor = view.findViewById(R.id.jobDetailAuthor);
        detailEmailAuthor = view.findViewById(R.id.detailEmailAuthor);
        openLinkBtn = view.findViewById(R.id.openLinkBtn);
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
        sjobDetailAuthor = getArguments().getString("jobDetailAuthor");
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
        if (sjobDetailAuthor.equals("admin@gmail.com")) {
            // if author is admin, hide email billing
            sjobDetailAuthor = "CEBU-APP";
            detailEmailAuthor.setVisibility(View.GONE);
        } else {
            // if author is user ang already published, hide email billing
            if (sjobDetailApproved.equals("Published")) {
                detailEmailAuthor.setVisibility(View.GONE);
            }
        }
        jobDetailAuthor.setText(sjobDetailAuthor);
    }

    private void setListeners() {
        fragmentClose.setOnClickListener(v-> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });

        // email job post author
        detailEmailAuthor.setOnClickListener(v -> {
            if (!sjobDetailAuthor.equals("admin@gmail.com")) {
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {sjobDetailAuthor});
                intent.putExtra(Intent.EXTRA_CC, new String[] {"fdc.egonzaga@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "CEBU APP - ADMIN");
                intent.putExtra(Intent.EXTRA_TEXT, "Hi, " + sjobDetailAuthor + "! \n\n " +
                        "Thanks for posting your job vacancy on CEBU APP. We would like to inform you that in order for your job to get published, you will need to pay " +
                        "P200.00 for a 3-days posting via Card or Gcash. In addition, you can definitely extend the posting for another 3 days for only P100.00.\n\n" +
                        "Please reach us if you have questions or concerns  via phone 09322702597 or email us at fdc.egonzaga@gmail.com.\n\n\n" +
                        "All the best,\nCEBU APP - ADMIN");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        openLinkBtn.setOnClickListener(v-> {
            new AlertDialog.Builder(getActivity())
                .setTitle("CebuApp")
                .setMessage("Do you want to open the link on a browser?")
                .setCancelable(false)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                    }
                })
                .create().show();

        });
    }
}