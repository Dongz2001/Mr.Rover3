package com.example.mrrover.model;

public class UserBookingModel {

    private String bookingId;
    private String userUid;
    private String driverName;
    private  String service;
    private  String vehicle;
    private  String date;
    private  String time;
    private  String status;
    private  String vehicleUserID;

    public UserBookingModel(String bookngId, String userUid, String driverName, String service, String vehicle, String date, String time, String status, String vehicleUserID) {
        this.userUid = userUid;
        this.driverName = driverName;
        this.service = service;
        this.vehicle = vehicle;
        this.date = date;
        this.time = time;
        this.status = status;
        this.bookingId = bookngId;
        this.vehicleUserID = vehicleUserID;
    }

    public String getDriverName(){
        return  driverName;
    }
    public String getService(){
        return  service;
    }
    public String getVehicle(){
        return  vehicle;
    }
    public String getDdate(){
        return  date;
    }
    public String getTtime(){
        return  time;
    }
    public String getUstatus(){
        return  status;
    }
    public String getBookingId(){
        return  bookingId;
    }
    public String getUserUid(){
        return  userUid;
    }
    public String getVehicleUserID(){
        return  vehicleUserID;
    }

}
