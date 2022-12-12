package com.example.homebarflyapp.model;

public class SuggestedIngredient {
    private String ingredientName;
    private Integer newRecipes;
    private String type;
    private String fileName;

    public SuggestedIngredient(String name, Integer count, String ingType, String file) {
        ingredientName = name;
        newRecipes = count;
        type = ingType;
        fileName = file;
    }

    public String getIngredientName() {return ingredientName;}

    public Integer getNewRecipes() {return newRecipes;}

    public String getType() {return type;}

    public String getFileName() {return fileName;}
}
