package com.derich.hama.mainlandlord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

import com.derich.hama.ui.AccountFragment;
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
                 R.id.nav_bookings,R.id.nav_landlord_houses,R.id.nav_add_house,R.id.nav_viewHousesInPlotLandlordFragment,R.id.nav_house_info, R.id.nav_viewProductFragment2,R.id.nav_sellingHouseFragment2)
                .setDrawerLayout(drawer)
                .build();
        checkLogIn();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_landlord);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ic_account) {
            Bundle args = new Bundle();
            AppCompatActivity activity = (AppCompatActivity) this;
            Fragment fragmentStaff = new AccountFragment();
            FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
            transactionStaff.replace(R.id.nav_host_fragment_landlord, fragmentStaff);
            transactionStaff.addToBackStack(null);
            fragmentStaff.setArguments(args);
            transactionStaff.commit();
        }
        return super.onOptionsItemSelected(item);
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