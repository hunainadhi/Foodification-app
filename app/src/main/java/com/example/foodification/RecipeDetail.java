package com.example.foodification;

import java.io.Serializable;
import java.util.List;

public class RecipeDetail implements Serializable {
    private String name;
    private List<RecipeStep> steps;
    private List<Ingredient> missingIngredients;

    // Constructor
    public RecipeDetail() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }

    public List<Ingredient> getMissingIngredients() {
        return missingIngredients;
    }

    public void setMissingIngredients(List<Ingredient> missingIngredients) {
        this.missingIngredients = missingIngredients;
    }
}