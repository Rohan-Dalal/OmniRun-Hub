package com.example.enhancedrunningcompanionapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CalculatorFragment extends Fragment {

    Button raceCalculator, paceCalculator;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    PaceCalculatorFragment paceCalculatorFragment;
    RaceCalculatorFragment raceCalculatorFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_calculator, null);

        // Initialize UI Components
        raceCalculator = fragmentView.findViewById(R.id.racecalculator);
        paceCalculator = fragmentView.findViewById(R.id.pacecalculator);


        fragmentManager = getChildFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        // Initializes fragments for each calculator if not already created
        if(paceCalculatorFragment == null) {
            paceCalculatorFragment = new PaceCalculatorFragment();
        }
        if(raceCalculatorFragment == null) {
            raceCalculatorFragment = new RaceCalculatorFragment();
        }

        fragmentTransaction.replace(R.id.calculatorfragment, paceCalculatorFragment);
        fragmentTransaction.commit();

        // Switches to the pace calculator if button is clicked
        paceCalculator.setOnClickListener(view -> {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.calculatorfragment, paceCalculatorFragment);
            fragmentTransaction.commit();
        });

        // Switches to the race calculator if button is clicked
        raceCalculator.setOnClickListener(view -> {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.calculatorfragment, raceCalculatorFragment);
            fragmentTransaction.commit();
        });
        return fragmentView;
    }
}