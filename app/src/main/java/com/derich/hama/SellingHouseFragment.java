package com.derich.hama;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.derich.hama.ui.home.HousesContainers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class SellingHouseFragment extends Fragment {

    private static final String TAG = "House fragment";
    //widgets
    //    private TextView mTitle;
    private TextView houseName,location,mDetails,mNewPrice;
    private CardView cvOffers;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnAddPics;
    private static final int PICK_IMAGE = 1997;
    private ProgressDialog progressDialog1;
    private Uri ImageUri;
    ArrayList imageList = new ArrayList();
    private int upload_count = 0;
    ArrayList urlStrings;
    public OfferDetails mProduct;
    private Button chooserBtn;
    private String houseOwner;
    private String houseDetails;
    private String newPrice;
    private String prevPrice;
    private List<HousePics> mHousePics;
    private String loc;
    private String name;
    private String section;
    private PagerAdapter pagerAdapter;
    private AutoScrollViewPager vpPics;
    private TabLayout mTabLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selling_house, container, false);
        mContext=getContext();
//        mTitle = view.findViewById(R.id.title);
        houseName = view.findViewById(R.id.offerHouseName);
        mTabLayout= view.findViewById(R.id.tab_layout_house);
        location = view.findViewById(R.id.offerLocation);
        mDetails = view.findViewById(R.id.offerDetails);
        mNewPrice = view.findViewById(R.id.offerPrice);
        vpPics = view.findViewById(R.id.house_product_container);
        btnAddPics=view.findViewById(R.id.btnAddSellingPics);
        btnAddPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPicsDialog();
            }
        });
        getIncomingIntent();
        getHousePics();
        return view;
    }

    private void getHousePics() {
        db.collection("AllHousesOnSale").document(name + houseOwner).collection("AllSellingHousePics").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mHousePics = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                                mHousePics.add(snapshot.toObject(HousePics.class));
                            }
                        } else {
//                            Toast.makeText(mContext, "No house photos added yet. photos you add will appear here", Toast.LENGTH_LONG).show();
                        }
                        initPagerAdapter();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("HouseInfo", "error " + e);
                    }
                });
    }
    private void initPagerAdapter(){
        PagerAdapter pagerAdapter = new com.derich.hama.ScreenSlidePagerAdapter(getParentFragmentManager(), mHousePics);
        vpPics.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(vpPics);
        // start auto scroll
        vpPics.startAutoScroll();
        // set auto scroll time in mili
        vpPics.setInterval(3000);
        // enable recycling using true
        vpPics.setCycle(true);
    }
    private void addPicsDialog() {
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
                                        storeLink(urlStrings);
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
    private void storeLink(ArrayList urlStrings) {
        for (int i = 0; i <urlStrings.size() ; i++) {
            HousePics housePics = new HousePics(urlStrings.get(i).toString(),name,houseOwner,loc);
            db.collection("AllHousesOnSale").document(name + houseOwner).collection("AllSellingHousePics").document(name+" "+ i).set(housePics)
                    .addOnSuccessListener(aVoid -> Toast.makeText(mContext,"Image added successfully",Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.w("HouseInfo", "error " + e);
                    });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == PICK_IMAGE) {
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

    private void getIncomingIntent() {
        Bundle bundle=getArguments();
        if (bundle!=null){
            name = bundle.getString("houseName");
            houseName.setText(name);
            loc = bundle.getString("houseLocation");
            location.setText(loc);
            prevPrice = bundle.getString("prevPrice");
            newPrice = bundle.getString("newPrice");
            Locale locale = new Locale("en","KE");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            mNewPrice.setText(fmt.format(Integer.valueOf(newPrice)));
            houseDetails = bundle.getString("houseDetails");
            mDetails.setText(houseDetails);
            houseOwner = bundle.getString("houseOwner");
            section = bundle.getString("section");
            if (section.equals("admin")){
            btnAddPics.setVisibility(View.VISIBLE);
            }
            else {
                btnAddPics.setVisibility(View.GONE);
            }

        }
    }


}