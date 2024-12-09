package com.example.mrrover.model;

import android.media.Image;

public class DriverModel {

   // private String ImageLocation;
    private String uid;
    private String FullName;
    private  String Gender;
    private Double rating;
    private Double distance;


    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public DriverModel (String uid, String FullName, String  gender, Double rating, Double distance){
        this.uid = uid;
        this.FullName = FullName;
        this.Gender =  gender;
        this.rating = rating;
        this.distance = distance;
       // this.ImageLocation = ImageLocation;
    }

    public String getUid(){
        return  uid;
    }

    public String  getFullName(){
        return FullName;
    }

    public String getGender(){
        return Gender;
    }

    public Double getRating(){
        return rating;
    }
/*
    public String getImageLocation() {
        return ImageLocation;
    }

 */
}
