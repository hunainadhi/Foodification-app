package com.example.foodification;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class UserInfoFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_info, container, false);

        // Find buttons by their IDs
        Button btnSettings = view.findViewById(R.id.btnSettings);
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);
        Button btnFavorites = view.findViewById(R.id.btnFavorites);
        Button btnAccountInfo = view.findViewById(R.id.btnAccountInfo);
        Button btnHelp = view.findViewById(R.id.btnHelp);
        Button btnSignOut = view.findViewById(R.id.btnSignOut);

        // Set click listeners for each button
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Settings button click
                // Example: Open SettingsFragment
                // getActivity().getSupportFragmentManager().beginTransaction()
                //     .replace(R.id.fragmentContainer, new SettingsFragment())
                //     .addToBackStack(null)
                //     .commit();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Edit Profile button click
                // Example: Open EditProfileFragment
                // getActivity().getSupportFragmentManager().beginTransaction()
                //     .replace(R.id.fragmentContainer, new EditProfileFragment())
                //     .addToBackStack(null)
                //     .commit();
            }
        });

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Favorites button click
                // Example: Open FavoritesFragment
                // getActivity().getSupportFragmentManager().beginTransaction()
                //     .replace(R.id.fragmentContainer, new FavoritesFragment())
                //     .addToBackStack(null)
                //     .commit();
            }
        });

        btnAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Account Information button click
                // Example: Open AccountInfoFragment
                // getActivity().getSupportFragmentManager().beginTransaction()
                //     .replace(R.id.fragmentContainer, new AccountInfoFragment())
                //     .addToBackStack(null)
                //     .commit();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Help button click
                // Example: Open HelpFragment
                // getActivity().getSupportFragmentManager().beginTransaction()
                //     .replace(R.id.fragmentContainer, new HelpFragment())
                //     .addToBackStack(null)
                //     .commit();
            }
        });

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
        Intent intent = new Intent(requireActivity(), FirstActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        mAuth.signOut();

        // Redirect back to the sign-in activity (Assuming SignInFragment is your sign-in fragment)
        // Finish the current activity to prevent going back to it
        requireActivity().finish();
    }
}

