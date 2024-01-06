package com.example.enhancedrunningcompanionapp;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

// This class creates a wrapper for the Google LatLng class
public class LatLngWrapper {
    private double latitude;
    private double longitude;
    public LatLngWrapper(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }
    public LatLngWrapper () {

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng convertToLatLng() {
        return new LatLng(latitude, longitude);
    }

    // Converts a LatLng ArrayList to a LatLngWrapper ArrayList
    public static ArrayList<LatLng> convertToLatLngArrayList(ArrayList<LatLngWrapper> latLngWrapper) {
        ArrayList<LatLng> list = new ArrayList<>();
        for(LatLngWrapper point : latLngWrapper) {
            list.add(new LatLng(point.latitude, point.longitude));
        }
        return list;
    }

    // Converts a LatLngWrapper ArrayList to a LatLng ArrayList
    public static ArrayList<LatLngWrapper> convertToLatLngWrapperArrayList(ArrayList<LatLng> latLng) {
        ArrayList<LatLngWrapper> list = new ArrayList<>();
        for(LatLng point : latLng) {
            list.add(new LatLngWrapper(point));
        }
        return list;
    }
}
