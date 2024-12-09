package com.example.mrrover.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String firstname;
    private String lastname;
    private Timestamp createdTimestamp;

    public UserModel() {
    }

    public UserModel(String phone, String firstname, String lastname, Timestamp createdTimestamp) {
        this.phone = phone;
        this.firstname = firstname;
        this.lastname = lastname;
        this.createdTimestamp = createdTimestamp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
