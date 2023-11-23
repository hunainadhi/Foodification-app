package com.example.foodification;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = view.findViewById(R.id.editTextEmail);
        passwordEditText = view.findViewById(R.id.editTextPassword);
        signUpButton = view.findViewById(R.id.buttonSignUp);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sign-up was successful; the user is created
                                // You may want to handle additional user profile setup here
                                Log.d("SignUp","Success");
                                Toast.makeText(getActivity().getApplicationContext(), "Account has been created successfully", Toast.LENGTH_SHORT).show();
                                SignIn signInFragment = new SignIn();
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, signInFragment)
                                        .commit();
                            } else {
                                // Sign-up failed; display an error message to the user
                                Log.d("SignUp","Fail");
                            }
                        });
            }
        });

        return view;
    }
}
