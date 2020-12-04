package com.derich.hama.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.derich.hama.CreatedRequests;
import com.derich.hama.Plots;
import com.derich.hama.R;
import com.derich.hama.mainlandlord.LandlordPlotsAdapter;
import com.derich.hama.mainlandlord.landlordui.ViewHousesInPlotLandlordFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class OwnerFragment extends Fragment implements LandlordPlotsAdapter.OnItemsClickListener{

    private ImageView imgNoContent;
    private TextView tvNoContent;
    List<Plots> mPlots;
    private Button btnRequest;
    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MaterialEditText edtPhoneNumber;
    Context mContext;
    LandlordPlotsAdapter mAdapter;
    private RecyclerView rvPlots;
    private Plots mPlotFromAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_owner, container, false);
        imgNoContent = root.findViewById(R.id.imageViewNoContent);
        tvNoContent = root.findViewById(R.id.textView_post_content);
        btnRequest = root.findViewById(R.id.buttonRequestPostPlots);
        rvPlots = root.findViewById(R.id.recyclerview_my_plots);
        btnRequest.setOnClickListener(view -> showRequestDialog());
        mContext= getActivity();
        checkIfUserHasPlots();
        return root;
    }

    private void checkIfUserHasPlots() {
        if (mUser!=null){
        db.collectionGroup("AllPlots").whereEqualTo("owner",mUser.getEmail()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mPlots = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                            mPlots.add(snapshot.toObject(Plots.class));
                        initRecyclerView();
                        imgNoContent.setVisibility(View.GONE);
                        tvNoContent.setVisibility(View.GONE);
                        btnRequest.setVisibility(View.GONE);
                        rvPlots.setVisibility(View.VISIBLE);
                    } else {
                        imgNoContent.setVisibility(View.VISIBLE);
                        tvNoContent.setVisibility(View.VISIBLE);
                        btnRequest.setVisibility(View.VISIBLE);
                        rvPlots.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.d("OwnerFragment","Error " + e);
                    }
                });
    }
        else {
            imgNoContent.setVisibility(View.VISIBLE);
            tvNoContent.setVisibility(View.VISIBLE);
            btnRequest.setVisibility(View.VISIBLE);
            rvPlots.setVisibility(View.GONE);
        }
    }
    private void initRecyclerView(){
        mAdapter = new LandlordPlotsAdapter(mPlots,this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rvPlots.setLayoutManager(layoutManager);
        rvPlots.setAdapter(mAdapter);
        rvPlots.setVisibility(View.VISIBLE);
    }
    private void showRequestDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Request to advertise");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_request_layout,null);

        edtPhoneNumber = add_menu_layout.findViewById(R.id.edtNewRequestPhone);

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.logo);
        alertDialog.setPositiveButton("YES", (dialogInterface, i) -> {
            if (edtPhoneNumber != null) {
                CreatedRequests createdRequests=new CreatedRequests(edtPhoneNumber.getText().toString(),mUser.getEmail());
                db.collection("AdminRequests").document("requests").collection("postrequests").document(mUser.getEmail())
                        .set(createdRequests)
                        .addOnSuccessListener(aVoid -> {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                            Toast.makeText(getContext(), "Thank you for the request. We will get back to you shortly.", Toast.LENGTH_LONG).show();
                            //initRecyclerView();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Not saved. Try again later.", Toast.LENGTH_LONG).show());
            } else {
                Toast.makeText(mContext, "No image selected yet. Please upload an image to continue", Toast.LENGTH_LONG).show();
            }
            dialogInterface.dismiss();
        });

        alertDialog.setNegativeButton("NO", (dialog, i) -> dialog.dismiss());
        alertDialog.show();
    }

    @Override
    public void onItemsClick(Plots mPlotss) {
        mPlotFromAdapter = mPlotss;
        startViewFragment();
    }

    private void startViewFragment() {
        Bundle args = new Bundle();
        AppCompatActivity activity = (AppCompatActivity) mContext;
        Fragment fragmentStaff = new ViewHousesInPlotLandlordFragment();
        FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
        transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
        transactionStaff.addToBackStack(null);
        args.putString("plotName",mPlotFromAdapter.getPlotName());
        args.putString("plotLocation",mPlotFromAdapter.getLocation());
        args.putString("plotDetails",mPlotFromAdapter.getOtherComments());
        args.putString("plotPhoneNo",mPlotFromAdapter.getPhoneNo());
        args.putString("plotOwner",mPlotFromAdapter.getOwner());
        fragmentStaff.setArguments(args);
        transactionStaff.commit();
    }
}