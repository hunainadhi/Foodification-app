package com.example.foodification;

import java.io.Serializable;

public class Recipe implements Serializable {

    // Member variables representing the details of a recipe
    private String name;
    private String prepTime;
    private String totalTime;
    private String ingredients;
    private String instructions;

    // Constructor that accepts parameters to initialize the member variables
    public Recipe(String name, String prepTime, String totalTime, String ingredients, String instructions) {
        this.name = name;
        this.prepTime = prepTime;
        this.totalTime = totalTime;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    // Getters and setters for each member variable

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
