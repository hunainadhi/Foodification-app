package com.example.foodification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, databaseReference;
    private String email, safeEmail;
    private StringBuilder ingredientsStringBuilder;
    private String allIngredientNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userEmail = preferences.getString("global_variable_key", "default_value");

        FirebaseApp.initializeApp(this);

        safeEmail = userEmail.replace('.', ',')
                .replace('#', '-')
                .replace('$', '+')
                .replace('[', '(')
                .replace(']', ')');
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(safeEmail)
                .child("ingredients");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ingredientsStringBuilder = new StringBuilder();
                    for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                        String ingredientName = ingredientSnapshot.child("name").getValue(String.class);
                        if (ingredientName != null) {
                            ingredientsStringBuilder.append(ingredientName).append(", ");
                        }
                    }

                    allIngredientNames = ingredientsStringBuilder.toString().replaceAll(", $", "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        fetchAndDisplayUserName();
        View invView = findViewById(R.id.rectangle_8);
        invView.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, Inventory.class);
            intent.putExtra("USER_EMAIL", userEmail); // Use userEmail directly
            startActivity(intent);
        });

        View RecView = findViewById(R.id.rectangle_9);
        RecView.setOnClickListener(view -> {
            fetchRecipesAndStartRecipePage();
        });

        View ExpView = findViewById(R.id.rectangle_4);
        ExpView.setOnClickListener(view -> {
            fetchRecipesAndStartRecipePage();
        });

        View GrocView = findViewById(R.id.rectangle_1);
        GrocView.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, Inventory.class);
            startActivity(intent);
        });
    }

    private void fetchRecipesAndStartRecipePage() {
        String ingredients = allIngredientNames;
        String apiUrl = "https://api.spoonacular.com/recipes/findByIngredients";
        String apiKey = "42b956e445e84aa08770373b20991b9e"; // Replace with your Spoonacular API key

        String url = apiUrl + "?ingredients=" + ingredients + "&apiKey=" + apiKey;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<Recipe> recipes = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject recipeObject = response.getJSONObject(i);
                                String id = recipeObject.getString("id");
                                String name = recipeObject.getString("title");
                                String image = recipeObject.getString("image");

                                // Create a Recipe object and add it to the list
                                Recipe recipe = new Recipe(id, image, name, "", "", "", "");
                                recipes.add(recipe);
                            }

                            // Convert the list of recipes to JSON
                            String recipesJson = new Gson().toJson(recipes);

                            Intent intent = new Intent(HomeActivity.this, RecipePage.class);
                            intent.putExtra("RECIPES", recipesJson);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        // Add the request to the RequestQueue
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void fetchAndDisplayUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userRef = mDatabase.child("users").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);

                        TextView userNameTextView = findViewById(R.id.welcome);
                        userNameTextView.setText("Hi, " + userName + "!");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    Toast.makeText(HomeActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
