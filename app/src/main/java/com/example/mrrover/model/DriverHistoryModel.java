package com.example.mrrover.model;

public class DriverHistoryModel {

    private String driver;
    private String loc;
    private String desti;
    private String gender;
    private String model;
    private String type;
    private String driverName;
    private  String service;
    private  String vehicle;
    private  String date;
    private  String time;
    private  String status;
    private String bookingId;
    private String vehicleowner;
    private String driverUID;
    private Double star;
    private String comments;

    public DriverHistoryModel(String driver, String loc,String desti,String gender, String model, String type, String driverName, String service, String vehicle, String date, String time, String status, String bookingId, String vehicleowner,String driverUID, Double star, String comments) {
        this.driver = driver;
        this.loc = loc;
        this.desti = desti;
        this.gender = gender;
        this.model = model;
        this.type = type;
        this.driverName = driverName;
        this.service = service;
        this.vehicle = vehicle;
        this.date = date;
        this.time = time;
        this.status = status;
        this.bookingId = bookingId;
        this.vehicleowner = vehicleowner;
        this.driverUID = driverUID;
        this.star = star;
        this.comments = comments;
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
    public void setStatus(String status) {
        this.status = status;
    }
    public String getBookingId(){
        return  bookingId;
    }
    public String getVehicleowner(){
        return  vehicleowner;
    }
    public String getdriverUID(){
        return  driverUID;
    }
    public String getdriverNAME(){
        return  driver;
    }
    public String getdesti(){
        return  desti;
    }
    public String getloc(){
        return  loc;
    }
    public String getgender(){
        return  gender;
    }
    public String getmodel(){
        return  model;
    }
    public String gettype(){
        return  type;
    }
    public Double getStar(){
        return  star;
    }
    public String getComments(){
        return  comments;
    }



}
