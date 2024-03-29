package com.example.enhancedrunningcompanionapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

// Fragment to allow user to calculate pace based information
public class PaceCalculatorFragment extends Fragment {
    TextView mainActivityText, nullChecker;
    EditText time_hrs_edittext, time_min_edittext, time_sec_edittext, pace_distance_edittext, pace_hrs_edittext, pace_min_edittext, pace_sec_edittext;
    Spinner distance_spinner, pace_units_spinner;
    ArrayList<String> pace_arraylist, distance_arraylist;
    Button calculate;
    double filled_out_fields, totalTime, totalPace, dummyNumber = 0;
    double conversion = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_pacecalculator, null);
        mainActivityText = getActivity().findViewById(R.id.textview_main);

        // Initializes UI components
        time_hrs_edittext = fragmentView.findViewById(R.id.time_hrs_edittext);
        time_min_edittext = fragmentView.findViewById(R.id.time_min_edittext);
        time_sec_edittext = fragmentView.findViewById(R.id.time_sec_edittext);
        pace_hrs_edittext = fragmentView.findViewById(R.id.pace_hrs_edittext);
        pace_min_edittext = fragmentView.findViewById(R.id.pace_min_edittext);
        pace_sec_edittext = fragmentView.findViewById(R.id.pace_sec_edittext);
        pace_distance_edittext = fragmentView.findViewById(R.id.distance_amount_edittext);
        distance_spinner = fragmentView.findViewById(R.id.distance_spinner);
        pace_units_spinner = fragmentView.findViewById(R.id.pace_units_spinner);
        nullChecker = fragmentView.findViewById(R.id.null_checker);
        calculate = fragmentView.findViewById(R.id.calculate);

        // Adds options for user's desired measuring metric
        pace_arraylist = new ArrayList<>();
        pace_arraylist.add("per mile");
        pace_arraylist.add("per km");

        distance_arraylist = new ArrayList<>();
        distance_arraylist.add("miles");
        distance_arraylist.add("kilometers");
        distance_arraylist.add("meters");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, pace_arraylist);
        ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, distance_arraylist);

        distance_spinner.setAdapter(spinnerAdapter1);
        pace_units_spinner.setAdapter(spinnerAdapter);

        distance_spinner.setSelection(0);
        pace_units_spinner.setSelection(0);

        // Does calculation for remaining empty field
        calculate.setOnClickListener(view -> {
            if (!checkPaceNull()) {
                filled_out_fields++;
            }
            if(!checkTimeNull()) {
                filled_out_fields++;
            }
            if(!checkDistanceNull()) {
                filled_out_fields++;
            }
            if(filled_out_fields!=2){
                nullChecker.setText("*Must Fill Out Only 2 Fields");
            }
            else {
                nullChecker.setText("");
                if(checkPaceNull()){
                    totalTime = convertToSeconds(time_hrs_edittext, time_min_edittext, time_sec_edittext);
                    unitConversion();
                    dummyNumber = totalTime/((Double.parseDouble(pace_distance_edittext.getText().toString()))*conversion)/3600;
                    convertToHours(pace_hrs_edittext, pace_min_edittext, pace_sec_edittext);
                } else if(checkDistanceNull()){
                    totalTime = convertToSeconds(time_hrs_edittext, time_min_edittext, time_sec_edittext);
                    totalPace = convertToSeconds(pace_hrs_edittext, pace_min_edittext, pace_sec_edittext);
                    unitConversion();
                    pace_distance_edittext.setText(Double.toString((Math.round(((totalTime/totalPace)*(1.0/conversion))*100))/100.0));
                } else if(checkTimeNull()){
                    totalPace = convertToSeconds(pace_hrs_edittext, pace_min_edittext, pace_sec_edittext);
                    unitConversion();
                    dummyNumber = ((Double.parseDouble(pace_distance_edittext.getText().toString())*conversion) * totalPace)/3600;
                    convertToHours(time_hrs_edittext, time_min_edittext, time_sec_edittext);
                }
            }
            filled_out_fields = 0;
            conversion = 1;
        });

        return fragmentView;
    }

    // Checks whether the time input fields are empty
    public boolean checkTimeNull(){
        return time_hrs_edittext.getText().toString().equals("") && time_min_edittext.getText().toString().equals("") && time_sec_edittext.getText().toString().equals("");
    }

    // Checks whether the distance input field is empty
    public boolean checkDistanceNull(){
        return pace_distance_edittext.getText().toString().equals("");
    }

    // Checks whether the pace input fields are empty
    public boolean checkPaceNull(){
        return pace_hrs_edittext.getText().toString().equals("") && pace_min_edittext.getText().toString().equals("") && pace_sec_edittext.getText().toString().equals("");
    }

    // Converts hours, minutes, seconds to total amount of seconds
    public double convertToSeconds(EditText editText_hrs, EditText editText_min, EditText editText_sec){
        double total = 0;
        if(!editText_hrs.getText().toString().equals(""))
            total += (Double.parseDouble(editText_hrs.getText().toString())) * 3600;
        if(!editText_min.getText().toString().equals(""))
            total += (Double.parseDouble(editText_min.getText().toString())) * 60;
        if(!editText_sec.getText().toString().equals(""))
            total += Double.parseDouble(editText_sec.getText().toString());

        return total;
    }

    public void convertToHours(EditText editText_hrs, EditText editText_min, EditText editText_sec){
        editText_hrs.setText(String.format("%02d", (int)dummyNumber));
        dummyNumber= (dummyNumber - Double.parseDouble(editText_hrs.getText().toString())) * 60;
        editText_min.setText(String.format("%02d", (int)dummyNumber));
        dummyNumber= (dummyNumber - Double.parseDouble(editText_min.getText().toString())) * 60;
        editText_sec.setText(String.format("%02d", Math.round(dummyNumber)));
    }

    // Does unit conversions based on user's selected units
    public void unitConversion(){
        if (distance_spinner.getSelectedItem().toString().equals("miles") && pace_units_spinner.getSelectedItem().toString().equals("per km")) {
            conversion = 1.609344;
        } else if(distance_spinner.getSelectedItem().toString().equals("kilometers") && pace_units_spinner.getSelectedItem().toString().equals("per mile")) {
            conversion = 1 / 1.609344;
        } else if (distance_spinner.getSelectedItem().toString().equals("meters") && pace_units_spinner.getSelectedItem().toString().equals("per mile")) {
            conversion = 1 / 1609.344;
        } else if (distance_spinner.getSelectedItem().toString().equals("meters") && pace_units_spinner.getSelectedItem().toString().equals("per km")) {
            conversion = 1 / 1000.0;
        }
    }
}