package com.example.foodification;

// Ingredient.java (Data Model Class)
public class Grocery {
    private String id; // Unique identifier
    private String name;
    private double quantity;
    private String unit;
    private Boolean check;

    public Grocery() {
        // Default constructor required by Firebase
        id = "";
        name = "";
        quantity = 0.0;
        unit = "";
        check = false;
    }

    public Grocery(String id, String name, double quantity, String unit,Boolean check) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.check = check;
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

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }
}
