package com.example.foodification;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

public class FirstActivity extends AppCompatActivity {

    private Button toggleFragmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        toggleFragmentButton = findViewById(R.id.toggleFragmentButton);

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
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
