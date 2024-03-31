package com.chirag.tybcaproject.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chirag.tybcaproject.Adaptor.BestFoodAdapter;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAllActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<Foods> allPlantsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        recyclerView = findViewById(R.id.recyclerViewAllPlants);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // 1 columns for 1 items per row

        // Call method to retrieve all best foods
        ViewallPlant();
    }

    private void ViewallPlant() {
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Foods");
        Query query = myref.orderByChild("bestFood").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Foods> bestFoodList = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        bestFoodList.add(issue.getValue(Foods.class));
                    }
                    if (!bestFoodList.isEmpty()) {
                        // Set up RecyclerView adapter
                        RecyclerView.Adapter adapter = new BestFoodAdapter(bestFoodList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(ViewAllActivity.this, "No plants found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewAllActivity.this, "No plants found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("ViewAllActivity", "Database error: " + error.getMessage());
            }
        });
    }
}
