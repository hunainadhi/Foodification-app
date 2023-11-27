package com.example.foodification;

import java.io.Serializable;

public class Recipe implements Serializable {

    private String id;
    private String image;
    private String name;
    private String prepTime;
    private String totalTime;
    private String ingredients;
    private String instructions;

    public Recipe(String id, String image, String name, String prepTime, String totalTime, String ingredients, String instructions) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.prepTime = prepTime;
        this.totalTime = totalTime;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

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
