package com.polycarp.easybooking.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.polycarp.easybooking.R;
import com.polycarp.easybooking.mainlandlord.LandLordHousesAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements HousesAdapter.OnItemsClickListener{
            private static final int NUM_COLUMNS = 2;

            //vars
            HousesAdapter mAdapter;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            List<HousesContainers> mHouses;
            List<String> cats = new ArrayList<>();
            ProgressBar pbLoading;
            Context mContext;
            //widgets
            private RecyclerView mRecyclerView;
            FirebaseStorage storage;
            StorageReference storageReference;
            private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

            public View onCreateView(@NonNull LayoutInflater inflater,
                    ViewGroup container, Bundle savedInstanceState) {
                View root = inflater.inflate(R.layout.fragment_home, container, false);
                mRecyclerView = root.findViewById(R.id.rvAllHousesNormal);
                pbLoading = root.findViewById(R.id.progressBarNormalHouses);
                mRecyclerView.setVisibility(View.INVISIBLE);
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
//        spCategories=root.findViewById(R.id.spinnerCategories);
                mContext= getActivity();
                getPlots();
                return root;
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
                mAdapter = new HousesAdapter(mHouses,this);
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
            }
            @Override
            public void onItemsClick(int position) {

            }
        }