package com.example.cebuapp.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class JobPosts implements Serializable {
    @Exclude
    private String key;
    private String jobPostTitle;
    private String jobPostImg;
    private String jobPostDescription;
    private String jobPostPosted;
    private String jobPostYearExp;
    private String jobPostSalary;
    private String jobPostCompany;
    private String jobPostCompanyDetails;
    private String jobPostJobField;
    private String jobPostProvince;
    private String jobPostLink;

    public JobPosts() {}

    public JobPosts(String jobPostTitle, String jobPostDescription, String jobPostPosted, String jobPostYearExp,
                    String jobPostSalary, String jobPostCompany, String jobPostCompanyDetails, String jobPostJobField,
                    String jobPostProvince, String jobPostLink) {
        this.jobPostTitle = jobPostTitle;
        this.jobPostDescription = jobPostDescription;
        this.jobPostPosted = jobPostPosted;
        this.jobPostYearExp = jobPostYearExp;
        this.jobPostSalary = jobPostSalary;
        this.jobPostCompany = jobPostCompany;
        this.jobPostCompanyDetails = jobPostCompanyDetails;
        this.jobPostJobField = jobPostJobField;
        this.jobPostProvince = jobPostProvince;
        this.jobPostLink = jobPostLink;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJobPostTitle() {
        return jobPostTitle;
    }

    public void setJobPostTitle(String jobPostTitle) {
        this.jobPostTitle = jobPostTitle;
    }

    public String getJobPostImg() {
        return jobPostImg;
    }

    public void setJobPostImg(String jobPostImg) {
        this.jobPostImg = jobPostImg;
    }

    public String getJobPostDescription() {
        return jobPostDescription;
    }

    public void setJobPostDescription(String jobPostDescription) {
        this.jobPostDescription = jobPostDescription;
    }

    public String getJobPostPosted() {
        return jobPostPosted;
    }

    public void setJobPostPosted(String jobPostPosted) {
        this.jobPostPosted = jobPostPosted;
    }

    public String getJobPostYearExp() {
        return jobPostYearExp;
    }

    public void setJobPostYearExp(String jobPostYearExp) {
        this.jobPostYearExp = jobPostYearExp;
    }

    public String getJobPostSalary() {
        return jobPostSalary;
    }

    public void setJobPostSalary(String jobPostSalary) {
        this.jobPostSalary = jobPostSalary;
    }

    public String getJobPostCompany() {
        return jobPostCompany;
    }

    public void setJobPostCompany(String jobPostCompany) {
        this.jobPostCompany = jobPostCompany;
    }

    public String getJobPostCompanyDetails() {
        return jobPostCompanyDetails;
    }

    public void setJobPostCompanyDetails(String jobPostCompanyDetails) {
        this.jobPostCompanyDetails = jobPostCompanyDetails;
    }

    public String getJobPostJobField() {
        return jobPostJobField;
    }

    public void setJobPostJobField(String jobPostJobField) {
        this.jobPostJobField = jobPostJobField;
    }

    public String getJobPostProvince() {
        return jobPostProvince;
    }

    public void setJobPostProvince(String jobPostProvince) {
        this.jobPostProvince = jobPostProvince;
    }

    public String getJobPostLink() {
        return jobPostLink;
    }

    public void setJobPostLink(String jobPostLink) {
        this.jobPostLink = jobPostLink;
    }
}
