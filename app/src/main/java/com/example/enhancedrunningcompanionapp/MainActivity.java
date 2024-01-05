package com.example.enhancedrunningcompanionapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView textViewHeader;
    BottomNavigationView bottomNavigationView;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        textViewHeader = findViewById(R.id.textview_main);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        CalculatorFragment calculatorFragment = new CalculatorFragment();
        ActivityFragment activityFragment = new ActivityFragment();
        GPSFragment gpsFragment = new GPSFragment();
        AccountInfoFragment accountInfoFragment = new AccountInfoFragment();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            bottomNavigationView.setSelectedItemId(R.id.calculator);
            fragmentTransaction.replace(R.id.selected_fragment, calculatorFragment);
        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bottomNavigationView.setSelectedItemId(R.id.gps);
            fragmentTransaction.replace(R.id.selected_fragment, gpsFragment);
        }
        fragmentTransaction.commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            fragmentTransaction = fragmentManager.beginTransaction();
            if(item.getItemId() == R.id.calculator) {
                textViewHeader.setText("Calculator");
                fragmentTransaction.replace(R.id.selected_fragment, calculatorFragment);
            } else if(item.getItemId() == R.id.gps) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    textViewHeader.setText("Run");
                    fragmentTransaction.replace(R.id.selected_fragment, gpsFragment);
                }
            } else if(item.getItemId() == R.id.activity) {
                textViewHeader.setText("Activity");
                fragmentTransaction.replace(R.id.selected_fragment, activityFragment);
            } else if(item.getItemId() == R.id.userinfo) {
                textViewHeader.setText("Account Information");
                fragmentTransaction.replace(R.id.selected_fragment, accountInfoFragment);
            }
            fragmentTransaction.commit();
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}