package com.example.homebarflyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.homebarflyapp.model.SuggestedIngredient;

import java.util.List;

public class SuggestionsRVAdapter extends RecyclerView.Adapter<SuggestionsRVAdapter.ViewHolder>{
    private List<SuggestedIngredient> ingredientItems;

    private int selectedPos = RecyclerView.NO_POSITION;
    Context context;

    public SuggestionsRVAdapter(List<SuggestedIngredient> ingredients) {
        ingredientItems = ingredients;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView ingredientLabel;
        TextView newRecipesLabel;
        ImageView addIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ingredientLabel = itemView.findViewById(R.id.suggestions_ingredientName);
            newRecipesLabel = itemView.findViewById(R.id.suggestions_newrecipes);
            addIcon = itemView.findViewById(R.id.addsuggestionIcon);
            addIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getBindingAdapterPosition() == RecyclerView.NO_POSITION) return;

            selectedPos = getBindingAdapterPosition();
            if (view.getId() == addIcon.getId()) {
                MainActivity mainActivity = (MainActivity)context;
                mainActivity.DB.insertMyBar(ingredientItems.get(selectedPos).getIngredientName(), ingredientItems.get(selectedPos).getType(), ingredientItems.get(selectedPos).getFileName());
                ingredientItems.remove(selectedPos);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.suggestions_rv_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView ingredientLabel = holder.ingredientLabel;
        TextView newRecipesLabel = holder.newRecipesLabel;
        ingredientLabel.setText(ingredientItems.get(position).getIngredientName());
        if (ingredientItems.get(position).getNewRecipes() == 1) newRecipesLabel.setText("Part of 1 recipe");
        else newRecipesLabel.setText("Part of " + ingredientItems.get(position).getNewRecipes() + " recipes");
    }

    @Override
    public int getItemCount() {
        return ingredientItems.size();
    }
}
