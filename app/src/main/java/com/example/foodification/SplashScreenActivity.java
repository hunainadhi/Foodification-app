package com.example.foodification;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in, redirect to HomeActivity
            startActivity(new Intent(SplashScreenActivity.this, BaseActivity.class));
        } else {
            // User is signed out, redirect to FirstActivity (or SignInActivity)
            startActivity(new Intent(SplashScreenActivity.this, FirstActivity.class));
        }

        // Finish the SplashScreenActivity to prevent going back to it
        finish();
    }
}
