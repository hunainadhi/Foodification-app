package com.example.foodification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.List;

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
        args.putSerializable("RecipeDetailMissedIngredients", (Serializable) missedIngredients);
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

        return view;
    }

    private String formatIngredients(List<RecipeStep> steps) {
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (RecipeStep step : steps) {
            for (Ingredients ingredients : step.getIngredients()) {
                ingredientsBuilder.append("• ")
                        .append(ingredients.getName())
                        .append("\n");
            }
        }
        return ingredientsBuilder.toString();
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
    private String formatEquipment(List<RecipeStep> steps) {
        StringBuilder equipmentBuilder = new StringBuilder();
        for (RecipeStep step : steps) {
            for (Equipment equip : step.getEquipment()) {
                equipmentBuilder.append("• ")
                        .append(equip.getName())
                        .append("\n");
            }
        }
        return equipmentBuilder.toString();
    }
}
