package com.example.foodification;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class FirstActivity extends AppCompatActivity {

    private TextView toggleFragmentButton;
    private TextView toggleFragmentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        toggleFragmentButton = findViewById(R.id.toggleFragmentButton);
        toggleFragmentText = findViewById(R.id.toggleFragmentText);
        if (savedInstanceState == null) {
            // Show the SignIn by default
            SignIn signInFragment = new SignIn();
            addFragment(signInFragment);

            // Initially, display the "Sign Up" button
            toggleFragmentButton.setText("Sign Up");
            toggleFragmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Switch to the SignUpFragment when the button is clicked
                    if (toggleFragmentButton.getText().toString().equals("Sign Up")) {
                        SignUp signUpFragment = new SignUp();
                        replaceFragment(signUpFragment);
                        toggleFragmentButton.setText("Sign In");
                        toggleFragmentText.setText("Already have an account?");
                    } else {
                        SignIn signInFragment = new SignIn();
                        replaceFragment(signInFragment);
                        toggleFragmentButton.setText("Sign Up");
                    }
                }
            });
        }
    }

    public void addFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
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
