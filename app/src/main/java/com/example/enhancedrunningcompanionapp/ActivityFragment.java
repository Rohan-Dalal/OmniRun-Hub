package com.example.enhancedrunningcompanionapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
// Fragment to display the user's run logs.
public class ActivityFragment extends Fragment implements OnMapReadyCallback {
    TextView mainActivityText;
    ListView activity_listview;
    ArrayList<Activities> activitiesArrayList;
    GoogleMap map;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_activity, null);

        // Initialize UI Components
        mainActivityText = getActivity().findViewById(R.id.textview_main);
        activity_listview = fragmentView.findViewById(R.id.activitylistview);
        activitiesArrayList = new ArrayList<>();

        // Initialize Firebase Realtime Database and create a reference to user-specific data
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("activities");

        // Obtain and asynchronously load the Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps2);
        mapFragment.getMapAsync(this);

        // Sets up the list view using the custom adapter defined bellow
        CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.adapter_layout_activities, activitiesArrayList);
        activity_listview.setAdapter(adapter);
        // When each activity is clicked, it displays the run route associated with it on the map
        activity_listview.setOnItemClickListener((adapterView, view, i, l) -> {
            map.clear();
            ArrayList<LatLng> latLngArrayList = LatLngWrapper.convertToLatLngArrayList(adapter.getItem(i).getLatLngArrayList());
            PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngArrayList).clickable(false);
            Polyline line = map.addPolyline(polylineOptions);
            line.setColor(Color.BLUE);
            map.animateCamera(CameraUpdateFactory.newLatLng(latLngArrayList.get(0)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngArrayList.get(0), 16.0f));
        });

        // When a change to user data is found, the list view is updated
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Activities value = snapshot1.getValue(Activities.class);
                    activitiesArrayList.add(value);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return fragmentView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }

    public class CustomAdapter extends ArrayAdapter<Activities> {
        List <Activities> list;
        Context context;
        int xmlResource;

        // Custom Adapter to display neatly display the list view
        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Activities> objects){
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

            // Initializes all the TextViews that are used to display relevant information for each of the user's runs
            TextView adapter_time = adapterLayout.findViewById(R.id.adaptertime);
            TextView adapter_pace = adapterLayout.findViewById(R.id.adapterpace);
            TextView adapter_distance = adapterLayout.findViewById(R.id.adapterdistance);
            TextView adapter_beginning_time = adapterLayout.findViewById(R.id.timeTextView);
            TextView adapter_date = adapterLayout.findViewById(R.id.dateTextView);

            // Set based on data stored in Firebase
            adapter_time.setText(list.get(position).getTime());
            adapter_pace.setText(list.get(position).getPace());
            adapter_distance.setText(list.get(position).getMileage());
            adapter_beginning_time.setText(list.get(position).getBeginningTime());
            adapter_date.setText(list.get(position).getDate());

            return adapterLayout;
        }
    }

}