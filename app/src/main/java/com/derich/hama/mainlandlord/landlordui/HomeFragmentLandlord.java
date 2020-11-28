package com.derich.hama.mainlandlord.landlordui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.derich.hama.OfferDetails;
import com.derich.hama.ProductPagerAdapter;
import com.derich.hama.ViewProductFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.derich.hama.Plots;
import com.derich.hama.R;
import com.derich.hama.mainlandlord.LandlordPlotsAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class HomeFragmentLandlord extends Fragment implements LandlordPlotsAdapter.OnItemsClickListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    LandlordPlotsAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Plots> mPlots;
    private List<OfferDetails> mAllOffers;
    List<String> cats = new ArrayList<>();
    Context mContext;
    MaterialEditText edtName,edtPlotOwner,edtPlotLocation,edtPhoneNumber,edtOthers;
    Button btnUpload, btnSelect;
    SwitchCompat switchWater,switchElectricity,switchWifi;
    CheckBox checkBoxWifiFree,checkBoxWifiPaid,checkBoxElectricityFree,checkBoxElectricityPaid,checkBoxWaterPaid,checkBoxWaterFree;
    //widgets
    private RecyclerView mRecyclerView;
    private Plots mNewPlots;
    private ProgressBar pbLoading;
    private FloatingActionButton fabAdd,fabAddOffer;
    Uri saveUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static final int PICK_IMAGE_REQUEST = 71;
    private String electricityType="none",waterType="none",wifiType="none";
    private String plotIMage;
    private ProductPagerAdapter mPagerAdapter;
    private ViewPager mProductContainer;
    private TabLayout mTabLayout;
    private Plots mPlotFromAdapter;
    private MaterialEditText edtOfferName,edtOfferNewPrice,edtOfferLocation,edtOfferDetails,edtOfferOwner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_landlord, container, false);
        mRecyclerView = root.findViewById(R.id.rv_landlord_plots);
        mRecyclerView.setVisibility(View.INVISIBLE);
        fabAddOffer=root.findViewById(R.id.fabAddOffer);
        mProductContainer = root.findViewById(R.id.product_container);
        mTabLayout = root.findViewById(R.id.tab_layout);
        pbLoading = root.findViewById(R.id.progressBarLandlordPlots);
        fabAdd=root.findViewById(R.id.fabAddPlot);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        registerForContextMenu(mRecyclerView);
        getOffers();
        fabAddOffer.setOnClickListener(view -> addOffer());
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("add");
            }
        });
//        spCategories=root.findViewById(R.id.spinnerCategories);
        mContext= getActivity();
        getPlots();
        return root;
    }
    private void addOffer() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new Offer");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_service_layout,null);

        edtOfferName = add_menu_layout.findViewById(R.id.edtOfferName);
        edtOfferNewPrice = add_menu_layout.findViewById(R.id.edtOfferNewPrice);
        edtOfferLocation = add_menu_layout.findViewById(R.id.edtOfferLocation);
        edtOfferDetails = add_menu_layout.findViewById(R.id.edtOfferDetails);
        edtOfferOwner = add_menu_layout.findViewById(R.id.edtOfferOwner);
        btnSelect = add_menu_layout.findViewById(R.id.btnProductSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUploadPic);
        //event for button
        btnSelect.setOnClickListener(v -> chooseImage());

        btnUpload.setOnClickListener(v -> uploadImage());

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.logo);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (plotIMage != null) {
                            OfferDetails mNewOffer = new OfferDetails(plotIMage, edtOfferName.getText().toString(), edtOfferNewPrice.getText().toString().toLowerCase(), edtOfferLocation.getText().toString().toLowerCase(), edtOfferDetails.getText().toString().toLowerCase(), edtOfferOwner.getText().toString().toLowerCase());
                            db.collection("AllHousesOnSale").document(mNewOffer.getOfferName() + mNewOffer.getOwner())
                                    .set(mNewOffer)
                                    .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                        Toast.makeText(getContext(), "House saved successfully", Toast.LENGTH_LONG).show();
                                        //initRecyclerView();
                                        getOffers();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Not saved. Try again later.", Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(mContext, "No image selected yet. Please upload an image to continue", Toast.LENGTH_LONG).show();
                        }
                        dialogInterface.dismiss();
                    }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
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
            ViewProductFragment viewProductFragment = new ViewProductFragment(product,"admin");
            fragments.add(viewProductFragment);
        }
        mPagerAdapter = new ProductPagerAdapter(getParentFragmentManager(), fragments);
        mProductContainer.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mProductContainer, true);
    }

    private void getPlots(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("AllPlots").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mPlots = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mPlots.add(snapshot.toObject(Plots.class));
                        } else {
                            Toast.makeText(mContext, "No plots found. Please add a new plot", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("HomeFragmentLandlord","Error " + e);
                    }
                });
        pbLoading.setVisibility(View.GONE);
    }

    private void initRecyclerView(){
        mAdapter = new LandlordPlotsAdapter(mPlots,this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
    }
    private void showDialog(String action) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_plot_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtPlotName);
        edtPlotLocation = add_menu_layout.findViewById(R.id.edtPlotLocation);
        edtPlotOwner = add_menu_layout.findViewById(R.id.edtPlotOwner);
        edtPhoneNumber = add_menu_layout.findViewById(R.id.edtPhoneNumber);
        edtOthers = add_menu_layout.findViewById(R.id.edtPlotComments);
        switchWifi = add_menu_layout.findViewById(R.id.switchWifi);
        switchWater = add_menu_layout.findViewById(R.id.switchWater);
        switchElectricity = add_menu_layout.findViewById(R.id.switchElectricity);
        btnSelect = add_menu_layout.findViewById(R.id.btnProductSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnHouseBook);
        checkBoxElectricityFree=add_menu_layout.findViewById(R.id.checkBoxElectricityFree);
        checkBoxElectricityPaid=add_menu_layout.findViewById(R.id.checkBoxElectricityPaid);
        checkBoxWaterFree=add_menu_layout.findViewById(R.id.checkBoxWaterFree);
        checkBoxWaterPaid=add_menu_layout.findViewById(R.id.checkBoxWaterPaid);
        checkBoxWifiFree=add_menu_layout.findViewById(R.id.checkBoxWifiFree);
        checkBoxWifiPaid=add_menu_layout.findViewById(R.id.checkBoxWifiPaid);
        if (action.equals("add")){
            alertDialog.setTitle("Add new Plot");
        }
        else {
            alertDialog.setTitle("Update Plot details");
            edtName.setText(mPlotFromAdapter.getPlotName());
            edtPlotLocation.setText(mPlotFromAdapter.getLocation());
            edtPhoneNumber.setText(mPlotFromAdapter.getPhoneNo());
            edtOthers.setText(mPlotFromAdapter.getOtherComments());
            plotIMage=mPlotFromAdapter.getPlotImage();
            edtPlotOwner.setText(mPlotFromAdapter.getOwner());
            if (!mPlotFromAdapter.getWifi().equals("none")){
                switchWifi.setChecked(true);
                checkBoxWifiFree.setVisibility(View.VISIBLE);
                checkBoxWifiPaid.setVisibility(View.VISIBLE);
                if (mPlotFromAdapter.getWifi().equals("free")){
                    checkBoxWifiFree.setChecked(true);
                }
                else {
                    checkBoxWifiPaid.setChecked(true);
                }
            }
            if (!mPlotFromAdapter.getWater().equals("none")){
                switchWater.setChecked(true);
                checkBoxWaterFree.setVisibility(View.VISIBLE);
                checkBoxWaterPaid.setVisibility(View.VISIBLE);
                if (mPlotFromAdapter.getWater().equals("free")){
                    checkBoxWaterFree.setChecked(true);
                }
                else {
                    checkBoxWaterPaid.setChecked(true);
                }
            }
            if (!mPlotFromAdapter.getElectricity().equals("none")){
                switchElectricity.setChecked(true);
                checkBoxElectricityFree.setVisibility(View.VISIBLE);
                checkBoxElectricityPaid.setVisibility(View.VISIBLE);
                if (mPlotFromAdapter.getElectricity().equals("free")){
                    checkBoxElectricityFree.setChecked(true);
                }
                else {
                    checkBoxElectricityPaid.setChecked(true);
                }
            }
        }
        checkBoxElectricityFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                   checkBoxElectricityPaid.setVisibility(View.GONE);
                    electricityType="tokens";
                }
                else {
                    checkBoxElectricityPaid.setVisibility(View.VISIBLE);
                    electricityType="none";
                }
            }
        });
        checkBoxElectricityPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    checkBoxElectricityFree.setVisibility(View.GONE);
                    electricityType="postpay";
                }
                else {
                    checkBoxElectricityFree.setVisibility(View.VISIBLE);
                    electricityType="none";
                }
            }
        });
        checkBoxWifiFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    checkBoxWifiPaid.setVisibility(View.GONE);
                    wifiType="free";
                }
                else {
                    checkBoxWifiPaid.setVisibility(View.VISIBLE);
                    wifiType="none";
                }
            }
        });
        checkBoxWifiPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    checkBoxWifiFree.setVisibility(View.GONE);
                    wifiType="paid";
                }
                else {
                    checkBoxWifiFree.setVisibility(View.VISIBLE);
                    wifiType="none";
                }
            }
        });
        checkBoxWaterFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    checkBoxWaterPaid.setVisibility(View.GONE);
                    waterType="free";
                }
                else {
                    checkBoxWaterPaid.setVisibility(View.VISIBLE);
                    waterType="none";
                }
            }
        });
        checkBoxWaterPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    checkBoxWaterFree.setVisibility(View.GONE);
                    waterType="paid";
                }
                else {
                    checkBoxWaterFree.setVisibility(View.VISIBLE);
                    waterType="none";
                }
            }
        });

        switchElectricity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxElectricityFree.setVisibility(View.VISIBLE);
                    checkBoxElectricityPaid.setVisibility(View.VISIBLE);
                } else {
                    checkBoxElectricityFree.setVisibility(View.GONE);
                    checkBoxElectricityPaid.setVisibility(View.GONE);
                    electricityType="none";
                }
            }
        });
        switchWater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxWaterFree.setVisibility(View.VISIBLE);
                    checkBoxWaterPaid.setVisibility(View.VISIBLE);
                } else {
                    checkBoxWaterFree.setVisibility(View.GONE);
                    checkBoxWaterPaid.setVisibility(View.GONE);
                    waterType="none";
                }
            }
        });
        switchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxWifiFree.setVisibility(View.VISIBLE);
                    checkBoxWifiPaid.setVisibility(View.VISIBLE);
                } else {
                    checkBoxWifiFree.setVisibility(View.GONE);
                    checkBoxWifiPaid.setVisibility(View.GONE);
                    wifiType="none";
                }
            }
        });
        //event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_house);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(plotIMage !=  null)
                {
                    mNewPlots = new Plots();
                    mNewPlots.setPlotName(edtName.getText().toString());
                    mNewPlots.setLocation(edtPlotLocation.getText().toString());
                    mNewPlots.setPhoneNo(edtPhoneNumber.getText().toString());
                    mNewPlots.setElectricity(electricityType);
                    mNewPlots.setWater(waterType);
                    mNewPlots.setWifi(wifiType);
                    mNewPlots.setOtherComments(edtOthers.getText().toString().trim());
                    mNewPlots.setOwner(edtPlotOwner.getText().toString().trim());
                    mNewPlots.setPlotImage(plotIMage);
                    if (action.equals("add")){
                        db.collection("Created plots").document(mNewPlots.getOwner()).collection("AllPlots").document(mNewPlots.getPlotName())
                                .set(mNewPlots)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                        Toast.makeText(getContext(),"Plot saved successfully",Toast.LENGTH_LONG).show();
                                        mPlots.remove(mNewPlots);
                                        mAdapter.notifyDataSetChanged();
                                        //initRecyclerView();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else {
                        deleteService(mPlotFromAdapter,mNewPlots);
                    }
                }
                else {
                    Toast.makeText(mContext,"No image selected yet. Please upload an image to continue",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void uploadImage() {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("image/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(mContext,"Image Uploaded!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    plotIMage = uri.toString();

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(mContext,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded: "+progress+"%");
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data!= null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Image Selected");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    public void onItemsClick(Plots mPlotss) {
        mPlotFromAdapter = mPlotss;
            getView().setOnCreateContextMenuListener(this);
            getView().showContextMenu();
    }
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu contextMenu, @NonNull View view, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(contextMenu, view, menuInfo);
        contextMenu.setHeaderTitle("Select an action");
        contextMenu.add(Menu.NONE, 1, 1, "View");
        contextMenu.add(Menu.NONE, 2, 2, "Update");
        contextMenu.add(Menu.NONE, 3, 3, "Delete");

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                startViewFragment();
                break;

            case 2:
                //Do stuff
                showUpdateDialog(mPlotFromAdapter);
                break;
            case 3:
                //Do stuff
                deleteItem(mPlotFromAdapter);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void startViewFragment() {
        unregisterForContextMenu(mRecyclerView);
        Bundle args = new Bundle();
        AppCompatActivity activity = (AppCompatActivity) mContext;
        Fragment fragmentStaff = new ViewHousesInPlotLandlordFragment();
        FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
        transactionStaff.replace(R.id.nav_host_fragment_landlord,fragmentStaff);
        transactionStaff.addToBackStack(null);
        args.putString("plotName",mPlotFromAdapter.getPlotName());
        args.putString("plotLocation",mPlotFromAdapter.getLocation());
        args.putString("plotDetails",mPlotFromAdapter.getOtherComments());
        args.putString("plotPhoneNo",mPlotFromAdapter.getPhoneNo());
        args.putString("plotOwner",mPlotFromAdapter.getOwner());
        fragmentStaff.setArguments(args);
        transactionStaff.commit();
    }

    private void deleteItem(Plots servTodelete) {
        db.collection("Created plots").document(servTodelete.getOwner()).collection("AllPlots").document(servTodelete.getPlotName())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(mContext, "successfully deleted!", Toast.LENGTH_LONG).show();
                        mPlots.remove(mPlotFromAdapter);
                        mAdapter.notifyDataSetChanged();
                        deleteImage(servTodelete);
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    private void showUpdateDialog(final Plots mServ) {
        showDialog("update");
    }
    private void deleteService(Plots servTodelete,Plots replacingService) {
        db.collection("Created plots").document(servTodelete.getOwner()).collection("AllPlots").document(servTodelete.getPlotName())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    deleteImage(servTodelete);
                    mNewPlots = replacingService;
                    db.collection("Created plots").document(mNewPlots.getOwner()).collection("AllPlots").document(mNewPlots.getPlotName())
                            .set(mNewPlots)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(),"Plot saved successfully",Toast.LENGTH_LONG).show();
                                    //initRecyclerView();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }
    private void deleteImage(Plots mDeleteServ) {
        FirebaseStorage mFirebaseStorage=FirebaseStorage.getInstance();
        final StorageReference imageFolder = mFirebaseStorage.getReferenceFromUrl(mDeleteServ.getPlotImage());
        imageFolder.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Image successfully deleted!", Toast.LENGTH_LONG).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Failed! "+ e, Toast.LENGTH_LONG).show();
                    }
                });
    }
}