package com.chirag.tybcaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chirag.tybcaproject.Adaptor.BestFoodAdapter;
import com.chirag.tybcaproject.Adaptor.CategoryAdapter;
import com.chirag.tybcaproject.Adaptor.ListFoodAdapter;
import com.chirag.tybcaproject.Domain.Category;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.MainMenu;
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
    private RecyclerView.Adapter adapterListPlant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBestPlant();
        initCategory();
        setVariable();
        Logout();
    }

    private void Logout() {
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, MainMenu.class));
                finish();
            }
        });
    }

    private void setVariable() {
        binding.cartBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CartActivity.class)));
        binding.viewallitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Passing necessary parameters for search
                Intent intent = new Intent(HomeActivity.this, ListPlantActivity.class);
                intent.putExtra("isSearch", false); // Set to false to show all items
                startActivity(intent);
            }
        });

        binding.searchBtn.setOnClickListener(v -> {
            String text = binding.searchEdt.getText().toString().trim();
            if (!text.isEmpty()) {
                searchPlants(text);
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
        ArrayList<Category> list = new ArrayList<>();

        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        // Retrieve each category and add it to the list
                        Category category = issue.getValue(Category.class);
                        list.add(category);
                    }

                    if (!list.isEmpty()) {
                        // Set up RecyclerView with LinearLayoutManager
                        binding.categoryView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        // Initialize and set the adapter
                        RecyclerView.Adapter adapterCategory = new CategoryAdapter(list);
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
        ArrayList<Foods> list = new ArrayList<>();
        Query query = myref.orderByChild("bestFood").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Foods.class));
                    }
                    if (!list.isEmpty()) {
                        binding.bestPlantView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        RecyclerView.Adapter adapterBestFood = new BestFoodAdapter(list);
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

    private void searchPlants(String searchText) {
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Foods");
        binding.progressBarBestPlant.setVisibility(View.VISIBLE);
        ArrayList<Foods> searchResults = new ArrayList<>();

        Query query = myref.orderByChild("title")
                .startAt(searchText.toLowerCase())
                .endAt(searchText.toLowerCase() + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        searchResults.add(issue.getValue(Foods.class));
                    }
                    if (!searchResults.isEmpty()) {
                        binding.bestPlantView.setLayoutManager(new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.HORIZONTAL, false));
                        adapterListPlant = new ListFoodAdapter(searchResults);
                        binding.bestPlantView.setAdapter(adapterListPlant);
                    }
                    binding.progressBarBestPlant.setVisibility(View.GONE);
                } else {
                    // Handle case where no matching items were found
                    Toast.makeText(HomeActivity.this, "No matching items found", Toast.LENGTH_SHORT).show();
                    binding.progressBarBestPlant.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("HomeActivity", "Database error: " + error.getMessage());
                binding.progressBarBestPlant.setVisibility(View.GONE);
            }
        });
    }
}
