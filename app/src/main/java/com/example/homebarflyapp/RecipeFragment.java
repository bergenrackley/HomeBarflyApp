package com.example.homebarflyapp;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homebarflyapp.model.AddIngredient;
import com.example.homebarflyapp.model.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class RecipeFragment extends Fragment {

    class IngredientObject {
        public String name;
        public String quantity;

        public IngredientObject(String ing, String quan) {
            name = ing;
            quantity = quan;
        }

        public IngredientObject(String ing) {
            name = ing;
            quantity = "na";
        }

    }

    class RecipeObject {
        public String recipeName;
        public List<IngredientObject> ingredients;
        public String instructions;

        public RecipeObject(String name, List<AddIngredient> ing, String inst) {
            recipeName = name;
            instructions = inst;
            ingredients = new ArrayList<IngredientObject>();
            for (AddIngredient ingredient : ing) {
                if (ingredient.getQuantity().equals("") || ingredient.getMeasurement().equals("")) ingredients.add(new IngredientObject(ingredient.getIngredientName()));
                else ingredients.add(new IngredientObject(ingredient.getIngredientName(), ingredient.getQuantity() + " " + ingredient.getMeasurement()));
            }

        }
    }

    public List<com.example.homebarfly.RecipeRVAdapter> adapters = new ArrayList<com.example.homebarfly.RecipeRVAdapter>();

    List<String> filterIngredients;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_fragment, container, false);

        MainActivity mainActivity = (MainActivity)getActivity();
        filterIngredients = new ArrayList<String>();

        FragmentTransaction toolbarTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
        toolbarTransaction.replace(R.id.toolbar_fragment_container, RecipeToolbarFragment.class, null);
        toolbarTransaction.commit();

        List<Cursor> recipeCursors = mainActivity.DB.getReadyToMake();
        HashMap<String, Integer> countHash = new HashMap<String, Integer>();
        Cursor hasSome = recipeCursors.get(0);
        hasSome.moveToFirst();
        while (!hasSome.isAfterLast()) {
            countHash.put(hasSome.getString(0), hasSome.getInt(1));
            hasSome.moveToNext();
        }

        Cursor recipes = recipeCursors.get(1);
        List<Recipe> recipeList = new ArrayList<Recipe>();
        List<Recipe> almostHaveList = new ArrayList<Recipe>();
        recipes.moveToFirst();
        while (!recipes.isAfterLast()) {
            if (countHash.get(recipes.getString(0)) == recipes.getInt(1)) {
                recipeList.add(new Recipe(recipes.getString(0), recipes.getString(2), recipes.getInt(3), recipes.getString(4)));
                countHash.remove(recipes.getString(0));
            } else {
                almostHaveList.add(new Recipe(recipes.getString(0), recipes.getString(2), recipes.getInt(1), countHash.get(recipes.getString(0)), recipes.getInt(3), recipes.getString(4)));
            }
            recipes.moveToNext();
        }

        Cursor allRecipes = mainActivity.DB.getAllRecipes();
        allRecipes.moveToFirst();
        List<Recipe> allRecipeList = new ArrayList<Recipe>();
        while (!allRecipes.isAfterLast()) {
            allRecipeList.add(new Recipe(allRecipes.getString(0), allRecipes.getString(2), allRecipes.getInt(3), allRecipes.getString(4)));
            allRecipes.moveToNext();
        }

        Collections.sort(recipeList, Recipe.compareByName());
        Collections.sort(almostHaveList, Recipe.compareByIngredient());
        Collections.sort(allRecipeList, Recipe.compareByName());

        adapters = new ArrayList<com.example.homebarfly.RecipeRVAdapter>();
        adapters.add(setRV(view.findViewById(R.id.rtmRV), recipeList));
        adapters.add(setRV(view.findViewById(R.id.nmiRV), almostHaveList));
        adapters.add(setRV(view.findViewById(R.id.arRV), allRecipeList));



        SearchView searchView = view.findViewById(R.id.recipeSearchBox);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                for (com.example.homebarfly.RecipeRVAdapter adapter : adapters) {
                    adapter.filterView(newText);
                }
                return true;
            }
        });

        return view;
    }

    private com.example.homebarfly.RecipeRVAdapter setRV(RecyclerView recyclerView, List<Recipe> recipeList) {
        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        com.example.homebarfly.RecipeRVAdapter adapter = new com.example.homebarfly.RecipeRVAdapter(recipeList);
        recyclerView.setAdapter(adapter);

        final int spacing = getResources().getDimensionPixelSize(R.dimen.gridSpacing);

        recyclerView.setPadding(spacing, spacing, spacing, spacing);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(spacing, spacing, spacing, spacing);
            }
        });

        return adapter;
    }

    public void showFilterDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.filterrecipe_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        MainActivity mainActivity = (MainActivity)getActivity();
        Cursor ingredients = mainActivity.DB.getIngredients();
        List<AddIngredient> data = new ArrayList<AddIngredient>();
        ingredients.moveToFirst();
        int nameIndex = ingredients.getColumnIndex("Ingredients.name");
        int typeIndex = ingredients.getColumnIndex("Ingredients.type");
        while (!ingredients.isAfterLast()) {
            boolean checkBool = false;
            if (filterIngredients.contains(ingredients.getString(nameIndex))) checkBool = true;
            data.add(new AddIngredient(ingredients.getString(nameIndex), ingredients.getString(typeIndex), checkBool));
            ingredients.moveToNext();
        }
        Collections.sort(data);

        RecyclerView recyclerView = dialog.findViewById(R.id.recipeFilterSearchRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AddIngredientRVAdapter adapter = new AddIngredientRVAdapter(data);
        recyclerView.setAdapter(adapter);

        SearchView searchView = dialog.findViewById(R.id.recipeFilterSearchBox);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterView(newText);
                return true;
            }
        });

        Button closeButton = dialog.findViewById(R.id.closeFilterButton);
        closeButton.setOnClickListener((e) -> {
            filterIngredients.clear();
            for (AddIngredient ingredient : adapter.getAddedIngredients()) {
                filterIngredients.add(ingredient.getIngredientName());
            }
            dialog.dismiss();
            for (com.example.homebarfly.RecipeRVAdapter recipeAdapter : adapters) {
                recipeAdapter.setFilterIngredients(filterIngredients);
            }
        });

        Button cancelButton = dialog.findViewById(R.id.clearFilterButton);
        cancelButton.setOnClickListener((e) -> {
            filterIngredients.clear();
            for (com.example.homebarfly.RecipeRVAdapter recipeAdapter : adapters) {
                recipeAdapter.setFilterIngredients(filterIngredients);
            }
            dialog.dismiss();
        });

        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    public void showNewRecipeIngredientsDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.newrecipe_ingredient_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        MainActivity mainActivity = (MainActivity)getActivity();
        Cursor ingredients = mainActivity.DB.getIngredients();
        List<AddIngredient> data = new ArrayList<AddIngredient>();
        ingredients.moveToFirst();
        int nameIndex = ingredients.getColumnIndex("Ingredients.name");
        int typeIndex = ingredients.getColumnIndex("Ingredients.type");
        while (!ingredients.isAfterLast()) {
            boolean checkBool = false;
            if (filterIngredients.contains(ingredients.getString(nameIndex))) checkBool = true;
            data.add( new AddIngredient(ingredients.getString(nameIndex), ingredients.getString(typeIndex), checkBool));
            ingredients.moveToNext();
        }
        Collections.sort(data);

        RecyclerView recyclerView = dialog.findViewById(R.id.newRecipe_ingredientSearchRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AddIngredientRVAdapter adapter = new AddIngredientRVAdapter(data);
        recyclerView.setAdapter(adapter);

        SearchView searchView = dialog.findViewById(R.id.newRecipe_ingredientSearchBox);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterView(newText);
                return true;
            }
        });

        Button closeButton = dialog.findViewById(R.id.newRecipe_closeIngredientButton);
        closeButton.setOnClickListener((e) -> {
            List<AddIngredient> recipeIngredients = adapter.getAddedIngredients();
            dialog.dismiss();
            showNewRecipeDialog(recipeIngredients);
        });

        Button cancelButton = dialog.findViewById(R.id.newRecipe_cancelIngredientButton);
        cancelButton.setOnClickListener((e) -> {
            dialog.dismiss();
        });

        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    public void showNewRecipeDialog(List<AddIngredient> data) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.newrecipe_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        RecyclerView recyclerView = dialog.findViewById(R.id.ingredientConfigureRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        NewRecipeRVAdapter adapter = new NewRecipeRVAdapter(data);
        recyclerView.setAdapter(adapter);

        EditText recipeName = dialog.findViewById(R.id.newrecipenamefield);
        EditText recipeInstructions = dialog.findViewById(R.id.newrecipeinstructionsfield);

        Button closeButton = dialog.findViewById(R.id.closeRecipeButton);
        closeButton.setOnClickListener((e) -> {
            adapter.getIngredientItems();
            RecipeObject recipeObject = new RecipeObject(recipeName.getText().toString(), adapter.getIngredientItems(), recipeInstructions.getText().toString());
            dialog.dismiss();
            MainActivity mainActivity = (MainActivity)getActivity();
            mainActivity.DB.addUserRecipe(recipeObject);
            mainActivity.reloadFragment("recipe");
        });

        Button cancelButton = dialog.findViewById(R.id.cancelRecipeButton);
        cancelButton.setOnClickListener((e) -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}
