package com.example.foodification;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    // UI elements
    private TextView recipeTitle;
    private TextView recipeIngredients;
    private TextView recipeInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize the UI components
        recipeTitle = findViewById(R.id.detail_recipe_title);
        recipeIngredients = findViewById(R.id.detail_recipe_ingredients);
        recipeInstructions = findViewById(R.id.detail_recipe_instructions);

        // Get the intent that started this activity, and extract the recipe object
        Intent intent = getIntent();
        Recipe selectedRecipe = (Recipe) intent.getSerializableExtra("RecipeData"); // Make sure Recipe class implements Serializable

        // If the recipe object is not null, use its data to populate the UI elements
        if (selectedRecipe != null) {
            recipeTitle.setText(selectedRecipe.getName()); // This assumes a 'getName' method in your Recipe class
            recipeIngredients.setText(selectedRecipe.getIngredients()); // This assumes a 'getIngredients' method in your Recipe class
            recipeInstructions.setText(selectedRecipe.getInstructions()); // This assumes a 'getInstructions' method in your Recipe class
        }
    }
}
