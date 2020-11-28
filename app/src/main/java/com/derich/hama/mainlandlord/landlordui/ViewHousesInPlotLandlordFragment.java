package com.derich.hama.mainlandlord.landlordui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.derich.hama.HouseInfoFragment;
import com.derich.hama.HousePics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.derich.hama.R;
import com.derich.hama.mainlandlord.LandLordHousesAdapter;
import com.derich.hama.ui.home.HousesContainers;
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
    private static final int PICK_IMAGE = 1997;
    LandLordHousesAdapter mAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<HousesContainers> mHouses;
    Context mContext;
    MaterialEditText edtHouseNumber,edtRent,edtDeposit;
    Button btnUpload, btnSelect;
    SwitchCompat switchDeposit;
    //widgets
    private RecyclerView mRecyclerView;
    private HousesContainers mNewHouses;
    private ProgressBar pbLoading;
    private FloatingActionButton fabAdd;
    private Uri ImageUri;
    ArrayList imageList = new ArrayList();
    private int upload_count = 0;
    private ProgressDialog progressDialog;
    ArrayList urlStrings;
    Uri saveUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static final int PICK_IMAGE_REQUEST = 71;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String plotIMage,deposit="none",plotName,plotLocation,plotPhoneNo,owner;
    private Spinner spCategories;
    private String type;
    private View root;
    private int pos;
    private HousesContainers mHousesFromAdapter;
    private Button chooserBtn;
    private ProgressDialog progressDialog1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_view_houses_in_plot_landlord, container, false);
        mRecyclerView = root.findViewById(R.id.rv_landlord_houses_in_plots);
        mRecyclerView.setVisibility(View.INVISIBLE);
        registerForContextMenu(mRecyclerView);
        pbLoading = root.findViewById(R.id.progressBarLandlord_houses_in_Plots);
        fabAdd= root.findViewById(R.id.fabAdd_houses_in_Plot);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fabAdd.setOnClickListener(view -> showDialog("add"));
        if (getArguments()!=null) {
            plotName = getArguments().getString("plotName");
            plotLocation = getArguments().getString("plotLocation");
            plotPhoneNo = getArguments().getString("plotPhoneNo");
            owner = getArguments().getString("plotOwner");

        }
//        registerForContextMenu(mRecyclerView);
//        mRecyclerView.setOnCreateContextMenuListener(this);
        mRecyclerView.setOnClickListener(view -> {
            view.showContextMenu();
            Toast.makeText(mContext,"clickedd",Toast.LENGTH_LONG).show();
        });
//        spCategories=root.findViewById(R.id.spinnerCategories);
        mContext= getActivity();
        getPlots();
        return root;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu contextMenu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Select an action");
        contextMenu.add(Menu.NONE, 5, 5, "View");
        contextMenu.add(Menu.NONE, 6, 6, "Update");
        contextMenu.add(Menu.NONE, 7, 7, "Delete");
        contextMenu.add(Menu.NONE, 8, 8, "Add house images");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 5:
                startViewFragment();
                break;

            case 6:
                //Do stuff
//                showUpdateDialog(mHousesFromAdapter);
                showDialog("update");
                break;
            case 7:
                //Do stuff
                deleteItem(mHousesFromAdapter);
                break;
            case 8:
                //Do stuff
                addHouseImages(mHousesFromAdapter);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void addHouseImages(HousesContainers mHA) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add more House images");
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View add_menu_layout = inflater.inflate(R.layout.add_images_layout,null);
        chooserBtn = add_menu_layout.findViewById(R.id.btnHousePicSelect);
        chooserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        progressDialog1 = new ProgressDialog(mContext);
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_house);
        alertDialog.setPositiveButton("YES", (dialog, i) -> {
            urlStrings = new ArrayList<>();
            progressDialog1.show();
            StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ImageFolder");

            for (upload_count = 0; upload_count < imageList.size(); upload_count++) {

                Uri IndividualImage = (Uri) imageList.get(upload_count);
                final StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());

                ImageName.putFile(IndividualImage).addOnSuccessListener(
                        taskSnapshot -> ImageName.getDownloadUrl().addOnSuccessListener(
                                uri -> {
                                    urlStrings.add(String.valueOf(uri));



                                    if (urlStrings.size() == imageList.size()){
                                        storeLink(urlStrings,mHA);
                                    }

                                }
                        )
                );


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

    private void storeLink(ArrayList urlStrings,HousesContainers mHa) {
        for (int i = 0; i <urlStrings.size() ; i++) {
            HousePics housePics = new HousePics(urlStrings.get(i).toString(),mHa.getPlotName(),mHa.getOwner(),mHa.getHouseNumber());
            db.collection(mHa.getPlotName()).document(mHa.getOwner()).collection("AllPhotos").document(mHa.getPlotName() + mHa.getHouseNumber() + i).set(housePics)
                    .addOnSuccessListener(aVoid -> Toast.makeText(mContext,"Image added successfully",Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("HouseInfo", "error " + e);
                    });
        }

    }

    private void startViewFragment() {
        Bundle args = new Bundle();
        AppCompatActivity activity = (AppCompatActivity) mContext;
        Fragment fragmentStaff = new HouseInfoFragment();
        FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
        transactionStaff.replace(R.id.nav_host_fragment_landlord,fragmentStaff);
        transactionStaff.addToBackStack(null);
        args.putString("houseNo",mHousesFromAdapter.getHouseNumber());
        args.putString("ownerName",mHousesFromAdapter.getOwner());
        args.putString("plotName",mHousesFromAdapter.getPlotName());
        fragmentStaff.setArguments(args);
        transactionStaff.commit();
    }

    private void getPlots(){
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("AllHouses").whereEqualTo("owner",owner).whereEqualTo("plotName",plotName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mHouses = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                            mHouses.add(snapshot.toObject(HousesContainers.class));
                    } else {
                        Toast.makeText(mContext, "No houses found. Please add a new house in this plot", Toast.LENGTH_LONG).show();
                    }
                    initRecyclerView();
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
    private void showDialog(String action) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

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
        switchDeposit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtDeposit.setVisibility(View.VISIBLE);
                edtDeposit.setText("");
            } else {
                edtDeposit.setVisibility(View.GONE);
                edtDeposit.setText("none");
            }
        });
        //event for button
        btnSelect.setOnClickListener(v -> chooseImage());

        btnUpload.setOnClickListener(v -> uploadImage());
        if (action.equals("update")){
            alertDialog.setTitle("Update House details");
            edtRent.setText(mHousesFromAdapter.getRent());
            type=mHousesFromAdapter.getType();
            plotIMage=mHousesFromAdapter.getHouseImage();
            spCategories.setSelection(((ArrayAdapter)spCategories.getAdapter()).getPosition(mHousesFromAdapter.getType()));
            edtHouseNumber.setText(mHousesFromAdapter.getHouseNumber());
            if (mHousesFromAdapter.getDeposit().equals("none")){
                switchDeposit.setChecked(false);
                edtDeposit.setVisibility(View.GONE);
                edtDeposit.setText("none");
            }
            else {
                switchDeposit.setChecked(true);
                edtDeposit.setVisibility(View.VISIBLE);
                edtDeposit.setText(mHousesFromAdapter.getDeposit());
            }
        }
        else {
            alertDialog.setTitle("Add new House");
        }
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_house);
        alertDialog.setPositiveButton("YES", (dialog, i) -> {
            if(plotIMage !=  null)
            {
                mNewHouses = new HousesContainers();
                mNewHouses.setPlotName(plotName);
                mNewHouses.setLocation(plotLocation);
                mNewHouses.setPhoneNo(plotPhoneNo);
                mNewHouses.setRent(edtRent.getText().toString().trim());
                mNewHouses.setDeposit(edtDeposit.getText().toString().trim());
                mNewHouses.setType(type);
                mNewHouses.setHouseImage(plotIMage);
                mNewHouses.setOwner(owner);
                mNewHouses.setHouseNumber(edtHouseNumber.getText().toString().trim());
                if (action.equals("add")){
                    db.collection(plotName).document(owner).collection("AllHouses").document(plotName + edtHouseNumber.getText().toString().trim())
                            .set(mNewHouses)
                            .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                Toast.makeText(getContext(),"House saved successfully",Toast.LENGTH_LONG).show();
                                mHouses.remove(mNewHouses);
                                mAdapter.notifyDataSetChanged();
                                //initRecyclerView();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show());
                }
                else {
                    deleteServices(mHousesFromAdapter,mNewHouses);
                }
            }
            else {
                Toast.makeText(mContext,"No image selected yet. Please upload an image to continue",Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });

        alertDialog.setNegativeButton("NO", (dialog, i) -> dialog.dismiss());
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
                    .addOnSuccessListener(taskSnapshot -> {
                        mDialog.dismiss();
                        Toast.makeText(mContext,"Image Uploaded!", Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                plotIMage = uri.toString();

                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        mDialog.dismiss();
                        Toast.makeText(mContext,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mDialog.setMessage("Uploaded: "+progress+"%");
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
        else if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {


                if (data.getClipData() != null) {

                    int countClipData = data.getClipData().getItemCount();
                    int currentImageSlect = 0;

                    while (currentImageSlect < countClipData) {

                        ImageUri = data.getClipData().getItemAt(currentImageSlect).getUri();
                        imageList.add(ImageUri);
                        currentImageSlect = currentImageSlect + 1;
                    }
                    chooserBtn.setText(currentImageSlect +" Images selected");


                } else {
                    Toast.makeText(getContext(), "Please Select Multiple Images", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    public void onItemsClick(HousesContainers mHousez) {
        mHousesFromAdapter = mHousez;
        getView().setOnCreateContextMenuListener(this);
        getView().showContextMenu();
    }


    private void deleteItem(HousesContainers housesContainers) {
        db.collection(housesContainers.getPlotName()).document(housesContainers.getOwner()).collection("AllHouses").document(housesContainers.getPlotName()+housesContainers.getHouseNumber())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(mContext, "successfully deleted!", Toast.LENGTH_LONG).show();
                        mHouses.remove(housesContainers);
                        mAdapter.notifyDataSetChanged();
                        deleteImage(housesContainers);
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    private void deleteImage(HousesContainers housesContainers) {
        FirebaseStorage mFirebaseStorage=FirebaseStorage.getInstance();
        final StorageReference imageFolder = mFirebaseStorage.getReferenceFromUrl(housesContainers.getHouseImage());
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

    private void deleteServices(HousesContainers housesContainers, HousesContainers mNewHouses) {
        db.collection(housesContainers.getPlotName()).document(housesContainers.getOwner()).collection("AllHouses").document(housesContainers.getPlotName()+housesContainers.getHouseNumber())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    db.collection(mNewHouses.getPlotName()).document(mNewHouses.getOwner()).collection("AllHouses").document(mNewHouses.getPlotName() + mNewHouses.getHouseNumber())
                            .set(mNewHouses)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(),"House updated successfully",Toast.LENGTH_LONG).show();
                                    deleteImage(housesContainers);
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
}