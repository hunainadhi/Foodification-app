package com.example.foodification;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfoFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    TextView btnSignOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_info, container, false);

        // Find buttons by their IDs
        btnSignOut = view.findViewById(R.id.btnSignOut);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fetchAndDisplayUserName(view);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Sign Out button click
                // Example: Sign out the user and navigate to FirstActivity
                signOutUser();
            }
        });

        return view;
    }

    private void signOutUser() {

        mAuth.signOut();
        Intent intent = new Intent(requireActivity(), FirstActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Redirect back to the sign-in activity (Assuming SignInFragment is your sign-in fragment)
        // Finish the current activity to prevent going back to it
        requireActivity().finish();
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
                        TextView userNameTextView = view.findViewById(R.id.nameText);
                        userNameTextView.setText(userName);
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
}

