package com.chirag.tybcaproject.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chirag.tybcaproject.Adaptor.ListFoodAdapter;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.databinding.ActivityListPlantBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListPlantActivity extends BaseActivity {

    private ActivityListPlantBinding binding;
    private RecyclerView.Adapter adapterListPlant;
    private int categoryId;

    private String categoryName;
    private String searchText;
    private boolean isSearch;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListPlantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();

        initList();
    }

    private void getIntentExtra() {

        categoryId = getIntent().getIntExtra("categoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSerach", false);

        binding.titleTxt.setText(categoryName);
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initList() {
        DatabaseReference myref = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query;
        if (isSearch && searchText != null && !searchText.isEmpty()) {
            // Perform a partial search based on title
            query = myref.orderByChild("title")
                    .startAt(searchText.toLowerCase())
                    .endAt(searchText.toLowerCase() + "\uf8ff");
        } else {
            query = myref.orderByChild("categoryId").equalTo(categoryId);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Foods.class));
                    }
                    if (list.size() > 0) {
                        binding.foodListView.setLayoutManager(new GridLayoutManager(ListPlantActivity.this, 2));
                        adapterListPlant = new ListFoodAdapter(list);
                        binding.foodListView.setAdapter(adapterListPlant);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    // Handle case where no matching items were found
                    // Show a message or perform any other action
                    // For example, you can display a toast message
                    Toast.makeText(ListPlantActivity.this, "No matching items found", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("ListPlantActivity", "Database error: " + error.getMessage());
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }
}

