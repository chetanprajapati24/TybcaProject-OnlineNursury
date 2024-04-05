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

import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private Context context;
    private List<Foods> favoriteList;

    public FavoriteAdapter(Context context, List<Foods> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_list_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Foods favoriteItem = favoriteList.get(position);

        holder.titleTxt.setText(favoriteItem.getTitle());
        holder.starTxt.setText("" + favoriteItem.getStar());
        holder.priceTxt.setText("₹" + favoriteItem.getPrice());


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
                .load(favoriteItem.getImagePath())
                .transform(new RoundedCorners(30))
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent= new Intent(context, DetailActivity.class);
            intent.putExtra("object",favoriteList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt,priceTxt,starTxt;
        ImageView pic;
        BlurView blurView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTxt=itemView.findViewById(R.id.titleTxt);
            priceTxt=itemView.findViewById(R.id.priceTxt);
            starTxt=itemView.findViewById(R.id.starTxt);
            pic=itemView.findViewById(R.id.img);
            blurView=itemView.findViewById(R.id.blueView);
        }
    }
}
