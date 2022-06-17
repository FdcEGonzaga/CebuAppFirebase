package com.example.cebuapp.model;

public class User {
    public String fullname, email, phone;
    public Integer role;

    public User() {}

    public User(String fullname, String email, String phone, Integer role) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
