package com.example.foodification;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipePageFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private String recipesJson;

    public RecipePageFragment(String recipesJson) {
        this.recipesJson = recipesJson;
    }

    public RecipePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recipepage, container, false);

        recyclerView = view.findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recipeAdapter = new RecipeAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(recipeAdapter);

        fetchRecipesFromIntent();

        return view;
    }

    private void fetchRecipesFromIntent() {
        if (recipesJson != null) {
            Type listType = new TypeToken<List<Recipe>>(){}.getType();
            recipeList = new Gson().fromJson(recipesJson, listType);
            updateRecipeData();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecipeData() {
        recipeAdapter.setRecipeList(recipeList);
        recipeAdapter.notifyDataSetChanged();
    }

    public void onRecipeClicked(Recipe recipe) {
        fetchRecipeDetailsAndOpenDetailFragment(recipe.getId());
    }

    private void fetchRecipeDetailsAndOpenDetailFragment(String recipeId) {
        String apiUrl = "https://api.spoonacular.com/recipes/" + recipeId + "/analyzedInstructions";
        String apiKey = "42b956e445e84aa08770373b20991b9e"; // Replace with your Spoonacular API key

        String url = apiUrl + "?apiKey=" + apiKey;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Parse the response to create a RecipeDetail object
                            RecipeDetail recipeDetail = parseRecipeDetail(response);
                            openRecipeDetailFragment(recipeDetail);
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

    private RecipeDetail parseRecipeDetail(JSONArray response) throws JSONException {
        RecipeDetail recipeDetail = new RecipeDetail();

        if (response.length() > 0) {
            JSONObject recipeObject = response.getJSONObject(0);
            recipeDetail.setName(recipeObject.optString("name", ""));

            JSONArray stepsArray = recipeObject.getJSONArray("steps");
            List<RecipeStep> steps = new ArrayList<>();

            for (int i = 0; i < stepsArray.length(); i++) {
                JSONObject stepObject = stepsArray.getJSONObject(i);
                RecipeStep step = new RecipeStep();

                step.setNumber(stepObject.getInt("number"));
                step.setStep(stepObject.getString("step"));

                // Parsing equipment
                JSONArray equipmentArray = stepObject.getJSONArray("equipment");
                List<Equipment> equipmentList = new ArrayList<>();
                for (int j = 0; j < equipmentArray.length(); j++) {
                    JSONObject equipmentObject = equipmentArray.getJSONObject(j);
                    Equipment equipment = new Equipment();
                    equipment.setId(equipmentObject.getInt("id"));
                    equipment.setName(equipmentObject.getString("name"));
                    equipment.setImage(equipmentObject.getString("image"));
                    equipmentList.add(equipment);
                }
                step.setEquipment(equipmentList);

                // Parsing ingredients
                JSONArray ingredientsArray = stepObject.getJSONArray("ingredients");
                List<Ingredient> ingredientList = new ArrayList<>();
                for (int k = 0; k < ingredientsArray.length(); k++) {
                    JSONObject ingredientObject = ingredientsArray.getJSONObject(k);
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(ingredientObject.getString("id"));
                    ingredient.setName(ingredientObject.getString("name"));
                    ingredient.setImage(ingredientObject.getString("image"));
                    ingredientList.add(ingredient);
                }
                step.setIngredients(ingredientList);

                steps.add(step);
            }

            recipeDetail.setSteps(steps);
        }

        return recipeDetail;
    }


    private void openRecipeDetailFragment(RecipeDetail recipeDetail) {
        RecipeDetailFragment fragment = RecipeDetailFragment.newInstance(recipeDetail);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.homeFragmentContainer, fragment) // Make sure the ID matches with your layout
                .addToBackStack(null)
                .commit();
    }
}
