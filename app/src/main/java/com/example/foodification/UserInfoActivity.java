package com.example.foodification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class UserInfoActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Find buttons by their IDs
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        Button btnFavorites = findViewById(R.id.btnFavorites);
        Button btnAccountInfo = findViewById(R.id.btnAccountInfo);
        Button btnHelp = findViewById(R.id.btnHelp);
        Button btnSignOut = findViewById(R.id.btnSignOut);

        // Set click listeners for each button
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Settings button click
                // Example: Open SettingsActivity
                //startActivity(new Intent(UserActivity.this, SettingsActivity.class));
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Edit Profile button click
                // Example: Open EditProfileActivity
                //startActivity(new Intent(UserActivity.this, EditProfileActivity.class));
            }
        });

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Favorites button click
                // Example: Open FavoritesActivity
                //startActivity(new Intent(UserActivity.this, FavoritesActivity.class));
            }
        });

        btnAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Account Information button click
                // Example: Open AccountInfoActivity
                //startActivity(new Intent(UserActivity.this, AccountInfoActivity.class));
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Help button click
                // Example: Open HelpActivity
                //startActivity(new Intent(UserActivity.this, HelpActivity.class));
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Sign Out button click
                // Example: Sign out the user and navigate to LoginActivity
                signOutUser();
            }
        });
    }

    private void signOutUser() {
        Intent intent = new Intent(UserInfoActivity.this, FirstActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        mAuth.signOut();

        // Redirect back to the sign-in activity (Assuming SignInActivity is your sign-in activity)
         // Finish the current activity to prevent going back to it
    }
}
