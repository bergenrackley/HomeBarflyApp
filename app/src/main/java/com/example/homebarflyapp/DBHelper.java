package com.example.homebarflyapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper implements Serializable {

    Context myContext;

    public DBHelper(Context context) {
        super(context, "Recipes.db", null, 1);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Recipes(name TEXT primary key, instructions TEXT, ingredientCount INTEGER, favorite INTEGER, fileName TEXT)");
        DB.execSQL("create Table Ingredients(name TEXT primary key, type TEXT, fileName TEXT)");
        DB.execSQL("create Table RecipeIngredients(id INTEGER primary key, name TEXT, quantity TEXT, recipeName TEXT)");
        DB.execSQL("create Table MyBar(name TEXT primary key, type TEXT, fileName TEXT)");

        //initDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Recipes");
        DB.execSQL("drop Table if exists Ingredients");
        DB.execSQL("drop Table if exists RecipeIngredients");
        DB.execSQL("drop Table if exists MyBar");
    }

    public Boolean insertIngredient(String name, String type, String fileName) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("type", type);
        contentValues.put("fileName", fileName);
        long result = DB.insert("Ingredients", null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public Boolean insertRecipeIngredient(Integer id, String name, String quantity, String recipeName) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("name", name);
        contentValues.put("quantity", quantity);
        contentValues.put("recipeName", recipeName);
        long result = DB.insert("RecipeIngredients", null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public Boolean insertRecipe(String name, String instructions, String fileName) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("instructions", instructions);
        contentValues.put("favorite", 0);
        contentValues.put("fileName", fileName);
        Cursor cursor = DB.rawQuery("Select Count(*) from RecipeIngredients where recipeName = ?", new String[] {name});
        cursor.moveToFirst();
        contentValues.put("ingredientCount", cursor.getInt(0));
        long result = DB.insert("Recipes", null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public Boolean insertMyBar(String name, String type, String fileName) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("type", type);
        contentValues.put("fileName", fileName);
        long result = DB.insert("MyBar", null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public void removeMyBar(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from MyBar where name = ?", new String[] {name});
        if (cursor.getCount() > 0) DB.delete("MyBar", "name=?", new String[] {name});
    }

    public Cursor getRecipes() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Recipes", null);
        return cursor;
    }

    public Cursor getIngredients() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Ingredients", null);
        return cursor;
    }

    public Cursor getRecipeIngredients(String recipeName) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from RecipeIngredients where recipeName = ?", new String[] {recipeName});
        return cursor;
    }

    public Cursor getMyBar() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from MyBar", null);
        return cursor;
    }

    public void initDatabase() {

        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from MyBar", null);
        if (cursor.getCount() > 0) return;

        InputStreamReader is = null;
        try {
            is = new InputStreamReader(myContext.getAssets().open("ingredients.csv"));
            CSVReader csvReader = new CSVReader(is);
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                insertIngredient(nextLine[0], nextLine[1], nextLine[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            is = new InputStreamReader(myContext.getAssets().open("recipeIngredients.csv"));
            CSVReader csvReader = new CSVReader(is);
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                insertRecipeIngredient(Integer.valueOf(nextLine[3]), nextLine[1], nextLine[2], nextLine[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            is = new InputStreamReader(myContext.getAssets().open("recipes.csv"));
            CSVReader csvReader = new CSVReader(is);
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                insertRecipe(nextLine[0], nextLine[1], nextLine[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Cursor> getReadyToMake() {
        SQLiteDatabase DB = this.getWritableDatabase();
        List<Cursor> cursors = new ArrayList<Cursor>();
        Cursor cursor1 = DB.rawQuery("Select recipeName, Count(*) as Count from RecipeIngredients where name in (Select name from MyBar) Group By recipeName", null);
        Cursor cursor2 = DB.rawQuery("Select name, ingredientCount, instructions, favorite, fileName from Recipes where name in (Select recipeName from RecipeIngredients where name in (Select name from MyBar) Group By recipeName)", null);
        cursors.add(cursor1);
        cursors.add(cursor2);

        return cursors;
    }

    public Cursor getAllRecipes() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select name, ingredientCount, instructions, favorite, fileName from Recipes", null);
        return cursor;
    }

    public boolean checkInBar(String ingredient) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select name from myBar where name = ?", new String[] {ingredient});
        return cursor.getCount() > 0;
    }

    public void toggleFavorite(String recipe) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select favorite from Recipes where name = ?", new String[] {recipe});
        cursor.moveToFirst();
        Integer favorite = cursor.getInt(0);
        ContentValues cv = new ContentValues();
        if (favorite == 1) cv.put("favorite", 0);
        else cv.put("favorite",1);
        DB.update("Recipes", cv, "name = ?", new String[] {recipe});
    }

    public boolean isIngredientInRecipe(String ingredient, String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select recipeName from RecipeIngredients where name = ? and recipeName = ?", new String[] {ingredient, name});
        return cursor.getCount() > 0;
    }

    public Cursor generateSuggestions() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select RecipeIngredients.name, Count(RecipeIngredients.name) as Count, Ingredients.type, Recipes.fileName from RecipeIngredients inner join Ingredients on RecipeIngredients.name = Ingredients.name inner join Recipes on RecipeIngredients.recipeName = Recipes.name where RecipeIngredients.name not in (Select name from MyBar) Group By RecipeIngredients.name Order By Count Desc", null);
        return cursor;
    }

    public void addUserRecipe(RecipeFragment.RecipeObject recipeObject) {
        if (recipeObject.recipeName.equals("")) return;
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select name from Recipes where name = ?", new String[] {recipeObject.recipeName});
        if (cursor.getCount() > 0) return;

        cursor = DB.rawQuery("Select id from RecipeIngredients Order By id Desc", null);
        cursor.moveToFirst();
        Integer id = cursor.getInt(0);

        for (RecipeFragment.IngredientObject ingredient : recipeObject.ingredients) {
            id += 1;
            insertRecipeIngredient(id, ingredient.name, ingredient.quantity, recipeObject.recipeName);
        }
        insertRecipe(recipeObject.recipeName, recipeObject.instructions, "not-an-image.png");
    }

    public void deleteDatabase() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("drop Table if exists Recipes");
        DB.execSQL("drop Table if exists Ingredients");
        DB.execSQL("drop Table if exists RecipeIngredients");
        DB.execSQL("drop Table if exists MyBar");
        onCreate(DB);
    }
}
