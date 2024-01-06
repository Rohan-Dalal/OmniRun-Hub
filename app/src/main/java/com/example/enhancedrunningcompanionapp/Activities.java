package com.example.enhancedrunningcompanionapp;

import java.util.ArrayList;

/*This class stores all the relevant information for a running activity*/
public class Activities {
    private String time, pace, mileage, beginningTime, date;
    private ArrayList<LatLngWrapper> latLngArrayList;

    public Activities(String time, String pace, String mileage, String beginningTime, String date, ArrayList<LatLngWrapper> latLngArrayList){
        this.time = time;
        this.pace = pace;
        this.mileage = mileage;
        this.beginningTime = beginningTime;
        this.date = date;
        this.latLngArrayList = latLngArrayList;
    }

    public Activities(){

    }

    public String getTime() {
        return time;
    }

    public String getPace() {
        return pace;
    }

    public String getMileage() {
        return mileage;
    }
    public String getBeginningTime() {
        return beginningTime;
    }
    public String getDate (){
        return date;
    }

    public ArrayList<LatLngWrapper> getLatLngArrayList() {
        return latLngArrayList;
    }
}