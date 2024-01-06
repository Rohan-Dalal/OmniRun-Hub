package com.example.enhancedrunningcompanionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// Activity to allow the user to log in using email and password
public class LoginActivity extends AppCompatActivity {
    Button signInButton;
    TextView switchToRegister;
    EditText emailEditText, passwordEditText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI Components
        signInButton = findViewById(R.id.signInButton);
        switchToRegister = findViewById(R.id.registerTextView);
        emailEditText = findViewById(R.id.emailEditText2);
        passwordEditText = findViewById(R.id.passwordEditText2);

        // Check if a user is already authenticated using FirebaseAuth. If so, finish the current activity and return.
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            finish();
            return;
        }

        // Signs in the user with inputted credentials
        signInButton.setOnClickListener(view -> signin());

        // Switches to the Register Activity
        switchToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        });
    }

    // Method to sign the user in
    private void signin() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All Fields Must Be Filled Out", Toast.LENGTH_SHORT).show();
            return;
        }

        // Accesses Firebase Authorization
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Open the first page the actual running application
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        } else {
                            // Notifies user that sign in failed
                            Toast.makeText(LoginActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}