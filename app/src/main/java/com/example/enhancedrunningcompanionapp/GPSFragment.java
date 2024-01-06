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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
// Fragment used to begin a user's run
public class GPSFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    TextView mainActivityText, time_textview, pace_textview, gps_distance;
    Button start_stop_button, reset_button;
    LocationManager locationManager;
    Geocoder geocoder;
    double totalDistance, time;
    GoogleMap map;
    Location initialLocation;
    Polyline line;
    boolean startorstop;
    Timer timer;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ArrayList<LatLng> latLngArrayList;
    String formattedTime, formattedDate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_gps, null);

        // Initialize UI Components
        mainActivityText = getActivity().findViewById(R.id.textview_main);
        time_textview = fragmentView.findViewById(R.id.gpstime);
        pace_textview = fragmentView.findViewById(R.id.gpspace);
        start_stop_button = fragmentView.findViewById(R.id.startstop);
        gps_distance = fragmentView.findViewById(R.id.gpsdistance);
        reset_button = fragmentView.findViewById(R.id.resetbutton);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("activities");
        latLngArrayList = new ArrayList<>();
        startorstop = false;
        start_stop_button.setText("Start");

        // Obtain and asynchronously load the Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        // Initialize location services and geocoder for the fragment's activity.
        // Check and request location permissions. If granted, request location updates and retrieve the initial location.
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(getActivity(), Locale.US);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 8, this);
            initialLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        // Either starts or stops the run
        start_stop_button.setOnClickListener(view -> {
            if (!startorstop){
                Calendar calendar = Calendar.getInstance();
                Date beginningTime = calendar.getTime();

                // Retrieves the date the run was started
                DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                formattedTime = timeFormat.format(beginningTime);

                // Retrieves the time the run was started
                DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                formattedDate = dateFormat.format(beginningTime);

                startorstop = true;
                start_stop_button.setText("Stop");

                // Add latitude and longitude of beginning location to list of LatLng
                latLngArrayList.add((new LatLng(initialLocation.getLatitude(), initialLocation.getLongitude())));

                // Begins the stopwatch and periodically updates the user's pace
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
                pauseRun();
            }
        });

        // If the user ends the run, it will be saved to their log
        reset_button.setOnClickListener(view -> {
            if(totalDistance!=0 || time!=0) {
                pauseRun();

                // The full run information is pushed to the user's Firebase log
                myRef.push().setValue(new Activities(time_textview.getText().toString(), pace_textview.getText().toString(), gps_distance.getText().toString(), formattedTime, formattedDate, LatLngWrapper.convertToLatLngWrapperArrayList(latLngArrayList)));

                // UI is reset to original form
                map.clear();
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
                // Distance is updated
                totalDistance += location.distanceTo(initialLocation);
                gps_distance.setText(Double.toString(Math.round(totalDistance / 16.09) / 100.0));

                latLngArrayList.add((new LatLng(location.getLatitude(), location.getLongitude())));

                // A line is added between the user's previous location and the current one
                PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngArrayList).clickable(false);
                line = map.addPolyline(polylineOptions);
                line.setColor(Color.BLUE);
            }

            initialLocation = location;
        }

        // If the user moves the map automatically follows
        if(map!=null) {
            map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initialLocation.getLatitude(), initialLocation.getLongitude()), 16.0f));
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Map is initialized
        map = googleMap;
        map.setMyLocationEnabled(true);
    }

    // This method pauses the timer and distance
    public void pauseRun(){
        if(totalDistance!=0) {
            pace_textview.setText(String.format("%02d", (int) (time / (Math.round(totalDistance / 16.09) / 100.0) / 3600)) + ":" + String.format("%02d", (int) (((time / (Math.round(totalDistance / 16.09) / 100.0)) % 3600) / 60)) + ":" + String.format("%02d", Math.round(time / (Math.round(totalDistance / 16.09) / 100.0)) % 60));
        }
        startorstop = false;
        start_stop_button.setText("Start");
        timer.cancel();
        timer.purge();
    }
}