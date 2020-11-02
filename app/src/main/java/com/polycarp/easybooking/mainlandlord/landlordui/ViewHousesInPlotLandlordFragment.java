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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.polycarp.easybooking.R;
import com.polycarp.easybooking.mainlandlord.LandLordHousesAdapter;
import com.polycarp.easybooking.mainlandlord.LandlordPlotsAdapter;
import com.polycarp.easybooking.ui.home.HousesContainers;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

//fragment_view_houses_in_plot_landlord
public class ViewHousesInPlotLandlordFragment extends Fragment implements LandLordHousesAdapter.OnItemsClickListener{
    private static final int NUM_COLUMNS = 2;

    //vars
    LandLordHousesAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<HousesContainers> mHouses;
    List<String> cats = new ArrayList<>();
    Context mContext;
    MaterialEditText edtHouseNumber,edtRent,edtDeposit;
    Button btnUpload, btnSelect;
    SwitchCompat switchDeposit;
    //widgets
    private RecyclerView mRecyclerView;
    private HousesContainers mNewHouses;
    private ProgressBar pbLoading;
    private FloatingActionButton fabAdd;
    Uri saveUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static final int PICK_IMAGE_REQUEST = 71;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String plotIMage,deposit="none",plotName,plotLocation,plotDetails,plotPhoneNo,owner;
    private Spinner spCategories;
    private String type;
    private View root;
    private int pos;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_view_houses_in_plot_landlord, container, false);
        mRecyclerView = root.findViewById(R.id.rv_landlord_houses_in_plots);
        mRecyclerView.setVisibility(View.INVISIBLE);
        pbLoading = root.findViewById(R.id.progressBarLandlord_houses_in_Plots);
        fabAdd= root.findViewById(R.id.fabAdd_houses_in_Plot);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        if (getArguments()!=null) {
            plotName = getArguments().getString("plotName");
            plotLocation = getArguments().getString("plotLocation");
            plotDetails = getArguments().getString("plotDetails");
            plotPhoneNo = getArguments().getString("plotPhoneNo");
            owner = getArguments().getString("plotOwner");

        }
//        registerForContextMenu(mRecyclerView);
//        mRecyclerView.setOnCreateContextMenuListener(this);
        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.showContextMenu();
                Toast.makeText(mContext,"clickedd",Toast.LENGTH_LONG).show();
            }
        });
//        spCategories=root.findViewById(R.id.spinnerCategories);
        mContext= getActivity();
        getPlots();
        return root;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu contextMenu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Select an action");
        MenuItem viewer = contextMenu.add(Menu.NONE, 0, 0, "View");
        MenuItem Update = contextMenu.add(Menu.NONE, 1, 1, "Update");
        MenuItem Delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");
        viewer.setOnMenuItemClickListener(onEditMenu);
        Update.setOnMenuItemClickListener(onEditMenu);
        Delete.setOnMenuItemClickListener(onEditMenu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private void getPlots(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("AllHouses").whereEqualTo("owner",owner).whereEqualTo("plotName",plotName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mHouses = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mHouses.add(snapshot.toObject(HousesContainers.class));
                        } else {
                            Toast.makeText(mContext, "No houses found. Please add a new house in this plot", Toast.LENGTH_LONG).show();
                        }
                        initRecyclerView();
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
        mAdapter = new LandLordHousesAdapter(mHouses,this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), NUM_COLUMNS);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
    }
    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new House");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_house_layout,null);

        edtRent = add_menu_layout.findViewById(R.id.edtHouseRent);
        edtHouseNumber = add_menu_layout.findViewById(R.id.edtHouseNumber);
        switchDeposit = add_menu_layout.findViewById(R.id.switchDeposit);

        btnSelect = add_menu_layout.findViewById(R.id.btnHousePicSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnHousePicUpload);
        spCategories = add_menu_layout.findViewById(R.id.spinnerAllTypes);
        List<String> cats = new ArrayList<>();
        cats.add("Single room");
        cats.add("Betsitter self-contained");
        cats.add("One Bedroom");
        cats.add("Two bedroom");
        cats.add("3 Bedroom");
        ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, cats);
        spCategories.setAdapter(usersAdapter);
        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = spCategories.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        edtDeposit = add_menu_layout.findViewById(R.id.edtHouseDeposit);
        edtDeposit.setVisibility(View.GONE);
        switchDeposit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtDeposit.setVisibility(View.VISIBLE);
                } else {
                    edtDeposit.setVisibility(View.GONE);
                    deposit="none";
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
                    mNewHouses = new HousesContainers();
                    mNewHouses.setPlotName(plotName);
                    mNewHouses.setLocation(plotLocation);
                    mNewHouses.setPhoneNo(plotPhoneNo);
                    mNewHouses.setRent(edtRent.getText().toString().trim());
                    mNewHouses.setDeposit(edtDeposit.getText().toString().trim());
                    mNewHouses.setDetails(plotDetails);
                    mNewHouses.setType(type);
                    mNewHouses.setHouseImage(plotIMage);
                    mNewHouses.setOwner(owner);
                    mNewHouses.setHouseNumber(edtHouseNumber.getText().toString().trim());
                    db.collection(plotName).document(mUser.getEmail()).collection("AllHouses").document(plotName + edtHouseNumber.getText().toString().trim())
                            .set(mNewHouses)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(),"House saved successfully",Toast.LENGTH_LONG).show();
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
        pos = position;
    }
    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case 0:
                    //Do stuff
                    Toast.makeText(mContext, "View", Toast.LENGTH_LONG).show();
                    break;

                case 1:
                    //Do stuff
                    showUpdateDialog(mHouses.get(pos));
                    Toast.makeText(mContext, "Update", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    //Do stuff
                    deleteItem(mHouses.get(pos));
                    break;
            }
            return true;
        }
    };

    private void deleteItem(HousesContainers housesContainers) {
        db.collection(housesContainers.getPlotName()).document(housesContainers.getOwner()).collection("AllHouses").document(housesContainers.getPlotName())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(mContext, "successfully deleted!", Toast.LENGTH_LONG).show();
                        mHouses.remove(pos);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void showUpdateDialog(final HousesContainers housesContainers) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new House");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View add_menu_layout = inflater.inflate(R.layout.add_new_house_layout,null);

        edtRent = add_menu_layout.findViewById(R.id.edtHouseRent);
        edtRent.setText(housesContainers.getRent());
        edtHouseNumber = add_menu_layout.findViewById(R.id.edtHouseNumber);
        edtHouseNumber.setText(housesContainers.getHouseNumber());
        edtHouseNumber.setText(housesContainers.getHouseNumber());
        switchDeposit = add_menu_layout.findViewById(R.id.switchDeposit);
        edtDeposit = add_menu_layout.findViewById(R.id.edtHouseDeposit);
        if (housesContainers.getDeposit().equals("none")){
            switchDeposit.setChecked(false);
        }
        else {
            switchDeposit.setChecked(true);
            edtDeposit.setText(housesContainers.getDeposit());
        }

        Button btnSelect = add_menu_layout.findViewById(R.id.btnHousePicSelect);
        Button btnUpload = add_menu_layout.findViewById(R.id.btnHousePicUpload);
        btnSelect.setVisibility(View.GONE);
        btnUpload.setVisibility(View.GONE);
        final Spinner spCategories = add_menu_layout.findViewById(R.id.spinnerAllTypes);
        List<String> cats = new ArrayList<>();
        cats.add("Single room");
        cats.add("Betsitter self-contained");
        cats.add("One Bedroom");
        cats.add("Two bedroom");
        cats.add("3 Bedroom");
        ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(
                mContext, android.R.layout.simple_spinner_item, cats);
        spCategories.setAdapter(usersAdapter);
        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = spCategories.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        switchDeposit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtDeposit.setVisibility(View.VISIBLE);
            } else {
                edtDeposit.setVisibility(View.GONE);
                deposit = "none";
            }
        });
        //event for button
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_house);
        alertDialog.setPositiveButton("YES", (dialog, i) -> {
            if(edtRent.getText().toString() !=  null)
            {
                HousesContainers mNewHouses=new HousesContainers(edtRent.getText().toString().trim(),housesContainers.getLocation(),edtDeposit.getText().toString().trim(),housesContainers.getDetails(),spCategories.getSelectedItem().toString(),housesContainers.getPhoneNo(),housesContainers.getPlotName(),housesContainers.getHouseImage(),housesContainers.getOwner(),edtHouseNumber.getText().toString().trim(),0);
                db.collection(housesContainers.getPlotName()).document(housesContainers.getOwner()).collection("AllHouses").document(housesContainers.getPlotName() + housesContainers.getHouseNumber())
                        .set(mNewHouses)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                Toast.makeText(mContext,"House updated successfully",Toast.LENGTH_LONG).show();
                                //initRecyclerView();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show());
            }
            else {
                Toast.makeText(mContext,"No image selected yet. Please upload an image to continue",Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}