package com.derich.hama.mainlandlord;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.derich.hama.Plots;
import com.derich.hama.R;

import java.io.File;
import java.util.List;

public class LandlordPlotsAdapter extends RecyclerView.Adapter<LandlordPlotsAdapter.ViewHolder>{
    Context mContext;
    List<Plots> mPlots;
    private File localFile;
    private Bitmap bmp;
    private ViewHolder holder1;
    private LandlordPlotsAdapter.OnItemsClickListener onItemsClickListener;
    private int pos;

    public LandlordPlotsAdapter(List<Plots> mPlots, OnItemsClickListener onItemsClickListener) {
        this.mPlots = mPlots;
        this.onItemsClickListener = onItemsClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_plots,parent,false);
        mContext = parent.getContext();
        return new LandlordPlotsAdapter.ViewHolder(view,onItemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tvName.setText(mPlots.get(position).getPlotName());
        holder.tvLocation.setText(mPlots.get(position).getLocation());
        holder.tvDescription.setText(mPlots.get(position).getPhoneNo());
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.loader_icon);
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(mPlots.get(position).getPlotImage())
                .into(holder.imgCategory);
        holder.mainLayout.setOnClickListener(view -> onItemsClickListener.onItemsClick(mPlots.get(position)));
    }

    @Override
    public int getItemCount() {
        return mPlots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvName,tvLocation,tvDescription;
        private ImageView imgCategory;
        private CardView mainLayout;
        LandlordPlotsAdapter.OnItemsClickListener onItemsClickListener;
        public ViewHolder(@NonNull View itemView, LandlordPlotsAdapter.OnItemsClickListener onItemsClickListener) {
            super(itemView);
            this.onItemsClickListener=onItemsClickListener;
            tvName=itemView.findViewById(R.id.titlePlot);
            tvLocation=itemView.findViewById(R.id.locationPlot);
            tvDescription=itemView.findViewById(R.id.descriptionPlot);
            imgCategory=itemView.findViewById(R.id.imagePlot);
            mainLayout=itemView.findViewById(R.id.card_view_plots);
            itemView.setOnClickListener(this);
//        itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onClick(View view) {
            onItemsClickListener.onItemsClick(mPlots.get(getAdapterPosition()));
        }
    }
    public interface OnItemsClickListener{
        void onItemsClick(Plots mPlots);
    }
}