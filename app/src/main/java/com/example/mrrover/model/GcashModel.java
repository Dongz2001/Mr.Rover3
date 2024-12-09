package com.example.mrrover.model;

public class GcashModel {

    private String gcashname;
    private String gcashnumber;

    public GcashModel(String gcashname, String gcashnumber) {
        this.gcashname = gcashname;
        this.gcashnumber = gcashnumber;
    }

    public String getGcashname(){

        return  gcashname;
    }

    public String getGcashnumber(){

        return  gcashnumber;
    }



}
