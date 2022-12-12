package com.example.homebarflyapp.model;

public class AddIngredient implements Comparable<AddIngredient>{
    private String ingredientName;
    private String ingredientType;
    private String fileName;
    private String quantity;
    private String measurement;
    private boolean isChecked;

    public AddIngredient(String name, String file) {
        ingredientName = name;
        fileName = file;
        isChecked = false;
    }

    public AddIngredient(String name, String type, String file) {
        ingredientName = name;
        ingredientType = type;
        fileName = file;
        isChecked = false;
    }

    public AddIngredient(String name, String type, boolean check) {
        ingredientName = name;
        ingredientType = type;
        isChecked = check;
    }

    public boolean getState() {return isChecked;}

    public String getIngredientName() {return ingredientName;}

    public String getIngredientType() {return ingredientType;}

    public String getFileName() {return fileName;}

    public String getQuantity() {return quantity;}

    public void setQuantity(String quan) {quantity = quan;}

    public String getMeasurement() {return measurement;}

    public void setMeasurement(String meas) {measurement = meas;}

    public void toggle() {isChecked = !isChecked;}

    public void setState(Boolean bool) {
        isChecked = bool;
    }

    @Override
    public int compareTo(AddIngredient addIngredient) {
        return this.ingredientName.toLowerCase().compareTo(addIngredient.getIngredientName().toLowerCase());
    }
}
