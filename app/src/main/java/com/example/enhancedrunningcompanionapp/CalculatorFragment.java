package com.example.enhancedrunningcompanionapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CalculatorFragment extends Fragment {

    Button raceCalculator;
    Button paceCalculator;
    TextView mainActivityText;
    TextView selectedCalculator;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    PaceCalculatorFragment paceCalculatorFragment;
    RaceCalculatorFragment raceCalculatorFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_calculator, null);
        mainActivityText = getActivity().findViewById(R.id.textview_main);
        selectedCalculator = fragmentView.findViewById(R.id.selected_calculator);
        raceCalculator = fragmentView.findViewById(R.id.racecalculator);
        paceCalculator = fragmentView.findViewById(R.id.pacecalculator);

        selectedCalculator.setText("Pace Calculator");
        fragmentManager = getChildFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if(paceCalculatorFragment == null)
            paceCalculatorFragment = new PaceCalculatorFragment();

        if(raceCalculatorFragment == null)
            raceCalculatorFragment = new RaceCalculatorFragment();

        fragmentTransaction.replace(R.id.calculatorfragment, paceCalculatorFragment);
        fragmentTransaction.commit();

        paceCalculator.setOnClickListener(view -> {
            selectedCalculator.setText("Pace Calculator");

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.calculatorfragment, paceCalculatorFragment);
            fragmentTransaction.commit();
        });

        raceCalculator.setOnClickListener(view -> {
            selectedCalculator.setText("Race Calculator");

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.calculatorfragment, raceCalculatorFragment);
            fragmentTransaction.commit();
        });
        return fragmentView;
    }
}