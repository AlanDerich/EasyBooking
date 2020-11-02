package com.polycarp.easybooking.ui.bookings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.polycarp.easybooking.HouseBooking;
import com.polycarp.easybooking.R;
import com.polycarp.easybooking.ui.home.HousesContainers;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class   BookingAdapterNormal extends RecyclerView.Adapter<BookingAdapterNormal.ViewHolder>{
    Context mContext;
    List<HouseBooking> mHouses;
    private BookingAdapterNormal.OnItemsClickListener onItemsClickListener;
    private int pos;

    public BookingAdapterNormal(List<HouseBooking> mHouses, BookingAdapterNormal.OnItemsClickListener onItemsClickListener) {
        this.mHouses = mHouses;
        this.onItemsClickListener = onItemsClickListener;
    }

    @NonNull
    @Override
    public BookingAdapterNormal.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bookings,parent,false);
        mContext = parent.getContext();
        return new BookingAdapterNormal.ViewHolder(view,onItemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapterNormal.ViewHolder holder, final int position) {
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.tvName.setText("Plot name: "+mHouses.get(position).getPlotName());
        holder.tvHouseNo.setText("House number "+mHouses.get(position).getHouseNo());
        holder.tvDate.setText("Date booked "+mHouses.get(position).getDate_booked());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle args = new Bundle();
//                AppCompatActivity activity = (AppCompatActivity) view.getContext();
//                Fragment fragmentStaff = new HouseInfoFragment();
//                FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
//                transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
//                transactionStaff.addToBackStack(null);
//                args.putString("rent",mHouses.get(position).getRent());
//                args.putString("location",mHouses.get(position).getLocation());
//                args.putString("deposit",mHouses.get(position).getDeposit());
//                args.putString("houseNumber",mHouses.get(position).getHouseNumber());
//                args.putString("details",mHouses.get(position).getDetails());
//
//                args.putString("type",mHouses.get(position).getType());
//                args.putString("phoneNo",mHouses.get(position).getPhoneNo());
//                args.putString("plotName",mHouses.get(position).getPlotName());
//                args.putString("houseImage",mHouses.get(position).getHouseImage());
//                args.putString("owner",mHouses.get(position).getOwner());
//                args.putInt("status",mHouses.get(position).getStatus());
//                fragmentStaff.setArguments(args);
//                transactionStaff.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHouses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvName,tvDate,tvHouseNo;
        private CardView mainLayout;
        BookingAdapterNormal.OnItemsClickListener onItemsClickListener;
        public ViewHolder(@NonNull View itemView, BookingAdapterNormal.OnItemsClickListener onItemsClickListener) {
            super(itemView);
            this.onItemsClickListener=onItemsClickListener;
            tvName=itemView.findViewById(R.id.title_main_normal_booking);
            tvHouseNo=itemView.findViewById(R.id.house_number_normal_booking);
            tvDate=itemView.findViewById(R.id.date_main_normal_booking);
            mainLayout=itemView.findViewById(R.id.card_view_normal_bookings);
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
