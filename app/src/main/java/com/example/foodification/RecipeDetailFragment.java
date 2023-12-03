package com.example.foodification;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RecipeDetailFragment extends Fragment {

    // UI elements
    private TextView recipeTitle;
    private TextView recipeIngredients;
    private TextView recipeInstructions, recipeEquipment;

    // Constructor for creating a new instance of the fragment with RecipeDetail
    public static RecipeDetailFragment newInstance(RecipeDetail recipeDetail) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("RecipeDetailData", recipeDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recipe_detail, container, false);

        // Initialize the UI components
        recipeTitle = view.findViewById(R.id.detail_recipe_title);
        recipeIngredients = view.findViewById(R.id.detail_recipe_ingredients);
        recipeInstructions = view.findViewById(R.id.detail_recipe_instructions);
        recipeEquipment= view.findViewById(R.id.detail_recipe_equipment);

        // Extract the recipe detail object from the arguments
        if (getArguments() != null) {
            RecipeDetail recipeDetail = (RecipeDetail) getArguments().getSerializable("RecipeDetailData");

            // Populate the UI elements with the recipe detail data
            if (recipeDetail != null) {
                recipeTitle.setText(recipeDetail.getName());
                recipeIngredients.setText(formatIngredients(recipeDetail.getSteps()));
                recipeEquipment.setText(formatEquipment(recipeDetail.getSteps()));
                recipeInstructions.setText(formatInstructions(recipeDetail.getSteps()));
            }
        }
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

        return view;
    }

    private String formatInstructions(List<RecipeStep> steps) {
        StringBuilder instructionsBuilder = new StringBuilder();
        for (RecipeStep step : steps) {
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
