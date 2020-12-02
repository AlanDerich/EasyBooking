package com.derich.hama.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.derich.hama.OfferDetails;
import com.derich.hama.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class OwnerFragment extends Fragment {

    private ImageView imgNoContent;
    private TextView tvNoContent;
    private Button btnRequest;
    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_owner, container, false);
        imgNoContent = root.findViewById(R.id.imageViewNoContent);
        tvNoContent = root.findViewById(R.id.textView_post_content);
        btnRequest = root.findViewById(R.id.buttonRequestPostPlots);
        btnRequest.setOnClickListener(view -> showRequestDialog());
        return root;
    }

    private void showRequestDialog() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//        alertDialog.setTitle("Request to advertise");
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View add_menu_layout = inflater.inflate(R.layout.add_new_request_layout,null);
//
//        edtOfferName = add_menu_layout.findViewById(R.id.edtOfferName);
//
//        alertDialog.setView(add_menu_layout);
//        alertDialog.setIcon(R.drawable.logo);
//        alertDialog.setPositiveButton("YES", (dialogInterface, i) -> {
//            if (plotIMage != null) {
//                OfferDetails mNewOffer = new OfferDetails(plotIMage, edtOfferName.getText().toString(), edtOfferNewPrice.getText().toString().toLowerCase(), edtOfferLocation.getText().toString().toLowerCase(), edtOfferDetails.getText().toString().toLowerCase(), edtOfferOwner.getText().toString().toLowerCase());
//                db.collection("AllHousesOnSale").document(mNewOffer.getOfferName() + mNewOffer.getOwner())
//                        .set(mNewOffer)
//                        .addOnSuccessListener(aVoid -> {
////                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
//                            Toast.makeText(getContext(), "House saved successfully", Toast.LENGTH_LONG).show();
//                            //initRecyclerView();
//                            getOffers();
//                        })
//                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Not saved. Try again later.", Toast.LENGTH_LONG).show());
//            } else {
//                Toast.makeText(mContext, "No image selected yet. Please upload an image to continue", Toast.LENGTH_LONG).show();
//            }
//            dialogInterface.dismiss();
//        });
//
//        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                dialog.dismiss();
//            }
//        });
//        alertDialog.show();
    }
}