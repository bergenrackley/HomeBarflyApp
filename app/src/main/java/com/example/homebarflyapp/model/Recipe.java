package com.example.homebarflyapp.model;

import java.util.Comparator;

public class Recipe {
    private String recipeName;
    private String instructions;
    private String fileName;
    private int isFavorite;
    private Integer ingredientCount;
    private Integer ingredientHave;

    public Recipe(String name, String instruc, int favorite, String file) {
        recipeName = name;
        instructions = instruc;
        fileName = file;
        isFavorite = favorite;
        ingredientCount = 0;
        ingredientHave = 0;
    }

    public Recipe(String name, String instruc, Integer count, Integer have, int favorite, String file) {
        recipeName = name;
        instructions = instruc;
        fileName = file;
        ingredientCount = count;
        ingredientHave = have;
        isFavorite = favorite;
    }

    public String getRecipeName() {return recipeName;}

    public String getInstructions() {return instructions;}

    public String getFileName() {return fileName;}

    public Integer getHave() {return ingredientHave;}

    public Integer getCount() {return ingredientCount;}

    public boolean getIsFavorite() {
        if (isFavorite == 1) return true;
        else return false;
    }

    public void toggleFavorite() {
        if (isFavorite == 1) isFavorite = 0;
        else isFavorite = 1;
    }

    public static Comparator<Recipe> compareByIngredient() {
        return new Comparator<Recipe>() {
            @Override
            public int compare(Recipe recipe, Recipe t1) {
                return (int)((recipe.ingredientCount - recipe.ingredientHave) - (t1.getCount() - t1.getHave()));
            }
        };
    }

    public static Comparator<Recipe> compareByName() {
        return new Comparator<Recipe>() {
            @Override
            public int compare(Recipe recipe, Recipe t1) {
                return recipe.recipeName.toLowerCase().compareTo(t1.getRecipeName().toLowerCase());
            }
        };
    }
}
