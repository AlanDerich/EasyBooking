package com.derich.hama.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.derich.hama.HouseInfoFragment;
import com.derich.hama.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HousesAdapter extends RecyclerView.Adapter<HousesAdapter.ViewHolder>{
    Context mContext;
    List<HousesContainers> mHouses;
    private HousesAdapter.OnItemsClickListener onItemsClickListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private int pos;
    private List<String> mFavorites;

    public HousesAdapter(List<HousesContainers> mHouses, HousesAdapter.OnItemsClickListener onItemsClickListener,List <String> mFavorites) {
        this.mHouses = mHouses;
        this.mFavorites = mFavorites;
        this.onItemsClickListener = onItemsClickListener;
    }

    @NonNull
    @Override
    public HousesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_houses,parent,false);
        mContext = parent.getContext();
        return new HousesAdapter.ViewHolder(view,onItemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HousesAdapter.ViewHolder holder, final int position) {
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.tvName.setText(mHouses.get(position).getPlotName());
        holder.tvLocation.setText(mHouses.get(position).getLocation());
        int price = (Integer.parseInt(mHouses.get(position).getRent()));
        holder.tvPrice.setText("Rent: "+fmt.format(price));
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.loader_icon);
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(mHouses.get(position).getHouseImage())
                .into(holder.imgCategory);
        holder.mainLayout.setOnClickListener(view -> {
            Bundle args = new Bundle();
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment fragmentStaff = new HouseInfoFragment();
            FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
            transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
            transactionStaff.addToBackStack(null);
            args.putString("rent",mHouses.get(position).getRent());
            args.putString("location",mHouses.get(position).getLocation());
            args.putString("deposit",mHouses.get(position).getDeposit());
            args.putString("houseNumber",mHouses.get(position).getHouseNumber());

            args.putString("type",mHouses.get(position).getType());
            args.putString("phoneNo",mHouses.get(position).getPhoneNo());
            args.putString("plotName",mHouses.get(position).getPlotName());
            args.putString("houseImage",mHouses.get(position).getHouseImage());
            args.putString("owner",mHouses.get(position).getOwner());
            args.putInt("status",mHouses.get(position).getStatus());
            fragmentStaff.setArguments(args);
            transactionStaff.commit();
        });
        if (mFavorites!=null){
            if (mFavorites.size()!=0){
                for (String string : mFavorites) {
                    if(string.equals(mHouses.get(position).getPlotName()+mHouses.get(position).getHouseNumber()+mHouses.get(position).getOwner())){
                        holder.btnFavorite.setImageResource(R.drawable.ic_favorited);
                    }
                }
            }
        }

        holder.btnFavorite.setOnClickListener(view -> {
            if (mUser!=null){
                if (checkIfFavorited(mHouses.get(position).getPlotName()+mHouses.get(position).getHouseNumber()+mHouses.get(position).getOwner())){
                    db.collection("Favorites").document(mUser.getEmail()).collection("AllFavorites").document(mHouses.get(position).getPlotName()+mHouses.get(position).getHouseNumber())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                Toast.makeText(mContext,"Removed from Favorites",Toast.LENGTH_LONG).show();
                                holder.btnFavorite.setImageResource(R.drawable.ic_favourite);
                                mFavorites.remove(mHouses.get(position).getPlotName()+mHouses.get(position).getHouseNumber()+mHouses.get(position).getOwner());

                            })
                            .addOnFailureListener(e -> Toast.makeText(mContext,"Not removed. Try again later.",Toast.LENGTH_LONG).show());
                }
                else {
                    Favorites mFav=new Favorites(mHouses.get(position).getPlotName(),mHouses.get(position).getHouseNumber(),mHouses.get(position).getOwner(),mUser.getEmail());
                    db.collection("Favorites").document(mUser.getEmail()).collection("AllFavorites").document(mHouses.get(position).getPlotName()+mHouses.get(position).getHouseNumber())
                            .set(mFav)
                            .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                Toast.makeText(mContext,"Added to Favorites",Toast.LENGTH_LONG).show();
                                holder.btnFavorite.setImageResource(R.drawable.ic_favorited);
                                mFavorites.add(mHouses.get(position).getPlotName()+mHouses.get(position).getHouseNumber()+mHouses.get(position).getOwner());

                            })
                            .addOnFailureListener(e -> Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show());
                }
            }
        });
    }
    private Boolean checkIfFavorited(String ssName) {
        if (mFavorites.size()!=0){
            for (String string : mFavorites) {
                if(string.equals(ssName)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mHouses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvName,tvLocation,tvPrice;
        private ImageView imgCategory;
        private CardView mainLayout;
        private ImageButton btnFavorite;
        HousesAdapter.OnItemsClickListener onItemsClickListener;
        public ViewHolder(@NonNull View itemView, HousesAdapter.OnItemsClickListener onItemsClickListener) {
            super(itemView);
            this.onItemsClickListener=onItemsClickListener;
            tvName=itemView.findViewById(R.id.titleMain);
            tvPrice=itemView.findViewById(R.id.priceMain);
            btnFavorite=itemView.findViewById(R.id.imageButtonFavorite);
            tvLocation=itemView.findViewById(R.id.locationMain);
            imgCategory=itemView.findViewById(R.id.imageHouse);
            mainLayout=itemView.findViewById(R.id.card_view_houses);
            itemView.setOnClickListener(this);
//        itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onClick(View view) {
            onItemsClickListener.onItemsClick(getAdapterPosition());
        }
    }
    public interface OnItemsClickListener{
        void onItemsClick(int position);
    }
}
