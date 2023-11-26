package com.example.foodification;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipepage);

        recyclerView = findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeAdapter = new RecipeAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(recipeAdapter);

        fetchRecipesFromIntent();
    }

    private void fetchRecipesFromIntent() {
        String recipesJson = getIntent().getStringExtra("RECIPES");
        if (recipesJson != null) {
            Type listType = new TypeToken<List<Recipe>>(){}.getType();
            recipeList = new Gson().fromJson(recipesJson, listType);
            updateRecipeData();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecipeData() {
        // Clear the adapter and add the updated recipe list
        recipeAdapter.setRecipeList(recipeList);
        recipeAdapter.notifyDataSetChanged();
    }
}
