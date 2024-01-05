package com.example.enhancedrunningcompanionapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class GPSFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    TextView mainActivityText, time_textview, pace_textview, gps_distance;
    Button start_stop_button, reset_button;
    LocationManager locationManager;
    Geocoder geocoder;
    double totalDistance, time;
    GoogleMap map;
    Location location1;
    Polyline line;
    boolean startorstop = false;
    Timer timer;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("activities");
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    ArrayList<Marker> markerArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_gps, null);
        mainActivityText = getActivity().findViewById(R.id.textview_main);
        time_textview = fragmentView.findViewById(R.id.gpstime);
        pace_textview = fragmentView.findViewById(R.id.gpspace);
        start_stop_button = fragmentView.findViewById(R.id.startstop);
        gps_distance = fragmentView.findViewById(R.id.gpsdistance);
        reset_button = fragmentView.findViewById(R.id.resetbutton);

        if(!startorstop)
            start_stop_button.setText("Start");
        else
            start_stop_button.setText("Stop");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(getActivity(), Locale.US);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 8, this);
            location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }


        start_stop_button.setOnClickListener(view -> {
            if (!startorstop){
                startorstop = true;
                start_stop_button.setText("Stop");
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location1.getLatitude(), location1.getLongitude()));
                markerOptions.visible(false);
                Marker marker = map.addMarker(markerOptions);
                latLngArrayList.add((new LatLng(location1.getLatitude(), location1.getLongitude())));
                markerArrayList.add(marker);
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        time += 1;
                        time_textview.setText(String.format("%02d", (int)Math.floor(time/3600.0)) + ":" + String.format("%02d",(int)Math.floor((time%3600)/60.0)) + ":" + String.format("%02d",(int)(time%60)));
                        if(time % 5 == 0 && totalDistance !=0)
                            pace_textview.setText(String.format("%02d",(int)(time/(Math.round(totalDistance / 16.09) / 100.0)/3600)) + ":" + String.format("%02d",(int)(((time/(Math.round(totalDistance / 16.09) / 100.0))%3600)/60)) + ":" + String.format("%02d",Math.round(time/(Math.round(totalDistance / 16.09) / 100.0))%60));
                    }

                };
                timer.schedule(timerTask, 0, 1000);
            }
            else{
                endRun();
            }

        });

        reset_button.setOnClickListener(view -> {
            if(totalDistance!=0 || time!=0) {
                endRun();

                myRef.push().setValue(new Activities(time_textview.getText().toString(), pace_textview.getText().toString(), gps_distance.getText().toString()));
                map.clear();
                markerArrayList.clear();
                latLngArrayList.clear();
                totalDistance = 0;
                time = 0;
                gps_distance.setText("0");
                pace_textview.setText("00:00:00");
                time_textview.setText("00:00:00");
            }
        });
        return fragmentView;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (SystemClock.elapsedRealtime() > 7000) {
            if(startorstop) {
                totalDistance += location.distanceTo(location1);
                gps_distance.setText(Double.toString(Math.round(totalDistance / 16.09) / 100.0));
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
                markerOptions.visible(false);
                Marker marker = map.addMarker(markerOptions);
                latLngArrayList.add((new LatLng(location.getLatitude(), location.getLongitude())));
                markerArrayList.add(marker);

                PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngArrayList).clickable(false);
                line = map.addPolyline(polylineOptions);
                line.setColor(Color.BLUE);
            }

            if(map!=null) {
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            }

            location1 = location;

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if(location1!=null) {
            map = googleMap;
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location1.getLatitude(), location1.getLongitude()), 16.0f));
        }
    }

    public void endRun(){
        if(totalDistance!=0)
            pace_textview.setText(String.format("%02d",(int)(time/(Math.round(totalDistance / 16.09) / 100.0)/3600)) + ":" + String.format("%02d",(int)(((time/(Math.round(totalDistance / 16.09) / 100.0))%3600)/60)) + ":" + String.format("%02d",Math.round(time/(Math.round(totalDistance / 16.09) / 100.0))%60));
        startorstop = false;
        start_stop_button.setText("Start");
        timer.cancel();
        timer.purge();
    }
}