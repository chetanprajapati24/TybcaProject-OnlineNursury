package com.chirag.tybcaproject.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chirag.tybcaproject.Adaptor.BestFoodAdapter;
import com.chirag.tybcaproject.Adaptor.CategoryAdapter;
import com.chirag.tybcaproject.Domain.Category;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.MainMenu;
import com.chirag.tybcaproject.R;
import com.chirag.tybcaproject.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter adapterListPlant;
    private ArrayList<Category> categoryList = new ArrayList<>();
    private ArrayList<Foods> bestFoodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        initBestPlant();
        initCategory();
        setVariable();
        Logout();
    }

    private void Logout() {
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   FirebaseAuth.getInstance().signOut();
             //   startActivity(new Intent(HomeActivity.this, MainMenu.class));
             //   finish();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext()); // Use v.getContext() to get the context
                builder.setMessage("Are you sure you want to Logout ?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(v.getContext(), MainMenu.class); // Use v.getContext() to get the context
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    private void setVariable() {
        binding.cartBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CartActivity.class)));
        binding.viewallitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ViewAllActivity.class));
            }
        });
        binding.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
            }
        });

        binding.searchBtn.setOnClickListener(v -> {
            String text = binding.searchEdt.getText().toString().trim();
            if (!text.isEmpty()) {
                // Start the SearchResultsActivity with the search query
                Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
                intent.putExtra("searchQuery", text);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
            }
        });

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ProfileUpdateActivity.class));
            }
        });
    }

    private void initCategory() {
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);

        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        // Retrieve each category and add it to the list
                        Category category = issue.getValue(Category.class);
                        categoryList.add(category);
                    }

                    if (!categoryList.isEmpty()) {
                        // Set up RecyclerView with LinearLayoutManager
                        binding.categoryView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        // Initialize and set the adapter
                        RecyclerView.Adapter adapterCategory = new CategoryAdapter(categoryList);
                        binding.categoryView.setAdapter(adapterCategory);
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("HomeActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private void initBestPlant() {
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Foods");
        binding.progressBarBestPlant.setVisibility(View.VISIBLE);
        Query query = myref.orderByChild("bestFood").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bestFoodList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        bestFoodList.add(issue.getValue(Foods.class));
                    }
                    if (!bestFoodList.isEmpty()) {
                        binding.bestPlantView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        RecyclerView.Adapter adapterBestFood = new BestFoodAdapter(bestFoodList);
                        binding.bestPlantView.setAdapter(adapterBestFood);
                    }
                    binding.progressBarBestPlant.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("HomeActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private void refreshData() {
        // Reload category and best plant data
        initCategory();
        initBestPlant();

        // Stop the refresh animation
        swipeRefreshLayout.setRefreshing(false);
    }
}
