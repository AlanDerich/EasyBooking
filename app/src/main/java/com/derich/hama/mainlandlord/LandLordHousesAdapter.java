package com.derich.hama.mainlandlord;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.derich.hama.R;
import com.derich.hama.ui.home.HousesContainers;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class LandLordHousesAdapter extends RecyclerView.Adapter<LandLordHousesAdapter.ViewHolder>{
    Context mContext;
    List<HousesContainers> mHouses;
    private LandLordHousesAdapter.OnItemsClickListener onItemsClickListener;
    public LandLordHousesAdapter(List<HousesContainers> mHouses, LandLordHousesAdapter.OnItemsClickListener onItemsClickListener) {
        this.mHouses = mHouses;
        this.onItemsClickListener = onItemsClickListener;
    }

    @NonNull
    @Override
    public LandLordHousesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_houses,parent,false);
        mContext = parent.getContext();
        return new LandLordHousesAdapter.ViewHolder(view,onItemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LandLordHousesAdapter.ViewHolder holder, final int position) {
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(mHouses.get(position).getRent()));
        holder.tvName.setText("Room number "+mHouses.get(position).getHouseNumber());
        holder.tvLocation.setText(mHouses.get(position).getLocation());
        holder.tvPrice.setText("Rent: "+fmt.format(price));
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.loader_icon);
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(mHouses.get(position).getHouseImage())
                .into(holder.imgCategory);
        holder.mainLayout.setOnClickListener(view -> onItemsClickListener.onItemsClick(mHouses.get(position)));
    }

    @Override
    public int getItemCount() {
        return mHouses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvName,tvLocation,tvPrice;
        private ImageView imgCategory;
        private CardView mainLayout;
        LandLordHousesAdapter.OnItemsClickListener onItemsClickListener;
        public ViewHolder(@NonNull View itemView, LandLordHousesAdapter.OnItemsClickListener onItemsClickListener) {
            super(itemView);
            this.onItemsClickListener=onItemsClickListener;
            tvName=itemView.findViewById(R.id.titleMain);
            tvPrice=itemView.findViewById(R.id.priceMain);
            tvLocation=itemView.findViewById(R.id.locationMain);
            imgCategory=itemView.findViewById(R.id.imageHouse);
            mainLayout=itemView.findViewById(R.id.card_view_houses);
            itemView.setOnClickListener(this);
//        itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onClick(View view) {
            onItemsClickListener.onItemsClick(mHouses.get(getAdapterPosition()));
        }

    }

    public interface OnItemsClickListener{
        void onItemsClick(HousesContainers mHousez);
    }
}
