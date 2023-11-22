package com.example.foodification;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = view.findViewById(R.id.editTextEmail);
        passwordEditText = view.findViewById(R.id.editTextPassword);
        signInButton = view.findViewById(R.id.buttonSignIn);

        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();

                // Input validation: Check if email and password are not empty
                if (email.isEmpty()) {
                    emailEditText.setError("Email is required.");
                    emailEditText.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required.");
                    passwordEditText.requestFocus();
                    return;
                }

                // Check if the user exists in Firebase
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d("SignIn","Success");
                                Intent myIntent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                                getActivity().startActivity(myIntent);

                                myIntent.putExtra("USER_EMAIL", email);
                                startActivity(myIntent);

                                // Sign-in was successful; do something with the user
                            } else {
                                // Sign-in failed; display an error message to the user
                                passwordEditText.setError("Invalid email or password.");
                                passwordEditText.requestFocus();
                            }
                        });
            }
        });

        return view;
    }
}
