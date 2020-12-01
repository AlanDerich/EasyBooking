package com.derich.hama.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.derich.hama.OfferDetails;
import com.derich.hama.ProductPagerAdapter;
import com.derich.hama.ViewProductFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.derich.hama.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HousesAdapter.OnItemsClickListener{
            private static final int NUM_COLUMNS = 2;

            //vars
            HousesAdapter mAdapter;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            List<HousesContainers> mHouses;
            List<Favorites> mFavorites;
            List<String> mFavoriteNames;
            private List<OfferDetails> mAllOffers;
            private ProductPagerAdapter mPagerAdapter;
            List<String> cats = new ArrayList<>();
            ProgressBar pbLoading;
            Context mContext;
            //widgets
            private RecyclerView mRecyclerView;
            FirebaseStorage storage;
            private ViewPager mProductContainer;
            private TabLayout mTabLayout;
            StorageReference storageReference;
            private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

            public View onCreateView(@NonNull LayoutInflater inflater,
                    ViewGroup container, Bundle savedInstanceState) {
                View root = inflater.inflate(R.layout.fragment_home, container, false);
                mRecyclerView = root.findViewById(R.id.rvAllHousesNormal);
                pbLoading = root.findViewById(R.id.progressBarNormalHouses);
                mRecyclerView.setVisibility(View.INVISIBLE);
                storage = FirebaseStorage.getInstance();
                mProductContainer = root.findViewById(R.id.product_container_normal);
                mTabLayout = root.findViewById(R.id.tab_layout_normal);
                storageReference = storage.getReference();
//        spCategories=root.findViewById(R.id.spinnerCategories);
                mContext= getActivity();
                getOffers();
                getFavorites();
                return root;
            }

    private void getFavorites() {
        if (mUser!=null) {
            db.collectionGroup("AllFavorites").whereEqualTo("username", mUser.getEmail()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        mFavorites = new ArrayList<>();
                        mFavoriteNames=new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                mFavorites.add(document.toObject(Favorites.class));
                            }
                            int k;
                            for (k=0;k<mFavorites.size();k++){
                                mFavoriteNames.add(mFavorites.get(k).getPlotName()+mFavorites.get(k).getHouseNumber()+mFavorites.get(k).getOwnerName());

                            }
                        }
                        getPlots();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("SpecificService", "error " + e);
                    });
        }
        else {
//            Toast.makeText(mContext, "This section is only available for logged in customers.", Toast.LENGTH_LONG).show();
            getPlots();
        }
    }

    private void getOffers() {
        db.collectionGroup("AllHousesOnSale").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mAllOffers = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                                mAllOffers.add(snapshot.toObject(OfferDetails.class));
                            }
                        } else {
//                            Toast.makeText(mContext, "No house photos added yet. photos you add will appear here", Toast.LENGTH_LONG).show();
                        }
                        initPagerAdapter();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.w("HouseInfo", "error " + e);
                });
    }
    private void initPagerAdapter(){
        ArrayList<Fragment> fragments = new ArrayList<>();
        OfferDetails products = new OfferDetails();
        for(OfferDetails product: mAllOffers){
            ViewProductFragment viewProductFragment = new ViewProductFragment(product,"normalUser");
            fragments.add(viewProductFragment);
        }
        mPagerAdapter = new ProductPagerAdapter(getParentFragmentManager(), fragments);
        mProductContainer.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mProductContainer, true);
    }
    private void getPlots(){
                //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
                db.collectionGroup("AllHouses").whereEqualTo("status",0).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                mHouses = new ArrayList<>();
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                        mHouses.add(snapshot.toObject(HousesContainers.class));
                                    initRecyclerView();
                                } else {
                                    Toast.makeText(mContext, "No plots found. Please add a new plot", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                                Log.d("ViewHousesFragment","Error " + e);
                            }
                        });
                pbLoading.setVisibility(View.GONE);
            }

            private void initRecyclerView(){
                mAdapter = new HousesAdapter(mHouses,this,mFavoriteNames);
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
            }
            @Override
            public void onItemsClick(int position) {

            }
        }