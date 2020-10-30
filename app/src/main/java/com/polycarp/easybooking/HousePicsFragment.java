package com.polycarp.easybooking;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.Locale;

public class HousePicsFragment extends Fragment {
    ImageView imgOrder;
    private String oldP,newP,img;

    public HousePicsFragment(String img) {
        this.img=img;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_house_pics, container, false);

        imgOrder = rootView.findViewById(R.id.imageView_offers_product_image);
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);
        Glide.with(getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(img)
                .into(imgOrder);
        return rootView;
    }
}