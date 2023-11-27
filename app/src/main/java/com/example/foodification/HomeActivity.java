package com.example.foodification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    BottomNavigationView nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        nav = findViewById(R.id.bottomNavigationView);

        // Call the method to fetch and display the user's name
        fetchAndDisplayUserName();
        View invVIew = findViewById(R.id.rectangle_8);
        invVIew.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, Inventory.class);
            String userEmail = getIntent().getStringExtra("USER_EMAIL");
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });
        View RecView = findViewById(R.id.rectangle_9);
        RecView.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, RecipePage.class);
            startActivity(intent);
        });

        View ExpView = findViewById(R.id.rectangle_4);
        ExpView.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, RecipePage.class);
            startActivity(intent);
        });

        View GrocView = findViewById(R.id.rectangle_1);
        ExpView.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, Inventory.class);
            startActivity(intent);
        });
        nav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Handle item clicks
                        if (item.getItemId()==R.id.account) {
                                // Handle the Settings item click
                                startActivity(new Intent(HomeActivity.this, UserInfoActivity.class));
                                return true;
                        }
                        return false;
                    }
                });
    }
    private void fetchAndDisplayUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userRef = mDatabase.child("users").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Assuming you have a "name" field in your user node
                        String userName = dataSnapshot.child("name").getValue(String.class);

                        // Now you have the user's name, you can use it as needed
                        // For example, display it in a TextView
                        TextView userNameTextView = findViewById(R.id.welcome);
                        userNameTextView.setText("Hi, " + userName + "!");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(HomeActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}