package com.chirag.tybcaproject.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chirag.tybcaproject.Adaptor.FavoriteAdapter;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private List<Foods> favoriteList;
    private ProgressBar progressBar;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.recyclerViewFavorite);
        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.backBtn);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoriteList = new ArrayList<>();
        adapter = new FavoriteAdapter(this, favoriteList);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed(); // Navigate back when back button is clicked
            }
        });

        retrieveFavoriteItems();
    }

    private void retrieveFavoriteItems() {
        progressBar.setVisibility(View.VISIBLE); // Show ProgressBar

        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites");
        favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Foods favoriteItem = snapshot.getValue(Foods.class);
                    favoriteList.add(favoriteItem);
                }
                if (favoriteList.isEmpty()) {
                    // No favorite items found, show toast message
                    Toast.makeText(FavoriteActivity.this, "No favorite items found", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE); // Hide ProgressBar
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FavoriteActivity.this, "Failed to retrieve favorites", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE); // Hide ProgressBar
            }
        });
    }

}
