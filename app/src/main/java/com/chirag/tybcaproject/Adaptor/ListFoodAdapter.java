package com.chirag.tybcaproject.Adaptor;

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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chirag.tybcaproject.Activity.DetailActivity;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.R;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class ListFoodAdapter extends RecyclerView.Adapter<ListFoodAdapter.viewholder> {
    ArrayList<Foods> item;
    Context context;

    public ListFoodAdapter(ArrayList<Foods> item) {

        this.item = item;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate=LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_food,parent,false);

        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.titleTxt.setText(item.get(position).getTitle());
        holder.priceTxt.setText("₹"+item.get(position).getPrice());
      //  holder.timeTxt.setText(item.get(position).getTimeId()+"min");
        holder.starTxt.setText(""+item.get(position).getStar());

        float radius=10f;
        View decorView=((Activity)holder.itemView.getContext()).getWindow().getDecorView();
        ViewGroup rootView=(ViewGroup)decorView.findViewById(android.R.id.content);
        Drawable windownBackground=decorView.getBackground();

        holder.blurView.setupWith(rootView, new RenderScriptBlur(holder.itemView.getContext())) // or RenderEffectBlur
                .setFrameClearDrawable(windownBackground) // Optional
                .setBlurRadius(radius);

        holder.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        holder.blurView.setClipToOutline(true);

        Glide.with(context)
                .load(item.get(position).getImagePath())
                .transform(new RoundedCorners(30))
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent= new Intent(context, DetailActivity.class);
            intent.putExtra("object",item.get(position));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt,priceTxt,starTxt,timeTxt;
        ImageView pic;
        BlurView blurView;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.titleTxt);
            priceTxt=itemView.findViewById(R.id.priceTxt);
            starTxt=itemView.findViewById(R.id.starTxt);
       //     timeTxt=itemView.findViewById(R.id.timeTxt);
            pic=itemView.findViewById(R.id.img);
            blurView=itemView.findViewById(R.id.blueView);
        }
    }
}
