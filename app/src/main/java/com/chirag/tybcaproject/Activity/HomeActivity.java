package com.chirag.tybcaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chirag.tybcaproject.Adaptor.BestFoodAdapter;
import com.chirag.tybcaproject.Adaptor.CategoryAdapter;
import com.chirag.tybcaproject.Domain.Category;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.Domain.Location;
import com.chirag.tybcaproject.Domain.Price;
import com.chirag.tybcaproject.MainMenu;
import com.chirag.tybcaproject.R;
import com.chirag.tybcaproject.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLocation();
    //    initTime();
        initPrice();
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
                startActivity(new Intent(HomeActivity.this, ListPlantActivity.class));
            }
        });

        binding.searchBtn.setOnClickListener(v -> {
            String text =binding.searchEdt.getText().toString();
            if (!text.isEmpty()){
                Intent intent= new Intent(HomeActivity.this, ListPlantActivity.class);
                intent.putExtra("text",text);
                intent.putExtra("isSearch",true);
                startActivity(intent);
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
        DatabaseReference myref = database.getReference("Category");
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
            }
        });
    }


    private void initBestPlant() {
        DatabaseReference myref=database.getReference("Foods");
        binding.progressBarBestPlant.setVisibility(View.VISIBLE);
        ArrayList<Foods> list=new ArrayList<>();
        Query query=myref.orderByChild("bestFood").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue:snapshot.getChildren()){
                        list.add(issue.getValue(Foods.class));
                    }
                    if (list.size()>0){
                        binding.bestPlantView.setLayoutManager(new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.HORIZONTAL,false));
                        RecyclerView.Adapter adapterBestFood =new BestFoodAdapter(list);
                        binding.bestPlantView.setAdapter(adapterBestFood);

                    }
                    binding.progressBarBestPlant.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initLocation() {
        DatabaseReference myRef = database.getReference("Location");
        ArrayList<Location> list = new ArrayList<>();
        ArrayAdapter<Location> adapter = new ArrayAdapter<>(HomeActivity.this, R.layout.sp_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.locationsp.setAdapter(adapter);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear(); // Clear the previous list
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Location.class));
                    }
                    adapter.notifyDataSetChanged(); // Update the adapter with the new list
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void initPrice() {
        DatabaseReference myRef = database.getReference("price");
        ArrayList<Price> list = new ArrayList<>();
        ArrayAdapter<Price> adapter = new ArrayAdapter<>(HomeActivity.this, R.layout.sp_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.Pricesp.setAdapter(adapter); // Make sure binding.Pricesp refers to your Spinner

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear(); // Clear the previous list
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Price.class));
                    }
                    adapter.notifyDataSetChanged(); // Update the adapter with the new list
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}