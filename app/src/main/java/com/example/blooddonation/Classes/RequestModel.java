package com.example.blooddonation.Classes;

public class RequestModel {
    public String name,phone,bloodType,units_no,hospitalName,message;

    public RequestModel() {
    }

    public RequestModel(String name,String hospitalName,String bloodType,String message,String units_no){
        this.name=name;
        this.hospitalName=hospitalName;
        this.bloodType=bloodType;
        this.message=message;
        this.units_no=units_no;
    }


}
