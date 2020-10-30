package com.polycarp.easybooking.mainlandlord;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.polycarp.easybooking.R;
import com.polycarp.easybooking.ui.home.HousesContainers;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class LandLordHousesAdapter extends RecyclerView.Adapter<LandLordHousesAdapter.ViewHolder>{
    Context mContext;
    List<HousesContainers> mHouses;
    private Bitmap bmp;
    private LandLordHousesAdapter.ViewHolder holder1;
    private LandLordHousesAdapter.OnItemsClickListener onItemsClickListener;
    private int pos;
    private MaterialEditText edtRent;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MaterialEditText edtHouseNumber;
    private SwitchCompat switchDeposit;
    private String deposit;
    private MaterialEditText edtDeposit;
    private String type;

    public LandLordHousesAdapter(List<HousesContainers> mHouses, LandLordHousesAdapter.OnItemsClickListener onItemsClickListener) {
        this.mHouses = mHouses;
        this.onItemsClickListener = onItemsClickListener;
    }

    @NonNull
    @Override
    public LandLordHousesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_houses,parent,false);
        mContext = parent.getContext();
        return new LandLordHousesAdapter.ViewHolder(view,onItemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LandLordHousesAdapter.ViewHolder holder, final int position) {
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(mHouses.get(position).getRent()));
        holder.tvName.setText(mHouses.get(position).getPlotName());
        holder.tvLocation.setText(mHouses.get(position).getLocation());
        holder.tvPrice.setText("Rent: "+fmt.format(price));
        holder.tvDescription.setText(mHouses.get(position).getDetails());
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(mHouses.get(position).getHouseImage())
                .into(holder.imgCategory);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               pos = position;
                view.showContextMenu();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHouses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener{
        private TextView tvName,tvLocation,tvDescription,tvPrice;
        private ImageView imgCategory;
        private CardView mainLayout;
        LandLordHousesAdapter.OnItemsClickListener onItemsClickListener;
        public ViewHolder(@NonNull View itemView, LandLordHousesAdapter.OnItemsClickListener onItemsClickListener) {
            super(itemView);
            this.onItemsClickListener=onItemsClickListener;
            tvName=itemView.findViewById(R.id.titleMain);
            tvPrice=itemView.findViewById(R.id.priceMain);
            tvLocation=itemView.findViewById(R.id.locationMain);
            tvDescription=itemView.findViewById(R.id.descriptionMain);
            imgCategory=itemView.findViewById(R.id.imageHouse);
            mainLayout=itemView.findViewById(R.id.card_view_houses);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
//        itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onClick(View view) {
            onItemsClickListener.onItemsClick(getAdapterPosition());
        }
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select an action");
            MenuItem Update = contextMenu.add(Menu.NONE, 1, 1, "Update");
            MenuItem Delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");
            Update.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

    }
    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case 0:
                    //Do stuff
                    Toast.makeText(mContext, "View", Toast.LENGTH_LONG).show();
//                    AddImageDialog();
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
                        notifyDataSetChanged();
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
        switchDeposit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtDeposit.setVisibility(View.VISIBLE);
                } else {
                    edtDeposit.setVisibility(View.GONE);
                    deposit = "none";
                }
            }
        });
        //event for button
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_house);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(edtRent.getText().toString() !=  null)
                {
                   HousesContainers mNewHouses=new HousesContainers(edtRent.getText().toString().trim(),housesContainers.getLocation(),edtDeposit.getText().toString().trim(),housesContainers.getDetails(),spCategories.getSelectedItem().toString(),housesContainers.getPhoneNo(),housesContainers.getPlotName(),housesContainers.getHouseImage(),housesContainers.getOwner(),edtHouseNumber.getText().toString().trim(),0);
                    db.collection(housesContainers.getPlotName()).document(housesContainers.getOwner()).collection("AllHouses").document(housesContainers.getPlotName())
                            .set(mNewHouses)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(mContext,"House saved successfully",Toast.LENGTH_LONG).show();
                                    //initRecyclerView();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
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

    public interface OnItemsClickListener{
        void onItemsClick(int position);
    }
}
