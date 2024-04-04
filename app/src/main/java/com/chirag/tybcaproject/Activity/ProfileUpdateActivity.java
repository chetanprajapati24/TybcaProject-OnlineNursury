package com.chirag.tybcaproject.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chirag.tybcaproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileUpdateActivity extends AppCompatActivity {

    String[] Maharashtra = {"Mumbai", "Pune", "Nashik"};
    String[] Gujarat = {"Valsad", "Navsari", "Surat"};

    EditText firstName, lastName, address;
    Spinner stateSpinner, citySpinner;
    Button updateButton;
    DatabaseReference customerReference;
    String state, city, userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        firstName = findViewById(R.id.fnamee);
        lastName = findViewById(R.id.lnamee);
        address = findViewById(R.id.address);
        stateSpinner = findViewById(R.id.statee);
        citySpinner = findViewById(R.id.cityy);
        updateButton = findViewById(R.id.update);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        customerReference = FirebaseDatabase.getInstance().getReference("Customer").child(userId);
        customerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve data from Firebase with specific keys
                    String fName = dataSnapshot.child("First Name").getValue(String.class);
                    String lName = dataSnapshot.child("Last Name").getValue(String.class);
                    String userAddress = dataSnapshot.child("HouseNo").getValue(String.class);
                    String userState = dataSnapshot.child("State").getValue(String.class);
                    String userCity = dataSnapshot.child("City").getValue(String.class);

                    // Set retrieved data to respective EditText fields
                    firstName.setText(fName);
                    lastName.setText(lName);
                    address.setText(userAddress);

                    // Set up state and city spinners based on retrieved data
                    state = userState;
                    city = userCity;
                    setUpStateSpinner();

                    // Select the retrieved state in the spinner
                    int statePosition = getPosition(stateSpinner, state);
                    stateSpinner.setSelection(statePosition);

                    // Select the retrieved city in the spinner
                    int cityPosition = getPosition(citySpinner, city);
                    citySpinner.setSelection(cityPosition);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                customerReference = FirebaseDatabase.getInstance().getReference("Customer").child(userId);
                customerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Customer customer = dataSnapshot.getValue(Customer.class);
                        if (customer != null) {
                            userEmail = customer.getEmail();
                            String fName = firstName.getText().toString().trim();
                            String lName = lastName.getText().toString().trim();
                            String userAddress = address.getText().toString().trim();
                            // Update state and city variables with selected values from spinners
                            state = stateSpinner.getSelectedItem().toString();
                            city = citySpinner.getSelectedItem().toString();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("First Name", fName);
                            hashMap.put("Last Name", lName);
                            hashMap.put("City", city);
                           hashMap.put("Email", userEmail);

                           hashMap.put("Mobile No", customer.getMobileNo());
                            hashMap.put("HouseNo", userAddress);
                            hashMap.put("State", state);
                            customerReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileUpdateActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProfileUpdateActivity.this, "Failed to Update Profile", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }
        });

    }

    private void setUpStateSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.State, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = parent.getItemAtPosition(position).toString();
                if (selectedState.equals("Maharashtra")) {
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(ProfileUpdateActivity.this, android.R.layout.simple_spinner_item, Maharashtra);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(cityAdapter);
                } else if (selectedState.equals("Gujarat")) {
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(ProfileUpdateActivity.this, android.R.layout.simple_spinner_item, Gujarat);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(cityAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private int getPosition(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
}
