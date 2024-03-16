package com.chirag.tybcaproject.Activity;



import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import com.bumptech.glide.Glide;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.Helper.ManagmentCart;
import com.chirag.tybcaproject.databinding.ActivityDetailBinding;

import eightbitlab.com.blurview.RenderScriptBlur;


public class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    private Foods object;
    private  int num=1;
    private ManagmentCart managmentCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentCart = new ManagmentCart(this);

     getBundleExtra();
     setVatiable();
     setBlurEffect();
    }

    private void setBlurEffect() {
        float radius=10f;
        View decorView=(this).getWindow().getDecorView();
        ViewGroup rootView=(ViewGroup)decorView.findViewById(android.R.id.content);
        Drawable windownBackground=decorView.getBackground();

        binding.blueView.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                .setFrameClearDrawable(windownBackground) // Optional
                .setBlurRadius(radius);

        binding.blueView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blueView.setClipToOutline(true);

        binding.blueView1.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                .setFrameClearDrawable(windownBackground) // Optional
                .setBlurRadius(radius);

        binding.blueView1.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blueView1.setClipToOutline(true);
    }

    private void setVatiable() {
        binding.backBtn.setOnClickListener(v -> finish());{
            Glide.with(DetailActivity.this)
                    .load(object.getImagePath())
                    .into(binding.pic);

            binding.priceTxt.setText("₹"+object.getPrice());
            binding.titleTxt.setText(object.getTitle());
            binding.descriptionTxt.setText(object.getDescription());
            binding.ratingTxt.setText(object.getStar()+"Rating");
            binding.ratingBar.setRating((float)object.getStar());
            binding.totalTxt.setText((num*object.getPrice())+"₹");

            binding.plusBtn.setOnClickListener(v -> {
                num=num+1;
                binding.numTxt.setText(num+"");
                binding.totalTxt.setText(num*object.getPrice()+"₹");
            });

            binding.minusBtn.setOnClickListener(v -> {
                if (num>1){
                    num=num-1;
                    binding.numTxt.setText(num+"");
                    binding.totalTxt.setText(num*object.getPrice()+"₹");
                }

            });

            binding.addButton.setOnClickListener(v -> {
                object.setNumberInCart(num);
                managmentCart.insertFood(object);
            });
        }
    }

    private void getBundleExtra() {

        object= (Foods) getIntent().getSerializableExtra("object");
    }
}