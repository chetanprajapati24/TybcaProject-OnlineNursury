package com.chirag.tybcaproject.Activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chirag.tybcaproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileUpdateActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        // Get the user's ID from the intent
        userId = getIntent().getStringExtra("userId");

        // Initialize Firebase Realtime Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Check if userId is not null before creating a child reference
        if (userId != null) {
            // Get a reference to the user's node in the database
            mUserDatabaseReference = mFirebaseDatabase.getReference("users").child(userId);
        } else {
            // Handle the case where userId is null (e.g., log an error or display a message)
            Log.e(TAG, "User ID is null");
        }

        // Initialize UI elements
        final TextInputLayout firstNameInput = findViewById(R.id.Firstname);
        final TextInputLayout lastNameInput = findViewById(R.id.Lastname);

        final TextInputLayout houseNoInput = findViewById(R.id.houseNo);
        final Button updateProfileButton = findViewById(R.id.UpdateProfile);

        // Set a click listener for the update profile button
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = String.valueOf(firstNameInput.getEditText().getText());
                String lastName = String.valueOf(lastNameInput.getEditText().getText());

                String houseNo = String.valueOf(houseNoInput.getEditText().getText());
                              // Create a new user object with the updated information
                User updatedUser = new User(firstName, lastName,  houseNo);

                // Update the user's node in the database
                mUserDatabaseReference.setValue(updatedUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Show a toast message to confirm the update
                                    Toast.makeText(ProfileUpdateActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Show a toast message if the update fails
                                    Toast.makeText(ProfileUpdateActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}