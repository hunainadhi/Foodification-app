package com.example.foodification;

// Ingredient.java (Data Model Class)
public class Ingredient {
    private String id; // Unique identifier
    private String name;
    private double quantity;
    private String unit;

    public Ingredient() {
        // Default constructor required by Firebase
        id = "";
        name = "";
        quantity = 0.0;
        unit = "";
    }

    public Ingredient(String id, String name, double quantity, String unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getter and setter methods for fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
