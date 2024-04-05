package com.chirag.tybcaproject.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.Helper.ManagmentCart;
import com.chirag.tybcaproject.R;
import com.chirag.tybcaproject.databinding.ActivityDetailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    private Foods object;
    private ImageView imageViewFavorite;
    private boolean isFavorite = false;
    private int num = 1;
    private ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentCart = new ManagmentCart(this);

        imageViewFavorite = findViewById(R.id.fav);

        imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    imageViewFavorite.setImageResource(R.drawable.favorite_white);
                    isFavorite = false;
                    Toast.makeText(DetailActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    removeFromFavoritesInFirebase(object);
                } else {
                    imageViewFavorite.setImageResource(R.drawable.favorite_fill);
                    isFavorite = true;
                    Toast.makeText(DetailActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    addToFavoritesInFirebase(object);
                }
            }
        });

        getBundleExtra();
        setVatiable();
        setBlurEffect();
    }

    private void setBlurEffect() {
        // Your existing blur effect setup
    }

    private void setVatiable() {
        binding.backBtn.setOnClickListener(v -> finish());
        Glide.with(DetailActivity.this)
                .load(object.getImagePath())
                .into(binding.img);

        binding.priceTxt.setText("₹" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getStar() + "Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText((num * object.getPrice()) + "₹");

        binding.plusBtn.setOnClickListener(v -> {
            num = num + 1;
            binding.numTxt.setText(num + "");
            binding.totalTxt.setText(num * object.getPrice() + "₹");
        });

        binding.minusBtn.setOnClickListener(v -> {
            if (num > 1) {
                num = num - 1;
                binding.numTxt.setText(num + "");
                binding.totalTxt.setText(num * object.getPrice() + "₹");
            }
        });

        binding.addButton.setOnClickListener(v -> {
            object.setNumberInCart(num);
            managmentCart.insertFood(object);
        });
    }

    private void getBundleExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }

    // Method to add to Firebase favorites
    private void addToFavoritesInFirebase(Foods food) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites");
        String key = favoritesRef.push().getKey(); // Get a new unique key
        favoritesRef.child(key).setValue(food);
    }

    // Method to remove from Firebase favorites
    private void removeFromFavoritesInFirebase(Foods food) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites");
        Query query = favoritesRef.orderByChild("title").equalTo(food.getTitle()); // Assuming 'title' is a unique identifier for your items
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(DetailActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DetailActivity.this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }



}
