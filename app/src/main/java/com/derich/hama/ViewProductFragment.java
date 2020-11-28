package com.derich.hama;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.derich.hama.mainlandlord.landlordui.ViewHousesInPlotLandlordFragment;

import java.text.NumberFormat;
import java.util.Locale;


public class ViewProductFragment extends Fragment {

    private static final String TAG = "ViewProductFragment";

    //widgets
    public ImageView mImageView;
//    private TextView mTitle;
    private TextView mNewPrice;
    private CardView cvOffers;
    private Context mContext;
    //vars
    public OfferDetails mProduct;
    String section;

    public ViewProductFragment(OfferDetails product,String section) {
        mProduct = product;
        this.section=section;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_product, container, false);
        mImageView = view.findViewById(R.id.image);
        mContext=getContext();
//        mTitle = view.findViewById(R.id.title);
        mNewPrice = view.findViewById(R.id.textView_offer_new_price);
        cvOffers = view.findViewById(R.id.cv_offers);
        cvOffers.setOnClickListener(view1 -> showDetailsFragment());

        setProduct();

        return view;
    }

    private void showDetailsFragment() {
        Bundle args = new Bundle();
        AppCompatActivity activity = (AppCompatActivity) mContext;
        Fragment fragmentStaff = new SellingHouseFragment();
        FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
        if (section.equals("admin")){
            transactionStaff.replace(R.id.nav_host_fragment_landlord,fragmentStaff);
        }
        else {
            transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
        }
        transactionStaff.addToBackStack(null);
        args.putString("houseName",mProduct.getOfferName());
        args.putString("houseLocation",mProduct.getLocation());
        args.putString("newPrice",mProduct.getNewPrice());
        args.putString("houseDetails",mProduct.getDetails());
        args.putString("houseOwner",mProduct.getOwner());
        args.putString("section",section);
        fragmentStaff.setArguments(args);
        transactionStaff.commit();
    }

    private void setProduct(){
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_loading);

        Glide.with(getActivity())
                .setDefaultRequestOptions(requestOptions)
                .load(mProduct.getPic())
                .into(mImageView);
        mNewPrice.setText(mProduct.getNewPrice());
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        mNewPrice.setText(fmt.format(Integer.valueOf(mProduct.getNewPrice())));
    }


}