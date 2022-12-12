package com.example.homebarflyapp;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.homebarflyapp.model.AddIngredient;

import java.util.List;

public class NewRecipeRVAdapter extends RecyclerView.Adapter<NewRecipeRVAdapter.ViewHolder>{
    private List<AddIngredient> ingredientItems;

    private int selectedPos = RecyclerView.NO_POSITION;
    private Context context;

    public NewRecipeRVAdapter(List<AddIngredient> ingredients) {
        ingredientItems = ingredients;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {
        Spinner measurementSpinner;
        TextView ingredientLabel;
        EditText quantityLabel;

        public ViewHolder(View itemView) {
            super(itemView);

            ingredientLabel = itemView.findViewById(R.id.newRecipe_ingredientLabel);
            measurementSpinner = itemView.findViewById(R.id.measurementSpinner);
            measurementSpinner.setOnItemSelectedListener(this);
            quantityLabel = itemView.findViewById(R.id.quantityTextField);
            quantityLabel.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    ingredientItems.get(getBindingAdapterPosition()).setQuantity(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            ingredientItems.get(getBindingAdapterPosition()).setMeasurement(parent.getItemAtPosition(pos).toString());
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.newrecipe_ingredient_rv_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AddIngredient ingredient = ingredientItems.get(position);
        TextView ingredientLabel = holder.ingredientLabel;
        Spinner measurementSpinner = holder.measurementSpinner;
        ingredientLabel.setText(ingredient.getIngredientName());

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.measurements_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measurementSpinner.setAdapter(adapter);
    }

    public List<AddIngredient> getIngredientItems() {return ingredientItems;}

    @Override
    public int getItemCount() {
        return ingredientItems.size();
    }
}
