package com.example.homebarflyapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class RecipeToolbarFragment extends Fragment {

    private boolean onlyFavorites = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_toolbar_fragment, container, false);

        ImageView favoriteIcon = view.findViewById(R.id.favoriteRecipeIcon);
        ImageView filterIcon = view.findViewById(R.id.filterRecipeIcon);
        ImageView addIcon = view.findViewById(R.id.addRecipeIcon);

        favoriteIcon.setOnClickListener((e) -> {
            onlyFavorites = !onlyFavorites;
            FragmentManager fm = getParentFragmentManager();
            com.example.homebarflyapp.RecipeFragment fragm = (com.example.homebarflyapp.RecipeFragment)fm.findFragmentById(R.id.fragment_container);
            for (com.example.homebarflyapp.RecipeRVAdapter adapter : fragm.adapters) {
                adapter.toggleFavorites();
            }

            if (onlyFavorites) favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
            else favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_border_24);

        });

        filterIcon.setOnClickListener((e) -> {
            FragmentManager fm = getParentFragmentManager();
            com.example.homebarfly.RecipeFragment fragm = (com.example.homebarfly.RecipeFragment)fm.findFragmentById(R.id.fragment_container);
            fragm.showFilterDialog();
        });

        addIcon.setOnClickListener((e) -> {
            FragmentManager fm = getParentFragmentManager();
            com.example.homebarfly.RecipeFragment fragm = (com.example.homebarfly.RecipeFragment)fm.findFragmentById(R.id.fragment_container);
            fragm.showNewRecipeIngredientsDialog();
        });


        return view;
    }
}
