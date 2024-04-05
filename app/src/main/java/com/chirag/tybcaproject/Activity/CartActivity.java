package com.chirag.tybcaproject.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chirag.tybcaproject.Adaptor.CartAdapter;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.Helper.ManagmentCart;
import com.chirag.tybcaproject.databinding.ActivityCartBinding;
import com.chirag.tybcaproject.databinding.BillLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eightbitlab.com.blurview.RenderScriptBlur;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;
    private double tax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        setVariable();
        calculateCart();
        initList();
        setBlurEffect();
        binding.orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void setBlurEffect() {
        float radius = 10f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        binding.blueView.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);

        binding.blueView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blueView.setClipToOutline(true);

        binding.blueView1.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);

        binding.blueView1.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blueView1.setClipToOutline(true);
    }

    private void initList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollview.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollview.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CartAdapter(managmentCart.getListCart(), CartActivity.this, this::calculateCart);
        binding.cartView.setAdapter(adapter);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void calculateCart() {
        double percentageTax = 0.02;
        double delivery = 10;
        tax = percentageTax * managmentCart.getTotalFee();
        double total = managmentCart.getTotalFee() + tax + delivery;
        double itemTotal = Math.floor(managmentCart.getTotalFee() * 10) / 10;

        binding.totalFeeTxt.setText("₹" + itemTotal);
        binding.taxTxt.setText("₹" + tax);
        binding.deliveryTxt.setText("₹" + delivery);
        binding.totalTxt.setText("₹" + total);
    }

    private void placeOrder() {
        // Perform actions to place the order, such as sending order details to a server or initiating a payment process
        showBillDialog();



    }

    private void uploadOrderToFirebase() {
        // Assuming you have a Firebase database reference
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        // Assuming you have a list of selected items
        // Loop through the list and upload each item to Firebase
        for (Foods item : managmentCart.getListCart()) {
            String orderId = ordersRef.push().getKey(); // Generate a unique key for each order
            ordersRef.child(orderId).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        // Handle successful upload
                        Toast.makeText(CartActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle upload failure
                        Toast.makeText(CartActivity.this, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void showBillDialog() {

        // Inflate the bill layout using DataBindingUtil
        BillLayoutBinding billBinding = BillLayoutBinding.inflate(getLayoutInflater());
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference customerReference = FirebaseDatabase.getInstance().getReference("Customer").child(userId);
        customerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user details from Firebase
                    String fName = dataSnapshot.child("First Name").getValue(String.class);
                    String lName = dataSnapshot.child("Last Name").getValue(String.class);
                    String userAddress = dataSnapshot.child("HouseNo").getValue(String.class);

                    // Set user details in the dialog
                    billBinding.fnamee.setText(fName);
                    billBinding.lastName.setText(lName);
                   billBinding.address.setText(userAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // Retrieve total price from the managementCart object
        double totalPrice = managmentCart.getTotalFee();

        // Calculate the total bill, tax, and delivery charges
        double itemTotal = Math.floor(totalPrice * 10) / 10;
        double tax = this.tax; // Use the previously calculated tax value
        double delivery = 10;
        double total = itemTotal + tax + delivery;



        // Set bill details
        billBinding.totalFeeTxt.setText("Item Total: ₹" + itemTotal);
        billBinding.taxTxt.setText("Tax: ₹" + tax);
        billBinding.deliveryTxt.setText("Delivery Charges: ₹" + delivery);
        billBinding.totalTxt.setText("Total: ₹" + total);


        // Create and configure the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(billBinding.getRoot())
                .setTitle("Bill Details")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // For example, you can show a confirmation message to the user
                       // showToast("Your order has been placed successfully!");

                        // Upload order to Firebase
                        uploadOrderToFirebase();

                        // After placing the order, you may want to clear the cart or perform other actions
                        managmentCart.clearCart();

                        // Update the UI to reflect the changes (e.g., update the cart list)
                        initList();

                        // Recalculate the cart totals
                        calculateCart();
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


    private void showToast(String message) {
        Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
