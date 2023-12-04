package com.example.foodification;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecipeDetailFragment extends Fragment {

    // UI elements
    private TextView recipeTitle;
    private TextView recipeIngredients;
    private TextView recipeInstructions, recipeEquipment;
    private Button missingIngredientsButton;
    private String email, safeEmail;

    private DatabaseReference mDatabase;


    private FirebaseAuth mAuth;

    private DatabaseReference databaseReference;
    private RecipeDetail recipeDetail;
    private Recipe recipe;

    // Constructor for creating a new instance of the fragment with RecipeDetail
    public static RecipeDetailFragment newInstance(RecipeDetail recipeDetail, Recipe recipe) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("RecipeDetailData", recipeDetail);
        args.putSerializable("Recipe", recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recipe_detail, container, false);

        // Initialize the UI components
        recipeTitle = view.findViewById(R.id.detail_recipe_title);
        recipeIngredients = view.findViewById(R.id.detail_recipe_ingredients);
        recipeInstructions = view.findViewById(R.id.detail_recipe_instructions);
        recipeEquipment= view.findViewById(R.id.detail_recipe_equipment);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        email = preferences.getString("global_variable_key", "default_value");
        FirebaseApp.initializeApp(requireContext());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("favouriteRecipes");
        safeEmail = email.replace('.', ',')
                .replace('#', '-')
                .replace('$', '+')
                .replace('[', '(')
                .replace(']', ')');
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(safeEmail)
                .child("grocery");


        // Extract the recipe detail object from the arguments
        if (getArguments() != null) {
             recipeDetail = (RecipeDetail) getArguments().getSerializable("RecipeDetailData");
             recipe = (Recipe) getArguments().getSerializable("Recipe");

            // Populate the UI elements with the recipe detail data
            if (recipeDetail != null) {
                recipeTitle.setText(recipeDetail.getName());
                recipeIngredients.setText(formatIngredients(recipeDetail.getSteps()));
                recipeEquipment.setText(formatEquipment(recipeDetail.getSteps()));
                recipeInstructions.setText(formatInstructions(recipeDetail.getSteps()));
            }
        }
        Button favButton = view.findViewById(R.id.favButton);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click
                String recipesString = new Gson().toJson(recipe);
                if(recipesString!=null) {
                    saveToFavourites(recipesString);
                }
            }
        });
        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });
         missingIngredientsButton= view.findViewById(R.id.addMissedIngredients);
        missingIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMissingIngredientsToGrocery(recipeDetail.getMissingIngredients());
            }
        });
        return view;
    }

    private void saveToFavourites(String recipesString) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        DatabaseReference userRef = mDatabase.child(userId).child("recipesJson");

        // Read the current value from the database
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the value exists
                if (dataSnapshot.exists()) {
                    // Value exists, append the new value
                    String currentRecipesJson = dataSnapshot.getValue(String.class);
                    String newRecipesJson = currentRecipesJson + ","+recipesString;
                    userRef.setValue(newRecipesJson);
                } else {
                    // Value doesn't exist, set the new value directly
                    userRef.setValue(recipesString);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("FirebaseAppend", "Error reading data", databaseError.toException());
            }
        });
    }

    private String formatInstructions(List<RecipeStep> steps) {
        StringBuilder instructionsBuilder = new StringBuilder();
        for (RecipeStep step : steps) {
            Log.i("RecipeDetailFragment","step"+step.getStep());
            instructionsBuilder.append(step.getNumber())
                    .append(". ")
                    .append(step.getStep())
                    .append("\n\n");
        }
        return instructionsBuilder.toString();
    }
    private String formatIngredients(List<RecipeStep> steps) {
        Set<String> uniqueIngredients = new LinkedHashSet<>(); // Preserve the insertion order
        for (RecipeStep step : steps) {
            for (Ingredients ingredient : step.getIngredients()) {
                uniqueIngredients.add(ingredient.getName());
            }
        }
        // Prepend "• " to the joined string if not empty
        return uniqueIngredients.isEmpty() ? "" : "• " + TextUtils.join("\n• ", uniqueIngredients);
    }
    private void addMissingIngredientsToGrocery(List<Ingredient> missedIngredients) {
            for (Ingredient missingIngredient : missedIngredients) {
                String name = missingIngredient.getName();
                Log.i("RecipeDetailFragment","name: "+name);
                String quantityStr = missingIngredient.getAmount();
                String unit = missingIngredient.getUnit();


                if (!name.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty() && !unit.equalsIgnoreCase("Select Unit")) {
                    double quantity = Double.parseDouble(quantityStr);

                    String groceryId = databaseReference.push().getKey();
                    Grocery grocery = new Grocery(groceryId, name, quantity, unit,false);

                    if (groceryId != null) {
                        databaseReference.child(groceryId).setValue(grocery);
                        Toast.makeText(requireContext(), "Grocery added!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    }
    private String formatEquipment(List<RecipeStep> steps) {
        Set<String> uniqueEquipment = new LinkedHashSet<>(); // Preserve the insertion order
        for (RecipeStep step : steps) {
            for (Equipment equip : step.getEquipment()) {
                uniqueEquipment.add(equip.getName());
            }
        }
        // Prepend "• " to the joined string if not empty
        return uniqueEquipment.isEmpty() ? "" : "• " + TextUtils.join("\n• ", uniqueEquipment);
    }
}
