package com.example.foodification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {
    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_fragment);
        nav = findViewById(R.id.bottomNavigationView);
        HomeFragment homeFragment = new HomeFragment();
        addFragment(homeFragment);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle item clicks
                if (item.getItemId() == R.id.account) {
                    // Replace startActivity with fragment transaction
                    replaceFragment(new UserInfoFragment());
                    return true;
                }
                else if (item.getItemId() == R.id.person) {
                    // Replace startActivity with fragment transaction
                    replaceFragment(new HomeFragment());
                    return true;
                }
                else if (item.getItemId() == R.id.settings) {
                    // Replace startActivity with fragment transaction
                    replaceFragment(new InventoryFragment());
                    return true;
                }
                else if (item.getItemId() == R.id.home) {
                    // Replace startActivity with fragment transaction
                    replaceFragment(new RecipePageFragment());
                    return true;
                }
                return false;
            }
        });
    }
    public void addFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, fragment);
        transaction.commit();
    }
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, fragment);
        transaction.commit();
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            // The back stack is empty, finish the activity
            finish();
        } else {
            // The back stack is not empty, perform the default back action
            super.onBackPressed();
        }
    }
}