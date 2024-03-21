package com.chirag.tybcaproject.Adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chirag.tybcaproject.Activity.ListPlantActivity;
import com.chirag.tybcaproject.Domain.Category;
import com.chirag.tybcaproject.R;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewholder> {
    ArrayList<Category> item;
    Context context;

    public CategoryAdapter(ArrayList<Category> item) {
        this.item = item;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category,parent,false);

        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, @SuppressLint("RecyclerView") int position) {
        holder.titleTxt.setText(item.get(position).getName());

        float radius=10f;
        View decorView=((Activity)holder.itemView.getContext()).getWindow().getDecorView();
        ViewGroup rootView=(ViewGroup)decorView.findViewById(android.R.id.content);
        Drawable windownBackground=decorView.getBackground();

        holder.blurView.setupWith(rootView, new RenderScriptBlur(holder.itemView.getContext())) // or RenderEffectBlur
               .setFrameClearDrawable(windownBackground) // Optional
               .setBlurRadius(radius);

        holder.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        holder.blurView.setClipToOutline(true);

        int drawableResourceId=holder.itemView.getResources()
               .getIdentifier(item.get(position).getImagePath(),"drawable",context.getPackageName());

        Glide.with(context)
               .load(drawableResourceId)
               .into(holder.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, ListPlantActivity.class);
                intent.putExtra("categoryId",item.get(position).getId());
                intent.putExtra("CategoryName",item.get(position).getName());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;
        BlurView blurView;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.titleCattxt);
            pic=itemView.findViewById(R.id.imgCat);
            blurView=itemView.findViewById(R.id.blueView);

        }
    }
}