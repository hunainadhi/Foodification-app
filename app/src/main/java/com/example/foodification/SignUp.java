package com.example.foodification;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUp extends Fragment {

    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private DatabaseReference mDatabase;


    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        nameEditText = view.findViewById(R.id.editTextFullName);
        emailEditText = view.findViewById(R.id.editTextEmail);
        passwordEditText = view.findViewById(R.id.editTextPassword);
        signUpButton = view.findViewById(R.id.buttonSignUp);
        TextView yourTextView = view.findViewById(R.id.textView);
        TextView yourSubTextView = view.findViewById(R.id.subTextView1);

        // Replace these colors with your desired colors
        int startColor = 0xFF01AED8;
        int endColor = 0xFF8BCF7B;

        // Create a SpannableString with a gradient
        String text = yourTextView.getText().toString();
        SpannableString spannable = new SpannableString(text);
        String subText = yourSubTextView.getText().toString();
        SpannableString subSpannable = new SpannableString(subText);

        // Apply the gradient color to the text
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, yourTextView.getTextSize(), startColor, endColor, Shader.TileMode.CLAMP);
        ForegroundColorSpan gradientColorSpan = new ForegroundColorSpan(startColor) {
            @Override
            public void updateDrawState(TextPaint tp) {
                super.updateDrawState(tp);
                tp.setShader(linearGradient);
            }
        };

        spannable.setSpan(gradientColorSpan, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        subSpannable.setSpan(gradientColorSpan, 0, subText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the SpannableString to the TextView
        yourTextView.setText(spannable);
        yourSubTextView.setText(subSpannable);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (name.isEmpty()) {
                    nameEditText.setError("Name is required.");
                    nameEditText.requestFocus();
                    return;
                }

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
                if (isValidEmail(email) && isValidPassword(password)) {
                    signUpUser(name,email, password);
                } else {
                    // Display an error message or handle validation failure
                    Toast.makeText(getContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    private boolean isValidEmail(String email) {
        // Basic email validation using regex
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return Pattern.matches(emailPattern, email);
    }

    private boolean isValidPassword(String password) {
        // Password validation: at least 6 characters
        return password.length() >= 6;
    }
    private void signUpUser(String name,String email, String password) {
        // Perform Firebase Authentication sign-up
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Store user information in Firebase Realtime Database
                        if (user != null) {
                            String userId = user.getUid();
                            storeUserData(userId, name, email);
                        }
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
    private void storeUserData(String userId, String name, String email) {
        // Reference to the "users" node in Realtime Database
        DatabaseReference userRef = mDatabase.child(userId);

        // Create a Map to store user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);

        // Set the user data in Realtime Database
        userRef.setValue(userData);
    }
}
