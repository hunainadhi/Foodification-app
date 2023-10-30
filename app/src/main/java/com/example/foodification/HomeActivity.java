package com.example.foodification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        View invVIew = findViewById(R.id.rectangle_8);
        invVIew.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, Inventory.class);
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
    }
}