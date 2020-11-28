package com.derich.hama;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.derich.hama.mainlandlord.MainActivityLandlord;
import com.derich.hama.ui.AccountFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Menu menu;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private View mHeaderView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserDetails> mUserr;
    private TextView tvUsername,tvEmail;
    private ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_search, R.id.nav_favorite, R.id.nav_owner, R.id.nav_help,R.id.nav_sellingHouseFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        checkUser();
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
        switch (item.getItemId()){
            case R.id.ic_account:
                Bundle args = new Bundle();
                AppCompatActivity activity = (AppCompatActivity) this;
                Fragment fragmentStaff = new AccountFragment();
                FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
                transactionStaff.addToBackStack(null);
                fragmentStaff.setArguments(args);
                transactionStaff.commit();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }
    private void checkUser() {
        if (mUser!=null) {
            mUserr = new ArrayList<>();
            db.collectionGroup("registeredUsers").whereEqualTo("username", mUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            mUserr.add(snapshot.toObject(UserDetails.class));
                        }
                        int size = mUserr.size();
                        int position;
                        if (size == 1) {
                            position = 0;
                            UserDetails userDetails = mUserr.get(position);
                            String namee = userDetails.getUsername();
                            String section = userDetails.getSection();
                            if (section.equals("landlord")) {
                                Intent intent = new Intent(MainActivity.this, MainActivityLandlord.class);
                                startActivity(intent);
                            } else if (section.equals("simpleUser")) {
//                            Intent intentMpesa=new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intentMpesa);
                            } else {
                                Toast.makeText(MainActivity.this, "Error validating details. Please login again", Toast.LENGTH_LONG).show();
                                Intent intentMpesa = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intentMpesa);
                            }
                        }

                    } else {
                        String username = mUser.getEmail();
                        String section = "simpleUser";
                        UserDetails newUser = new UserDetails(username, section);
                        Toast.makeText(MainActivity.this, "No data found.", Toast.LENGTH_LONG).show();
                        db.collection("users").document("all users").collection("registeredUsers").document(mUser.getEmail())
                                .set(newUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "User added successfully", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Not saved. Try again later.", Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                            Log.d("LoginAct", "Error" + e);
                        }
                    });
        }
        else {
            Intent intentMpesa = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentMpesa);
        }
    }
}