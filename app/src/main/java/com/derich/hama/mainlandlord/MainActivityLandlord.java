package com.derich.hama.mainlandlord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.derich.hama.LoginActivity;
import com.derich.hama.R;
import com.squareup.picasso.Picasso;

public class MainActivityLandlord extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private Menu menu;
    private FirebaseUser mUser;
    private View mHeaderView;
    private TextView tvUsername,tvEmail;
    private ImageView imgUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_landlord);
        Toolbar toolbar = findViewById(R.id.toolbarLandlord);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_landlord);
        NavigationView navigationView = findViewById(R.id.nav_view_landlord);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.nav_bookings,R.id.nav_landlord_houses,R.id.nav_add_house,R.id.nav_viewHousesInPlotLandlordFragment)
                .setDrawerLayout(drawer)
                .build();
        checkLogIn();
        mHeaderView=navigationView.getHeaderView(0);
        tvUsername=mHeaderView.findViewById(R.id.drawer_main_username);
        tvEmail=mHeaderView.findViewById(R.id.drawer_main_email);
        imgUser=mHeaderView.findViewById(R.id.imageView_main_drawer);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_landlord);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (mUser!=null){
            tvEmail.setText(mUser.getEmail());
            tvUsername.setText(mUser.getDisplayName());
            //imgUser.setImageURI(mUser.getPhotoUrl());
            Picasso.with(this).load(mUser.getPhotoUrl()).into(imgUser);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                if (mUser!=null){
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_logout));
                    signOut();
                }
                else {
                    Intent intent= new Intent(MainActivityLandlord.this,LoginActivity.class);
                    startActivity(intent);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent= new Intent(MainActivityLandlord.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_landlord);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private void checkLogIn() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {

        } else {
            Intent intentLogin= new Intent(MainActivityLandlord.this,LoginActivity.class);
            startActivity(intentLogin);
        }
    }
}