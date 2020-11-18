package com.derich.hama;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.derich.hama.ui.home.HousesContainers;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class HouseInfoFragment extends Fragment implements View.OnClickListener{

    private static final int NUM_COLUMNS = 2;

    //vars
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> cats = new ArrayList<>();
    List<String> mAllOrders = new ArrayList<>();
    Context mContext;
    RelativeLayout mainL;
    private FirebaseUser mUser;
    //widgets
    private RelativeLayout mCart;
    private ViewPager vpOffers;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar pbOrders;
    private TextView tvLocation,tvDate,tvStatus,tvOrderId;
    private RecyclerView rvViewOrder;
    private String orderID,location,date;
    private int status;
    private String owner,houseImage,houseNumber,plotName,phoneNo,type;
    private String details;
    private String deposit;
    private String rent;
    private List<HousePics> mHousePics;
    private PagerAdapter pagerAdapter;
    private TextView tvPlotName,tvRent,tvDeposit,tvType,tvNumber,tvDescription,booked_text;
    private RelativeLayout btn_book;
    private MaterialEditText edtName;
    private List<HousesContainers> mNewHouses;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_house_info, container, false);
        tvPlotName = root.findViewById(R.id.tv_plot_name_house_info);
        tvLocation = root.findViewById(R.id.tv_plot_location_house_info);
        tvStatus = root.findViewById(R.id.tv_plot_status_house_info);
        tvRent = root.findViewById(R.id.tv_plot_rent_house_info);
        tvDeposit = root.findViewById(R.id.tv_plot_deposit_house_info);
        tvType = root.findViewById(R.id.tv_plot_type_house_info);
        booked_text = root.findViewById(R.id.text_add_to_cart);
        tvNumber = root.findViewById(R.id.tv_plot_owner_no_house_info);
        vpOffers=root.findViewById(R.id.pictures_container);
        btn_book = root.findViewById(R.id.add_to_cart);
        tvDescription = root.findViewById(R.id.tv_plot_description_house_info);
        btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBookingDialog();
            }
        });

//    if (mOrders.get(position).getStatus()==0){
//        tvStatus.setText("Pending");
//    }
//    else {
//        tvStatus.setText("Approved");
//    }
//        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layoutViewOrders);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mContext= getActivity();
        getIncomingIntent();
        checkStatus();
        getHousePhotos();
        return root;
    }

    private void checkStatus() {
        db.collectionGroup("AllBookings").whereEqualTo("houseNo",houseNumber).whereEqualTo("plotName",plotName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            booked_text.setText("House already Booked");
                            btn_book.setClickable(false);

                        } else {
                            booked_text.setText("Book house");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.d("ViewHousesFragment","Error " + e);
                });
    }

    private void showBookingDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new Product");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View add_menu_layout = inflater.inflate(R.layout.add_new_item_layout,null);

        edtName = add_menu_layout.findViewById(R.id.book_house_your_number);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_house);
        alertDialog.setPositiveButton("YES", (dialog, i) -> {
            dialog.dismiss();
            if(edtName.getText().toString() !=  null)
            {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.US);
                String formattedDate = sdf.format(c);
                HouseBooking houseBooking=new HouseBooking(houseNumber,owner,mUser.getEmail(),formattedDate,edtName.getText().toString().trim(),plotName);
                db.collection("Bookings").document(mUser.getEmail()).collection("AllBookings").document(plotName+houseNumber)
                        .set(houseBooking)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                Toast.makeText(mContext,"House booked successfully",Toast.LENGTH_LONG).show();
                                booked_text.setText("Booked");
                                btn_book.setClickable(false);
                                HousesContainers mNewHouses=new HousesContainers(rent,location,deposit,details,type,phoneNo,plotName,houseImage,owner,houseNumber,1);
                                db.collection(plotName).document(owner).collection("AllHouses").document(plotName+houseNumber)
                                        .set(mNewHouses)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                                Toast.makeText(mContext,"House booked successfully",Toast.LENGTH_LONG).show();
                                                booked_text.setText("Booked");
                                                btn_book.setClickable(false);


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                            }
                        });
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

    private void getIncomingIntent() {
        if (getArguments().getString("houseImage")!=null) {
            rent = getArguments().getString("rent");
            tvRent.setText(rent);
            location = getArguments().getString("location");
            tvLocation.setText(location);
            status = getArguments().getInt("status");
            if (status==0){
                tvStatus.setText("Available");
            }
            else {
                tvStatus.setText("Occupied");
            }
            deposit = getArguments().getString("deposit");
            tvDeposit.setText(deposit);
            details = getArguments().getString("details");
            tvDescription.setText(details);
            type = getArguments().getString("type");
            tvType.setText(type);
            phoneNo = getArguments().getString("phoneNo");
            tvNumber.setText(phoneNo);
            plotName = getArguments().getString("plotName");
            tvPlotName.setText(plotName);
            houseImage = getArguments().getString("houseImage");
            houseNumber = getArguments().getString("houseNumber");
//            RequestOptions requestOptions = new RequestOptions()
//                    .placeholder(R.drawable.ic_launcher_background);
//            Glide.with(mContext)
//                    .setDefaultRequestOptions(requestOptions)
//                    .load(houseImage)
//                    .into(img);
            owner = getArguments().getString("owner");

        }
        else {
            houseNumber=getArguments().getString("houseNo");
            owner=getArguments().getString("ownerName");
            plotName=getArguments().getString("plotName");
            db.collectionGroup("AllHouses").whereEqualTo("houseNumber",houseNumber).whereEqualTo("owner",owner).whereEqualTo("plotName",plotName)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            mNewHouses = new ArrayList<>();
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                    mNewHouses.add(snapshot.toObject(HousesContainers.class));
                                populateTextViews();
                            } else {
                                Toast.makeText(mContext, "House not found.", Toast.LENGTH_LONG).show();
                            }

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("HomeFragmentLandlord","Error " + e);
                    });
        }
    }

    private void populateTextViews() {
        rent = mNewHouses.get(0).getRent();
        tvRent.setText(rent);
        location = mNewHouses.get(0).getLocation();
        tvLocation.setText(location);
        status = mNewHouses.get(0).getStatus();
        if (status==0){
            tvStatus.setText("Available");
        }
        else {
            tvStatus.setText("Unavailable");
        }
        deposit = mNewHouses.get(0).getDeposit();
        tvDeposit.setText(deposit);
        details = mNewHouses.get(0).getDetails();
        tvDescription.setText(details);
        type = mNewHouses.get(0).getType();
        tvType.setText(type);
        phoneNo = mNewHouses.get(0).getPhoneNo();
        tvNumber.setText(phoneNo);
        plotName = mNewHouses.get(0).getPlotName();
        tvPlotName.setText(plotName);
        houseImage = mNewHouses.get(0).getHouseImage();
        houseNumber = mNewHouses.get(0).getHouseNumber();
//            RequestOptions requestOptions = new RequestOptions()
//                    .placeholder(R.drawable.ic_launcher_background);
//            Glide.with(mContext)
//                    .setDefaultRequestOptions(requestOptions)
//                    .load(houseImage)
//                    .into(img);
        owner = mNewHouses.get(0).getOwner();
    }

    private void getHousePhotos(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        //mProducts.addAll(Arrays.asList(Products.FEATURED_PRODUCTS));
        db.collectionGroup("housePhotos").whereEqualTo("owner",owner).whereEqualTo("plotName",plotName).whereEqualTo("houseNumber",houseNumber).get()
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
                        mHousePics.add(new HousePics(houseImage));
                        initViewPager();
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

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cart:{
                //open Cart Activity
//                Intent intent = new Intent(view.getContext(), ViewCartActivity.class);
//                startActivity(intent);
                break;
            }
        }
    }
    private void initViewPager() {
        pagerAdapter = new ScreenSlidePagerAdapter(getParentFragmentManager(),mHousePics);
        vpOffers.setAdapter(pagerAdapter);
        pageSwitcher(5);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(
                    vpOffers.getContext(), new AccelerateInterpolator());
            // scroller.setFixedDuration(5000);
            mScroller.set(vpOffers, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }
    public class HesitateInterpolator implements Interpolator {
        public HesitateInterpolator() {
        }

        public float getInterpolation(float t) {
            float x = 2.0f * t - 1.0f;
            return 0.5f * (x * x * x + 1.0f);
        }
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<HousePics> mFragments;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<HousePics> fragments) {
            super(fm);
            mFragments = fragments;
        }
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new HousePicsFragment(mFragments.get(position).getPic());
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
    Timer timer;
    int page = 1;

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
        // in
        // milliseconds
    }

    // this is an inner class...
    class RemindTask extends TimerTask {

        @Override
        public void run() {
if (getActivity()!=null){
    getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {

            if (page > mHousePics.size()) { // In my case the number of pages are 5
                timer.cancel();
                // Showing a toast for just testing purpose
            } else {
                vpOffers.setCurrentItem(page++);
            }
        }
    });
}
            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.



        }
    }
    public class FixedSpeedScroller extends Scroller {

        private int mDuration = 500;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator,
                                  boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy,
                                int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
}