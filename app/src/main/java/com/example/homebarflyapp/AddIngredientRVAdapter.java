package com.example.homebarflyapp;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homebarflyapp.model.AddIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddIngredientRVAdapter extends RecyclerView.Adapter<AddIngredientRVAdapter.ViewHolder>{
    private List<AddIngredient> ingredientItems;
    private List<AddIngredient> unfilteredItems;
    private List<AddIngredient> addedIngredients = new ArrayList<AddIngredient>();

    private int selectedPos = RecyclerView.NO_POSITION;

    public AddIngredientRVAdapter(List<AddIngredient> ingredients) {
        ingredientItems = ingredients;
        unfilteredItems = ingredients;
        for (AddIngredient ingredient : ingredients) {
            if (ingredient.getState()) addedIngredients.add(ingredient);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.ingredientCheckbox);
            checkBox.setOnClickListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View view) {
            if (getBindingAdapterPosition() == RecyclerView.NO_POSITION) return;

            selectedPos = getBindingAdapterPosition();

            if (view.getId() == checkBox.getId()) {
                AddIngredient ingredient = ingredientItems.get(selectedPos);
                ingredient.toggle();
                if (ingredient.getState()) {
                    addedIngredients.add(ingredient);
                } else {
                    addedIngredients.remove(ingredient);
                }

            }
            //notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.addingredient_rv_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AddIngredient ingredient = ingredientItems.get(position);
        CheckBox checkBox = holder.checkBox;
        checkBox.setText(ingredient.getIngredientName());
        checkBox.setChecked(ingredient.getState());
    }

    @Override
    public int getItemCount() {
        return ingredientItems.size();
    }

    public List<AddIngredient> getAddedIngredients() {return addedIngredients;}

    public void filterView(String string) {
        ingredientItems = unfilteredItems.stream().filter(n -> n.getIngredientName().toLowerCase().contains(string.toLowerCase())).collect(Collectors.toList());
        notifyDataSetChanged();
    }
}
