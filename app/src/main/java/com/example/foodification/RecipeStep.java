package com.example.foodification;

import java.io.Serializable;
import java.util.List;

public class RecipeStep implements Serializable {
    private int number;
    private String step;
    private List<Equipment> equipment;
    private List<Ingredients> ingredients;
    private List<Ingredient> missedIngredients;

    // Constructor
    public RecipeStep() {
    }

    // Getters and Setters
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setMissedIngredients(List<Ingredient> missedIngredients) {
        this.missedIngredients = missedIngredients;
    }

    public List<Ingredient> getMissedIngredients() {
        return missedIngredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }
}
