package com.example.foodification;

        import android.annotation.SuppressLint;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.toolbox.JsonArrayRequest;
        import com.android.volley.toolbox.JsonObjectRequest;
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
    private List<Recipe> recipeList;
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

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void fetchRecipesFromIntent() {
        if (recipesJson != null) {
            Type listType = new TypeToken<List<Recipe>>() {}.getType();
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
        fetchRecipeDetailsAndOpenDetailFragment(recipe.getId(), recipe.missedIngredients, recipe);
    }

    private void fetchRecipeDetailsAndOpenDetailFragment(String recipeId, List<Ingredient> missedIngredients, Recipe recipe) {
        String apiUrl = "https://api.spoonacular.com/recipes/" + recipeId + "/analyzedInstructions";
        String apiKey = "438f4a62633645fd893cddefb2f2df00"; // Replace with your API key
        String url = apiUrl + "?apiKey=" + apiKey;

        ProgressBarClass.getInstance().showProgress(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ProgressBarClass.getInstance().dismissProgress();
                    if (response == null || response.length() == 0) {
                        Toast.makeText(getContext(), "Empty or null response for recipe details", Toast.LENGTH_SHORT).show();
                        return; // Prevents further processing and keeps the user on the current fragment
                    }

                    try {
                        RecipeDetail recipeDetail = parseRecipeDetail(response, recipe.getName(), missedIngredients, recipe.getImage());
                        if (recipeDetail != null) {
                            openRecipeDetailFragment(recipeDetail, recipe);
                        } else {
                            Toast.makeText(getContext(), "No detail found for the recipe", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Error parsing recipe details", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }, error -> {
            ProgressBarClass.getInstance().dismissProgress();
            Toast.makeText(getContext(), "Network error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }

    private RecipeDetail parseRecipeDetail(JSONArray response, String recipeName, List<Ingredient> missedIngredients, String image) throws JSONException {
        RecipeDetail recipeDetail = new RecipeDetail();

        if (response.length() > 0) {
            JSONObject recipeObject = response.getJSONObject(0);
            recipeDetail.setName(recipeName);

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
                List<Ingredients> ingredientList = new ArrayList<>();
                for (int k = 0; k < ingredientsArray.length(); k++) {
                    JSONObject ingredientObject = ingredientsArray.getJSONObject(k);
                    Ingredients ingredients = new Ingredients();
                    ingredients.setId(ingredientObject.getString("id"));
                    ingredients.setName(ingredientObject.getString("name"));
                    ingredients.setImage(ingredientObject.getString("image"));
                    ingredientList.add(ingredients);
                }
                step.setIngredients(ingredientList);
                steps.add(step);
            }
            recipeDetail.setSteps(steps);
            recipeDetail.setMissingIngredients(missedIngredients);
            recipeDetail.setImage(image);
        }

        return recipeDetail;
    }

    private void openRecipeDetailFragment(RecipeDetail recipeDetail, Recipe recipe) {
        RecipeDetailFragment fragment = RecipeDetailFragment.newInstance(recipeDetail, recipe);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.homeFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
