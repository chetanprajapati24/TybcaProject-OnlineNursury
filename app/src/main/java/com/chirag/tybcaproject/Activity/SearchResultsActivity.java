package com.chirag.tybcaproject.Activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chirag.tybcaproject.Adaptor.ListFoodAdapter;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResultsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ArrayList<Foods> searchResultsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerView = findViewById(R.id.recyclerViewSearchResults);

        // Retrieve the search query from the intent
        String searchQuery = getIntent().getStringExtra("searchQuery");
        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchPlants(searchQuery);
        } else {
            Toast.makeText(this, "No search query found", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchPlants(String searchText) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Foods");
        Query query = myRef.orderByChild("title")
                .startAt(searchText.toUpperCase())
                .endAt(searchText.toLowerCase() + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchResultsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        searchResultsList.add(issue.getValue(Foods.class));
                    }
                    displaySearchResults(); // Call method to display search results
                } else {
                    Toast.makeText(SearchResultsActivity.this, "No matching items found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void displaySearchResults() {
        RecyclerView.Adapter adapterSearchResults = new ListFoodAdapter(searchResultsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultsActivity.this));
        recyclerView.setAdapter(adapterSearchResults); // Set the adapter here
    }
}