package com.example.mrrover.model;

public class CustomerIDModel {

    private String userUid;
    private String driverUid;
    private String ownerName;
    private  String date;
    private  String time;

    public CustomerIDModel(String ownerName, String date, String time) {
        this.ownerName = ownerName;
        this.date = date;
        this.time = time;
    }

    public String getOwnerName(){
        return  ownerName;
    }
    public String getDdate(){
        return  date;
    }
    public String getTtime(){
        return  time;
    }


}
