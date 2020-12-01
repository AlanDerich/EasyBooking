package com.derich.hama.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.derich.hama.R;
import com.derich.hama.UserDetails;
import com.derich.hama.ui.home.Favorites;
import com.derich.hama.ui.home.HousesAdapter;
import com.derich.hama.ui.home.HousesContainers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment implements HousesAdapter.OnItemsClickListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    HousesAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<HousesContainers> mServices;
    List<Favorites> mFavorites;
    List<String> mFavoriteNames,mFavoriteCategoryNames;
    Context mContext;
    //widgets
    private RecyclerView mRecyclerView;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private HousesContainers mServFromAdapter;
    private List<UserDetails> mUserr;
    private String section;
    private ProgressBar progressBar;
    private int pos = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        mRecyclerView = root.findViewById(R.id.rv_products_offered);
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressBar = root.findViewById(R.id.progressBarServices);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mContext= getActivity();
        checkUser();
        registerForContextMenu(mRecyclerView);
//        getServices();
        getFavorites();
        return root;
    }

    private void getFavorites() {
        if (mUser!=null) {
            db.collectionGroup("AllFavorites").whereEqualTo("username", mUser.getEmail()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        mFavorites = new ArrayList<>();
                        mFavoriteNames=new ArrayList<>();
                        mFavoriteCategoryNames=new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                mFavorites.add(document.toObject(Favorites.class));
                            }
                            int k;
                            for (k=0;k<mFavorites.size();k++){
                                mFavoriteNames.add(mFavorites.get(k).getPlotName()+mFavorites.get(k).getHouseNumber()+mFavorites.get(k).getOwnerName());
                                mFavoriteCategoryNames.add(mFavorites.get(k).getPlotName());
                            }
                            mServices = new ArrayList<>();
                            getServices(pos);
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(mContext, "You haven't favorited any service.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("SpecificService", "error " + e);
                    });
        }
        else {
            Toast.makeText(mContext, "This section is only available for logged in customers.", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getServices(int k){
        String mmm=mFavoriteCategoryNames.get(k);
        String kkk=mFavoriteNames.get(k);
        Favorites mfav=mFavorites.get(k);
        db.collectionGroup("AllHouses").whereEqualTo("plotName",mmm).whereEqualTo("houseNumber",mfav.getHouseNumber()).whereEqualTo("owner",mfav.getOwnerName()).whereEqualTo("status",0)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            mServices.add(document.toObject(HousesContainers.class));
                        }
                    } else {
                        Toast.makeText(mContext, "No products found", Toast.LENGTH_SHORT).show();
                    }
                    ++pos;
                    if (pos>=mFavoriteNames.size()){
                        progressBar.setVisibility(View.GONE);
                        initRecyclerView();
                    }
                    else {
                        getServices(pos);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.w("SpecificService", "error " + e);
                });

        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
    }

    private void initRecyclerView(){
        mAdapter = new HousesAdapter(mServices,this,mFavoriteNames);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemsClick(int mServ) {
//        view.showContextMenu();
    }
    private void checkUser() {
        if (mUser!=null){
            mUserr= new ArrayList<>();
            final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            db.collectionGroup("registeredUsers").whereEqualTo("username",mUser.getEmail()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        mUserr.add(snapshot.toObject(UserDetails.class));
                    }
                    int size = mUserr.size();
                    int position;
                    if (size==1){
                        position=0;
                        UserDetails userDetails= mUserr.get(position);
                        section = userDetails.getSection();
                    }

                } else {
                    String username =mUser.getEmail();
                    section = "simpleUser";
                    UserDetails newUser = new UserDetails(username,section);
                    Toast.makeText(mContext,"No data found.",Toast.LENGTH_LONG).show();
                    db.collection("users").document("all users").collection("registeredUsers").document(mUser.getEmail())
                            .set(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(mContext,"User added successfully",Toast.LENGTH_LONG).show();
                                section="simpleUser";
                            })
                            .addOnFailureListener(e -> Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show());

                }
            })
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext,"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                        Log.d("LoginAct","Error" + e);
                    });
        }
    }
}