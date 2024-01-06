package com.example.enhancedrunningcompanionapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

// Fragment to allow user to calculate splits for desired race
public class RaceCalculatorFragment extends Fragment {
    Spinner race_distance_spinner;
    ArrayList<String> race_distance_list;
    Button calculate_splits;
    EditText race_time_hrs, race_time_min, race_time_sec,race_time_t;
    ListView listView;
    ArrayList<RaceSplits> raceSplitsArrayList;
    double total_laps, total_time, lap_time, lap_hr, lap_min, lap_sec, original_lap = 0;
    CustomAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_racecalculator, null);

        // Initialize UI components
        race_distance_spinner = fragmentView.findViewById(R.id.race_distance_spinner);
        listView = fragmentView.findViewById(R.id.list);
        calculate_splits = fragmentView.findViewById(R.id.calculate_splits);
        race_time_hrs = fragmentView.findViewById(R.id.race_time_hrs);
        race_time_min = fragmentView.findViewById(R.id.race_time_min);
        race_time_sec = fragmentView.findViewById(R.id.race_time_sec);
        race_time_t = fragmentView.findViewById(R.id.race_time_t);

        if (raceSplitsArrayList == null) {
            raceSplitsArrayList = new ArrayList<>();
        }

        // Sets up the list view using the custom adapter made bellow
        adapter = new CustomAdapter(getActivity(), R.layout.adapter_layout_race_calculator, raceSplitsArrayList);
        listView.setAdapter(adapter);

        // Gives users options on desired race
        race_distance_list = new ArrayList<String>();
        race_distance_list.add("10 km");
        race_distance_list.add("5 km");
        race_distance_list.add("3200 m");
        race_distance_list.add("1600 m");
        race_distance_list.add("800 m");

        // Create and set up an ArrayAdapter for the race distance spinner using the provided layout and data.
        ArrayAdapter<String> spinnerAdapter;
        spinnerAdapter = new ArrayAdapter<>(getActivity(), androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, race_distance_list);
        race_distance_spinner.setAdapter(spinnerAdapter);

        // Gets lap amounts based on user selection
        race_distance_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0: total_laps = 25;
                        break;

                    case 1: total_laps = 12.5;
                        break;

                    case 2: total_laps = 8;
                        break;

                    case 3: total_laps = 4;
                        break;

                    case 4: total_laps = 2;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Calculates and displays the split required for each lap based on user inputted time
        calculate_splits.setOnClickListener(view -> {
            raceSplitsArrayList.clear();

            total_time = convertToSeconds(race_time_hrs, race_time_min, race_time_sec, race_time_t);
            for(int i = 0; i<total_laps; i++) {

                original_lap = total_time/total_laps;
                lap_time = (total_time/total_laps + i*original_lap)/3600.0;
                if(total_laps == 12.5 && i == 12)
                    lap_time -= (original_lap / 7200.0);
                lap_hr = Math.floor(lap_time);
                lap_time = (lap_time - lap_hr)*60;
                lap_min = Math.floor(lap_time);
                lap_time = (lap_time - lap_min)*60;
                lap_sec = lap_time;

                raceSplitsArrayList.add(new RaceSplits(i+1, String.format("%02d", (int)lap_hr) + ":" + String.format("%02d", (int)lap_min) + ":" + String.format("%.2f", lap_sec), (i+1)*400));

            }
            if(total_laps==12.5){
                raceSplitsArrayList.remove(12);
                raceSplitsArrayList.add(new RaceSplits(12.5, String.format("%02d", (int)lap_hr) + ":" + String.format("%02d", (int)lap_min) + ":" + String.format("%.2f", lap_sec), 5000));
            }
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        });

        return fragmentView;
    }

    // Custom Adapter to display neatly display the race splits
    public class CustomAdapter extends ArrayAdapter<RaceSplits>{
        List <RaceSplits> list;
        Context context;
        int xmlResource;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<RaceSplits> objects){
            super(context, resource, objects);
            xmlResource = resource;
            list = objects;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterLayout = layoutInflater.inflate(xmlResource, null);

            // Initialize UI Components
            TextView lap_number = adapterLayout.findViewById(R.id.lap_number);
            TextView lap_distance = adapterLayout.findViewById(R.id.lap_distance);
            TextView lap_time = adapterLayout.findViewById(R.id.lap_time);

            // Set TextViews based on lap information
            lap_number.setText(String.format(Double.toString(list.get(position).getLap())).replaceAll("\\.0+$", ""));
            lap_distance.setText((list.get(position).getDistance()) + " meters");
            lap_time.setText(list.get(position).getTime() + " (Hrs, Min, Sec)");

            return adapterLayout;
        }
    }

    // Convert user input from EditText fields to seconds and return the total time.
    public double convertToSeconds(EditText editText_hrs, EditText editText_min, EditText editText_sec, EditText editText_t){
        double total = 0;
        if(!editText_hrs.getText().toString().equals(""))
            total += (Double.parseDouble(editText_hrs.getText().toString())) * 3600;
        if(!editText_min.getText().toString().equals(""))
            total += (Double.parseDouble(editText_min.getText().toString())) * 60;
        if(!editText_sec.getText().toString().equals(""))
            total += Double.parseDouble(editText_sec.getText().toString());
        if(!editText_t.getText().toString().equals(""))
            if(editText_t.getText().toString().length()==1)
                total += Double.parseDouble(editText_t.getText().toString())/10.0;
            else
                total += Double.parseDouble(editText_t.getText().toString())/100.0;

        return total;
    }
}