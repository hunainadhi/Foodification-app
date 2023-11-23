package com.example.foodification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignIn extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private TextView forgotPasswordTextView;
    private CheckBox rememberMeCheckBox;
    private static final String PREFS_NAME = "RememberMePrefs";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PASSWORD = "password";

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = view.findViewById(R.id.editTextEmail);
        passwordEditText = view.findViewById(R.id.editTextPassword);
        signInButton = view.findViewById(R.id.buttonSignIn);
        forgotPasswordTextView = view.findViewById(R.id.textForgotPassword);
        TextView yourTextView = view.findViewById(R.id.textView);
        rememberMeCheckBox = view.findViewById(R.id.checkboxRememberMe);
        loadRememberMeData();

        // Replace these colors with your desired colors
        int startColor = 0xFF01AED8;
        int endColor = 0xFF8BCF7B;

        // Create a SpannableString with a gradient
        String text = yourTextView.getText().toString();
        SpannableString spannable = new SpannableString(text);

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

        // Set the SpannableString to the TextView
        yourTextView.setText(spannable);


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
                if (isValidEmail(email) && isValidPassword(password)) {
                    signInUser(email, password);
                } else {
                    // Display an error message or handle validation failure
                    Toast.makeText(getContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String forgotEmail = emailEditText.getText().toString().trim();
                if(isValidEmail(forgotEmail)) {
                    sendPasswordResetEmail(forgotEmail);
                }
                else{
                    Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_SHORT).show();
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
    private void signInUser(String email, String password) {
        // Perform Firebase Authentication sign-in
        // Check if the user exists in Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d("SignIn","Success");
                        Intent myIntent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                        getActivity().startActivity(myIntent);
                        // Sign-in was successful; do something with the user
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("global_variable_key", email);
                        editor.apply();
                    } else {
                        // Sign-in failed; display an error message to the user
                        passwordEditText.setError("Invalid email or password.");
                        passwordEditText.requestFocus();
                    }
                });
        if (rememberMeCheckBox.isChecked() && !isRememberMeDataSaved()) {
            saveRememberMeData(email);
        }
    }
    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadRememberMeData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedEmail = prefs.getString(PREF_EMAIL, "");

        // Populate the UI with saved data if available
        if (!savedEmail.isEmpty()) {
            emailEditText.setText(savedEmail);
            rememberMeCheckBox.setChecked(true);
        }
    }
    private void saveRememberMeData(String email) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_EMAIL, email);
        editor.apply();
    }
    private boolean isRememberMeDataSaved() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.contains(PREF_EMAIL) && prefs.contains(PREF_PASSWORD);
    }

}
