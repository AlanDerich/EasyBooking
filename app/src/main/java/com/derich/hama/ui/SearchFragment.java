package com.derich.hama.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.derich.hama.Plots;
import com.derich.hama.R;
import com.derich.hama.ui.home.Favorites;
import com.derich.hama.ui.home.HousesAdapter;
import com.derich.hama.ui.home.HousesContainers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements HousesAdapter.OnItemsClickListener{
    private Context mContext;
    private LinearLayout llFilters;
    private RecyclerView rvSearched;
    private ImageButton imgButtonAddFilters;
    List<Favorites> mFavorites;
    List<String> mFavoriteNames;
    HousesAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    List<HousesContainers> mHouses,mHousesInitial;
    private CheckBox checkBoxLocation,checkBoxRent,checkBoxType;
    private Spinner spinnerLocations,spinnerRents,spinnerTypes;
    private String typeSelected,locationSelected,rentSelected,maxRentSelected,minRentSelected;
    private TextView textViewFiltersApplied;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment llAddFilters
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        llFilters = root.findViewById(R.id.llAddFilters);
        imgButtonAddFilters=root.findViewById(R.id.imgButtonAddFilters);
        rvSearched=root.findViewById(R.id.rv_searched_houses);
        textViewFiltersApplied = root.findViewById(R.id.textViewFiltersApplied);
        mContext=getActivity();
        llFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFiltersDialog();
            }
        });
        imgButtonAddFilters.setOnClickListener(view -> showFiltersDialog());
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

    private void showFiltersDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Filters");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_filters,null);
        checkBoxLocation = add_menu_layout.findViewById(R.id.checkBoxLocation);
        checkBoxRent=add_menu_layout.findViewById(R.id.checkBoxRent);
        checkBoxType=add_menu_layout.findViewById(R.id.checkBoxType);
        spinnerLocations = add_menu_layout.findViewById(R.id.spinnerLocations);
        spinnerRents=add_menu_layout.findViewById(R.id.spinnerRent);
        spinnerTypes=add_menu_layout.findViewById(R.id.spinnerType);
        checkBoxLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    spinnerLocations.setVisibility(View.VISIBLE);
                }
                else {
                    spinnerLocations.setVisibility(View.GONE);
                    locationSelected = null;
                }
            }
        });
        checkBoxRent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    spinnerRents.setVisibility(View.VISIBLE);
                }
                else {
                    spinnerRents.setVisibility(View.GONE);
                    rentSelected = null;
                }
            }
        });
        checkBoxType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    spinnerTypes.setVisibility(View.VISIBLE);
                }
                else {
                    spinnerTypes.setVisibility(View.GONE);
                    typeSelected = null;
                }
            }
        });
        List<String> types = new ArrayList<>();
        types.add("Single room");
        types.add("Betsitter self-contained");
        types.add("One Bedroom");
        types.add("Two bedroom");
        types.add("3 Bedroom");
        ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, types);
        spinnerTypes.setAdapter(usersAdapter);
        spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeSelected = spinnerTypes.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        List<String> locations = new ArrayList<>();
        if (mHousesInitial.size()!=0){
            int a=0;
            for (a=0;a<mHousesInitial.size();a++){
                String kk=mHousesInitial.get(a).getLocation();
                if (!locations.contains(kk)){
                    locations.add(kk);
                }
            }
        }
        ArrayAdapter<String> locationsAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, locations);
        spinnerLocations.setAdapter(locationsAdapter);
        spinnerLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                locationSelected = spinnerLocations.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        List<String> rentRange = new ArrayList<>();

        ArrayAdapter<String> rentAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, rentRange);
        rentAdapter.add("2000-5000");
        rentAdapter.add("5001-7000");
        rentAdapter.add("7001-9000");
        rentAdapter.add("9001-11000");
        rentAdapter.add("11001-13000");
        rentAdapter.add("13001-15000");
        rentAdapter.add("15001-17000");
        rentAdapter.add("17001-19000");
        rentAdapter.add("19001-22000");
        spinnerRents.setAdapter(rentAdapter);
        spinnerRents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rentSelected = spinnerRents.getSelectedItem().toString();
                String arr[] = rentSelected.split("-", 2);
                minRentSelected = arr[0];
                maxRentSelected = arr[1];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_house);
            alertDialog.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (locationSelected!=null){
                    if (typeSelected!=null){
                        if (rentSelected!=null){
                            //location=true,type=true,rent=true;
                            getPlotsByLocationTypeRent();
                            textViewFiltersApplied.setText("Location: "+locationSelected+", Type:"+ typeSelected+", Rent:" + rentSelected);
                        }
                        else {
                            //location=true,type=true,rent=false;
                            getPlotsByLocationType();
                            textViewFiltersApplied.setText("Location: "+locationSelected+", Type:"+ typeSelected);
                        }
                    }
                    else {
                        if (rentSelected!=null){
                            //location=true,type=false,rent=true;
                            getPlotsByLocationRent();
                            textViewFiltersApplied.setText("Location: "+locationSelected+", Rent:" + rentSelected);
                        }
                        else {
                            //location=true,type=false,rent=false;
                            getPlotsByLocation();
                            textViewFiltersApplied.setText("Location: "+locationSelected);
                        }
                    }
                }
                else {
                    if (typeSelected!=null){
                        if (rentSelected!=null){
                            //location=false,type=true,rent=true;
                            getPlotsByTypeRent();
                            textViewFiltersApplied.setText("Type:"+ typeSelected+", Rent:" + rentSelected);
                        }
                        else {
                            //location=false,type=true,rent=false;
                            getPlotsByType();
                            textViewFiltersApplied.setText("Type:"+ typeSelected);
                        }
                    }
                    else {
                        if (rentSelected!=null){
                            //location=false,type=false,rent=true;
                            getPlotsByRent();
                            textViewFiltersApplied.setText("Rent:" + rentSelected);
                        }
                        else {
                            //location=false,type=false,rent=false;
                            Toast.makeText(mContext,"No Filter selected",Toast.LENGTH_SHORT).show();
                            textViewFiltersApplied.setText("No Filter selected");
                            getPlots();
                        }
                    }
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private void reset(){
    locationSelected=null;
    typeSelected=null;
    rentSelected=null;
    maxRentSelected=null;
    minRentSelected=null;
    }
    private void getPlotsByRent() {
        db.collectionGroup("AllHouses").whereLessThan("rent",maxRentSelected).whereGreaterThan("rent",minRentSelected).whereEqualTo("status",0).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mHouses = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mHouses.add(snapshot.toObject(HousesContainers.class));
                            initRecyclerView();
                        } else {
                            Toast.makeText(mContext, "No houses found matching your criteria", Toast.LENGTH_LONG).show();
                        }
                        reset();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("ViewHousesFragment","Error " + e);
                    }
                });
    }

    private void getPlotsByType() {
        db.collectionGroup("AllHouses").whereEqualTo("type",typeSelected).whereEqualTo("status",0).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mHouses = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mHouses.add(snapshot.toObject(HousesContainers.class));
                            initRecyclerView();
                        } else {
                            Toast.makeText(mContext, "No houses found matching your criteria.", Toast.LENGTH_LONG).show();
                        }
                        reset();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("ViewHousesFragment","Error " + e);
                    }
                });
    }
    private void getPlotsByLocation() {
        db.collectionGroup("AllHouses").whereEqualTo("location",locationSelected).whereEqualTo("status",0).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mHouses = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mHouses.add(snapshot.toObject(HousesContainers.class));
                            initRecyclerView();
                        } else {
                            Toast.makeText(mContext, "No houses found matching your criteria.", Toast.LENGTH_LONG).show();
                        }
                        reset();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("ViewHousesFragment","Error " + e);
                    }
                });
    }
    private void getPlotsByTypeRent() {
        db.collectionGroup("AllHouses").whereEqualTo("type",typeSelected).whereLessThanOrEqualTo("rent",maxRentSelected).whereGreaterThanOrEqualTo("rent",minRentSelected).whereEqualTo("status",0).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mHouses = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mHouses.add(snapshot.toObject(HousesContainers.class));
                            initRecyclerView();
                        } else {
                            Toast.makeText(mContext, "No houses found matching your criteria.", Toast.LENGTH_LONG).show();
                        }
                        reset();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("ViewHousesFragment","Error " + e);
                    }
                });
    }


    private void getPlotsByLocationRent() {
        db.collectionGroup("AllHouses").whereEqualTo("location",locationSelected).whereLessThanOrEqualTo("rent",maxRentSelected).whereGreaterThanOrEqualTo("rent",minRentSelected).whereEqualTo("status",0).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mHouses = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mHouses.add(snapshot.toObject(HousesContainers.class));
                            initRecyclerView();
                        } else {
                            Toast.makeText(mContext, "No houses found matching your criteria.", Toast.LENGTH_LONG).show();
                        }
                        reset();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("ViewHousesFragment","Error " + e);
                    }
                });
    }

    private void getPlotsByLocationType() {
        db.collectionGroup("AllHouses").whereEqualTo("type",typeSelected).whereEqualTo("location",locationSelected).whereEqualTo("status",0).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mHouses = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mHouses.add(snapshot.toObject(HousesContainers.class));
                            initRecyclerView();
                        } else {
                            Toast.makeText(mContext, "No houses found matching your criteria.", Toast.LENGTH_LONG).show();
                        }
                        reset();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("ViewHousesFragment","Error " + e);
                    }
                });
    }

    private void getPlotsByLocationTypeRent() {
        db.collectionGroup("AllHouses").whereEqualTo("type",typeSelected).whereEqualTo("location",locationSelected).whereLessThanOrEqualTo("rent",maxRentSelected).whereGreaterThanOrEqualTo("rent",minRentSelected).whereEqualTo("status",0).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mHouses = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mHouses.add(snapshot.toObject(HousesContainers.class));
                            initRecyclerView();
                        } else {
                            Toast.makeText(mContext, "No houses found matching your criteria.", Toast.LENGTH_LONG).show();
                        }
                        reset();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("ViewHousesFragment","Error " + e);
                    }
                });
    }

    private void getPlots(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("AllHouses").whereEqualTo("status",0).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mHouses = new ArrayList<>();
                        mHousesInitial=new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mHouses.add(snapshot.toObject(HousesContainers.class));
                            mHousesInitial=mHouses;
                            initRecyclerView();
                        } else {
                            Toast.makeText(mContext, "No houses found. Please add a new plot", Toast.LENGTH_LONG).show();
                        }
                        reset();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("ViewHousesFragment","Error " + e);
                    }
                });
    }

    private void initRecyclerView(){
        mAdapter = new HousesAdapter(mHouses,this,mFavoriteNames);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
        rvSearched.setLayoutManager(layoutManager);
        rvSearched.setAdapter(mAdapter);
        rvSearched.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemsClick(int position) {

    }
}