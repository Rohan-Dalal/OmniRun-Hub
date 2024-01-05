package com.example.enhancedrunningcompanionapp;

import android.app.Activity;
import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {
    TextView mainActivityText;
    ListView activity_listview;
    ArrayList<Activities> activitiesArrayList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("activities");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_activity, null);
        mainActivityText = getActivity().findViewById(R.id.textview_main);
        activity_listview = fragmentView.findViewById(R.id.activitylistview);
        activitiesArrayList = new ArrayList<Activities>();

        CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.adapter_layout_activities, activitiesArrayList);
        activity_listview.setAdapter(adapter);

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

    public class CustomAdapter extends ArrayAdapter<Activities> {
        List <Activities> list;
        Context context;
        int xmlResource;

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

            TextView adapter_time = adapterLayout.findViewById(R.id.adaptertime);
            TextView adapter_pace = adapterLayout.findViewById(R.id.adapterpace);
            TextView adapter_distance = adapterLayout.findViewById(R.id.adapterdistance);

            adapter_time.setText("Time: " + list.get(position).getTime() + " (Hrs, Min, Sec)");
            adapter_pace.setText("Pace: " + list.get(position).getPace() + " (Hrs, Min, Sec) Per Mile");
            adapter_distance.setText("Distance: " + list.get(position).getMileage() + " Miles");

            return adapterLayout;
        }
    }

}