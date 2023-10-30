package com.example.foodification;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecipePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipepage);

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list first
        recipeAdapter = new RecipeAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(recipeAdapter);

        // Prepare data for the RecyclerView
        prepareRecipeData(); // Now your adapter is initialized before this call
    }


    // This method creates a list of static recipes
    @SuppressLint("NotifyDataSetChanged")
    private void prepareRecipeData() {
        // Creating a static list of recipes with dummy data
        Recipe recipe1 = new Recipe("Spaghetti Carbonara", "10 mins", "30 mins",
                "Spaghetti, eggs, bacon, Parmesan cheese, garlic, salt, pepper",
                "Cook pasta, fry bacon until crisp, beat eggs with cheese and season, combine all and serve hot.");

        Recipe recipe2 = new Recipe("Classic Chicken Salad", "15 mins", "15 mins",
                "Chicken, lettuce, boiled eggs, mayonnaise, mustard, salt, pepper",
                "Mix chopped boiled chicken with other ingredients, lay on a bed of lettuce, serve chilled.");

        Recipe recipe3 = new Recipe("Chocolate Chip Cookies", "15 mins", "45 mins",
                "Flour, baking soda, butter, sugar, brown sugar, vanilla extract, eggs, chocolate chips",
                "Combine ingredients, form into balls, place on baking sheet, and bake until golden brown.");

        // You can directly update the data in your adapter.
        recipeAdapter.getRecipeList().add(recipe1);
        recipeAdapter.getRecipeList().add(recipe2);
        recipeAdapter.getRecipeList().add(recipe3);

        // Notify the adapter that the data has changed
        recipeAdapter.notifyDataSetChanged();
    }

}
