package com.example.enhancedrunningcompanionapp;

// This class stores all the relevant information for a race split
public class RaceSplits {
    private String time;
    private int distance;
    private double lap;

    public RaceSplits(double lap, String time, int distance){
        this.lap = lap;
        this.time = time;
        this.distance = distance;
    }

    public double getLap() {
        return lap;
    }

    public String getTime() {
        return time;
    }

    public int getDistance() {
        return distance;
    }
}