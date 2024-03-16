package com.chirag.tybcaproject.Adaptor;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.Helper.ChangeNumberItemsListener;
import com.chirag.tybcaproject.Helper.ManagmentCart;
import com.chirag.tybcaproject.R;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder>{
    ArrayList<Foods> listItemSelected;
    private ManagmentCart managmentCart;
    ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter(ArrayList<Foods> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = listItemSelected;
        this.managmentCart = new ManagmentCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart,parent,false);

        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        float radius = 10f;
        View decorView = ((Activity) holder.itemView.getContext()).getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windownBackground = decorView.getBackground();

        holder.blueView.setupWith(rootView, new RenderScriptBlur(holder.itemView.getContext())) // or RenderEffectBlur
                .setFrameClearDrawable(windownBackground) // Optional
                .setBlurRadius(radius);

        holder.blueView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        holder.blueView.setClipToOutline(true);

        if (listItemSelected.get(position).getImagePath() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(listItemSelected.get(position).getImagePath())
                    .transform(new CenterCrop(), new RoundedCorners(30))
                    .into(holder.pic);
        } else {
            // Handle null image path
            // For example, you can set a placeholder image or hide the ImageView
            holder.pic.setImageDrawable(null); // Clear the ImageView
        }


        holder.title.setText(listItemSelected.get(position).getTitle());
        holder.feeEachitem.setText("₹" + listItemSelected.get(position).getPrice());
        holder.totalEachitem.setText("₹" + (listItemSelected.get(position).getNumberInCart() * listItemSelected.get(position).getPrice()));
        holder.num.setText(String.valueOf(listItemSelected.get(position).getNumberInCart()));

        //holder.orderbtn.setOnClickListener(v -> Toast.makeText(holder.itemView.getContext(), "Oder Successful", Toast.LENGTH_SHORT).show());


        holder.plusitem.setOnClickListener(v ->
                managmentCart.plusNumberItem(listItemSelected, position, () -> {
                    changeNumberItemsListener.change();
                    notifyDataSetChanged();
                }));

        holder.minusitem.setOnClickListener(v ->
                managmentCart.minusNumberItem(listItemSelected, position, () -> {
                    changeNumberItemsListener.change();
                    notifyDataSetChanged();
                }));
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title,feeEachitem,plusitem,minusitem;
        ImageView pic;
        TextView totalEachitem ,num;
        BlurView blueView , orderbtn;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            title =itemView.findViewById(R.id.titleTxt);
            feeEachitem=itemView.findViewById(R.id.feeEachitem);
            plusitem=itemView.findViewById(R.id.plusBtn);
            minusitem=itemView.findViewById(R.id.minusBtn);
            pic=itemView.findViewById(R.id.img);
            totalEachitem=itemView.findViewById(R.id.priceTxt);
            num=itemView.findViewById(R.id.numTxt);
            blueView=itemView.findViewById(R.id.blueView);
            orderbtn=itemView.findViewById(R.id.orderBtn);
        }
    }
}
