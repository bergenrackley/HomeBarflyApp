package com.example.homebarflyapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homebarflyapp.model.SuggestedIngredient;

import java.util.ArrayList;
import java.util.List;

public class SuggestionsFragment extends Fragment {
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.suggestions_fragment, container, false);

        MainActivity mainActivity = (MainActivity)getActivity();

        FragmentTransaction toolbarTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
        toolbarTransaction.replace(R.id.toolbar_fragment_container, SuggestionsToolbarFragment.class, null);
        toolbarTransaction.commit();

        Cursor suggestions = mainActivity.DB.generateSuggestions();
        suggestions.moveToFirst();
        List<SuggestedIngredient> baseSpiritList = new ArrayList<SuggestedIngredient>();
        List<SuggestedIngredient> liqueurList = new ArrayList<SuggestedIngredient>();
        List<SuggestedIngredient> mixinsList = new ArrayList<SuggestedIngredient>();

        while (!suggestions.isAfterLast()) {
            switch (suggestions.getString(2)) {
                case "Base Spirit":
                    baseSpiritList.add(new SuggestedIngredient(suggestions.getString(0), suggestions.getInt(1), suggestions.getString(2), suggestions.getString(3)));
                    break;
                case "Liqueur":
                    liqueurList.add(new SuggestedIngredient(suggestions.getString(0), suggestions.getInt(1), suggestions.getString(2), suggestions.getString(3)));
                    break;
                case "Mix-in":
                    mixinsList.add(new SuggestedIngredient(suggestions.getString(0), suggestions.getInt(1), suggestions.getString(2), suggestions.getString(3)));
                    break;
                default:
                    break;
            }
            suggestions.moveToNext();
        }
        RecyclerView baseSpiritsRV = mView.findViewById(R.id.suggestions_baseSpiritsRV);
        setUpRV(baseSpiritsRV, baseSpiritList);
        RecyclerView liqueursRV = mView.findViewById(R.id.suggestions_liqueursRV);
        setUpRV(liqueursRV, liqueurList);
        RecyclerView mixinsRV = mView.findViewById(R.id.suggestions_mixinsRV);
        setUpRV(mixinsRV, mixinsList);

        return mView;
    }


    private void setUpRV(RecyclerView recyclerView, List<SuggestedIngredient> dataSet) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SuggestionsRVAdapter adapter = new SuggestionsRVAdapter(dataSet);
        recyclerView.setAdapter(adapter);
    }

}
