package com.derich.hama.ui.bookings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.derich.hama.HouseBooking;
import com.derich.hama.R;

import java.util.ArrayList;
import java.util.List;

public class BookingsFragment extends Fragment implements BookingAdapterNormal.OnItemsClickListener{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<HouseBooking> mBookings;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private BookingAdapterNormal mAdapter;
    Context mContext;
    private RecyclerView mRecyclerview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_normal_bookings, container, false);
        mRecyclerview = root.findViewById(R.id.rv_bookings_normal);
        mContext= getActivity();
        getBookings();
        return root;
    }

    private void getBookings() {
        db.collectionGroup("AllBookings").whereEqualTo("username",mUser.getEmail()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mBookings = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                            mBookings.add(snapshot.toObject(HouseBooking.class));
                    } else {
                        Toast.makeText(mContext, "No Bookings found", Toast.LENGTH_LONG).show();
                    }
                    initRecyclerView();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    Log.d("ViewHousesFragment","Error " + e);
                });
    }

    private void initRecyclerView(){
        mAdapter = new BookingAdapterNormal(mBookings,this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemsClick(int position) {

    }
}