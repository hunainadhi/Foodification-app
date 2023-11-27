package com.example.foodification;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Call the method to fetch and display the user's name
        fetchAndDisplayUserName(view);

        View invView = view.findViewById(R.id.rectangle_8);
        invView.setOnClickListener(v -> {
            // Replace startActivity with fragment transaction
            replaceFragment(new InventoryFragment());
        });

        View RecView = view.findViewById(R.id.rectangle_9);
        RecView.setOnClickListener(v -> {
            // Replace startActivity with fragment transaction
            replaceFragment(new RecipePageFragment());
        });

        View ExpView = view.findViewById(R.id.rectangle_4);
        ExpView.setOnClickListener(v -> {
            // Replace startActivity with fragment transaction
            replaceFragment(new RecipePageFragment());
        });

        View GrocView = view.findViewById(R.id.rectangle_1);
        GrocView.setOnClickListener(v -> {
            // Replace startActivity with fragment transaction
            replaceFragment(new InventoryFragment());
        });

        return view;
    }

    private void fetchAndDisplayUserName(View view) {
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
                        TextView userNameTextView = view.findViewById(R.id.welcome);
                        userNameTextView.setText("Hi, " + userName + "!");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(getActivity(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void replaceFragment(Fragment fragment) {
        // Replace the current fragment with the new one
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, fragment); // Use your fragment container ID
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


