package com.polycarp.easybooking.mainlandlord.landlordui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.polycarp.easybooking.Plots;
import com.polycarp.easybooking.R;
import com.polycarp.easybooking.mainlandlord.LandlordPlotsAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class HomeFragmentLandlord extends Fragment implements LandlordPlotsAdapter.OnItemsClickListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    LandlordPlotsAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Plots> mPlots;
    List<String> cats = new ArrayList<>();
    Context mContext;
    MaterialEditText edtName,edtPlotLocation,edtPhoneNumber,edtOthers;
    Button btnUpload, btnSelect;
    SwitchCompat switchWater,switchElectricity,switchWifi;
    CheckBox checkBoxWifiFree,checkBoxWifiPaid,checkBoxElectricityFree,checkBoxElectricityPaid,checkBoxWaterPaid,checkBoxWaterFree;
    //widgets
    private RecyclerView mRecyclerView;
    private Plots mNewPlots;
    private ProgressBar pbLoading;
    private FloatingActionButton fabAdd;
    Uri saveUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static final int PICK_IMAGE_REQUEST = 71;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String electricityType="none",waterType="none",wifiType="none";
    private String plotIMage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_landlord, container, false);
        mRecyclerView = root.findViewById(R.id.rv_landlord_plots);
        mRecyclerView.setVisibility(View.INVISIBLE);
        pbLoading = root.findViewById(R.id.progressBarLandlordPlots);
        fabAdd=root.findViewById(R.id.fabAddPlot);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
//        spCategories=root.findViewById(R.id.spinnerCategories);
        mContext= getActivity();
        getPlots();
        return root;
    }
    private void getPlots(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("AllPlots").whereEqualTo("owner",mUser.getEmail()).get()
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
    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new Plot");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_plot_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtPlotName);
        edtPlotLocation = add_menu_layout.findViewById(R.id.edtPlotLocation);
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
                    mNewPlots.setOwner(mUser.getEmail());
                    mNewPlots.setPlotImage(plotIMage);
                    db.collection("Created plots").document(mUser.getEmail()).collection("AllPlots").document(mNewPlots.getPlotName())
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
    public void onItemsClick(int position) {

    }
}