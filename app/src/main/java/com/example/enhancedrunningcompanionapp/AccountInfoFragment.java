package com.example.enhancedrunningcompanionapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
//Fragment to display all of the user's account information
public class AccountInfoFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView nameTextView, emailTextView;
    Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_accountinfo, null);

        // Initialize Firebase Realtime Database and create a reference to user-specific data
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Initialize UI Components
        nameTextView = fragmentView.findViewById(R.id.nameTextView);
        emailTextView = fragmentView.findViewById(R.id.emailTextView);
        logoutButton = fragmentView.findViewById(R.id.logoutButton);

        // Attach a ValueEventListener to myRef to listen for changes in user data
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                nameTextView.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
                emailTextView.setText(currentUser.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Logs out user when button is clicked
        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return fragmentView;
    }
}