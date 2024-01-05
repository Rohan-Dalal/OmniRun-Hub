package com.example.enhancedrunningcompanionapp;

public class Activities {
    private String time, pace, mileage;

    public Activities(String time, String pace, String mileage){
        this.time = time;
        this.pace = pace;
        this.mileage = mileage;
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
}