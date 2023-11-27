package com.example.foodification;
import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipePageFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    String recipesJson;

    public RecipePageFragment(String recipesJson) {
        this.recipesJson=recipesJson;
    }

    public RecipePageFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recipepage, container, false);

        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the adapter with an empty list first
        recipeAdapter = new RecipeAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(recipeAdapter);

        // Prepare data for the RecyclerView
        fetchRecipesFromIntent(); // Now your adapter is initialized before this call

        return view;
    }
    private void fetchRecipesFromIntent() {
        if (recipesJson != null) {
            Type listType = new TypeToken<List<Recipe>>(){}.getType();
            recipeList = new Gson().fromJson(recipesJson, listType);
            updateRecipeData();
        }
    }

    // This method creates a list of static recipes
    @SuppressLint("NotifyDataSetChanged")
    private void updateRecipeData() {
            // Clear the adapter and add the updated recipe list
            recipeAdapter.setRecipeList(recipeList);
            recipeAdapter.notifyDataSetChanged();
        }
}

