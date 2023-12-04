package com.example.foodification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, databaseReference;
    private String email, safeEmail;
    private StringBuilder ingredientsStringBuilder;
    private String allIngredientNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String userEmail = preferences.getString("global_variable_key", "default_value");

        FirebaseApp.initializeApp(getContext());

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
            fetchRecipesAndStartRecipePage();
            //replaceFragment(new RecipePageFragment());
        });

        View ExpView = view.findViewById(R.id.rectangle_4);
        ExpView.setOnClickListener(v -> {
            // Replace startActivity with fragment transaction
            fetchRecipesAndStartRecipePage();
            //(new RecipePageFragment());
        });

        View GrocView = view.findViewById(R.id.rectangle_1);
        GrocView.setOnClickListener(v -> {
            // Replace startActivity with fragment transaction
            replaceFragment(new GroceryFragment());
        });

        return view;
    }
    private void fetchRecipesAndStartRecipePage() {
        String ingredients = allIngredientNames;
        String apiUrl = "https://api.spoonacular.com/recipes/findByIngredients";
        String apiKey = "438f4a62633645fd893cddefb2f2df00"; // Replace with your Spoonacular API key

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

                                JSONArray missedIngredients = recipeObject.getJSONArray("missedIngredients");
                                List<Ingredient> missedIngredientsList = new ArrayList<>();
                                for (int j = 0; j < missedIngredients.length(); j++) {
                                    JSONObject ingredientObject = missedIngredients.getJSONObject(j);
                                    Ingredient ingredients = new Ingredient();
                                    ingredients.setId(ingredientObject.getString("id"));
                                    ingredients.setName(ingredientObject.getString("name"));
                                    ingredients.setUnit(ingredientObject.getString("unit"));
                                    ingredients.setAmount(ingredientObject.getString("amount"));
                                    missedIngredientsList.add(ingredients);
                                }

                                int totalIngredients = recipeObject.getInt("usedIngredientCount") + missedIngredients.length();
                                double missedPercentage = totalIngredients == 0 ? 0 : (double) missedIngredients.length() / totalIngredients * 100;
                                String missedPer = String.valueOf(missedPercentage).substring(0,4);
                                String missedCount = String.valueOf(missedIngredients.length());


                                // Create a Recipe object and add it to the list
                                Recipe recipe = new Recipe(id, image, name, missedPer,missedCount, "", "",missedIngredientsList);
                                recipes.add(recipe);
                            }

                            // Convert the list of recipes to JSON
                            String recipesJson = new Gson().toJson(recipes);

                            replaceFragment(new RecipePageFragment(recipesJson));
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
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
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


