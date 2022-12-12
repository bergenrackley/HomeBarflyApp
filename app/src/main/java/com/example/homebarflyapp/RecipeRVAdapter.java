package com.example.homebarflyapp;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homebarfly.model.Recipe;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeRVAdapter extends RecyclerView.Adapter<RecipeRVAdapter.ViewHolder>{

    private List<Recipe> recipeItems;
    private List<Recipe> unfilteredItems;
    private int selectedPos = RecyclerView.NO_POSITION;
    private Context mContext;
    //Filter related variables
    private boolean onlyFavorites;
    private String filterString;
    private List<String> filterIngredients;

    public RecipeRVAdapter(List<Recipe> listItems) {
        recipeItems = listItems;
        unfilteredItems = listItems;
        onlyFavorites = false;
        filterString = "";
        filterIngredients = new ArrayList<String>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public TextView nameTextView;
        public ConstraintLayout gridLayout;
        public ImageView favoriteIcon;
        public ImageView recipeImage;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView)itemView.findViewById(R.id.recipeIconName);
            gridLayout = (ConstraintLayout)itemView.findViewById((R.id.recipeLayout));
            favoriteIcon = (ImageView)itemView.findViewById(R.id.gridFavoriteIcon);
            recipeImage = (ImageView)itemView.findViewById(R.id.recipeIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getBindingAdapterPosition() == RecyclerView.NO_POSITION) return;

            selectedPos = getBindingAdapterPosition();
            showRecipeDialog(view, recipeItems.get(selectedPos));
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View contactView = inflater.inflate(R.layout.recipe_rv_grid, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipeItems.get(position);

        TextView nameView = holder.nameTextView;
        ImageView favoriteIcon = holder.favoriteIcon;
        nameView.setText(recipe.getRecipeName());

        ImageView recipeImage = holder.recipeImage;

        try
        {
            InputStream ims = mContext.getAssets().open("images/recipes/" + recipe.getRecipeName() + "/thumbnail.jpg");
            Drawable d = Drawable.createFromStream(ims, null);
            recipeImage.setImageDrawable(d);
            ims.close();
        }
        catch(IOException ex)
        {
        }

        if (recipe.getIsFavorite()) favoriteIcon.setVisibility(View.VISIBLE);
        else favoriteIcon.setVisibility(View.INVISIBLE);
        ConstraintLayout gridLayout = holder.gridLayout;
    }

    @Override
    public int getItemCount() {
        return recipeItems.size();
    }

    private void showRecipeDialog(View view, Recipe recipe) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.recipe_dialog);

        MainActivity mainActivity = (MainActivity)mContext;
        Cursor ingredients = mainActivity.DB.getRecipeIngredients(recipe.getRecipeName());
        List<String> ingredientList = new ArrayList<String>();
        ingredients.moveToFirst();
        int nameIndex = ingredients.getColumnIndex("RecipeIngredients.name");
        int quantityIndex = ingredients.getColumnIndex("RecipeIngredients.quantity");
        while (!ingredients.isAfterLast()) {
            String quantityVar = "";
            if (!ingredients.getString(quantityIndex).equals("na")) quantityVar = ingredients.getString(quantityIndex) + " ";
            if (mainActivity.DB.checkInBar(ingredients.getString(nameIndex))) ingredientList.add(quantityVar + ingredients.getString(nameIndex));
            else ingredientList.add("<font color='#EE0000'>" + quantityVar + ingredients.getString(nameIndex) + "</font>");
            ingredients.moveToNext();
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        ImageView recipeImage = dialog.findViewById(R.id.recipe_img);
        try
        {
            InputStream ims = mContext.getAssets().open("images/recipes/" + recipe.getFileName());
            Drawable d = Drawable.createFromStream(ims, null);
            recipeImage.setImageDrawable(d);
            ims.close();
        }
        catch(IOException ex)
        {
        }

        Button closeButton = dialog.findViewById(R.id.closeRecipeButton);
        closeButton.setOnClickListener((e) -> {
            dialog.dismiss();
        });

        Button favoriteButton = dialog.findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener((e) -> {
            mainActivity.DB.toggleFavorite(recipe.getRecipeName());
            recipe.toggleFavorite();
            notifyDataSetChanged();
        });

        TextView recipeName = dialog.findViewById(R.id.recipeName);
        recipeName.setText(recipe.getRecipeName());
        TextView instructionField = dialog.findViewById(R.id.recipeField);
        instructionField.setText(recipe.getInstructions());
        TextView ingredientsField = dialog.findViewById(R.id.ingredientsField);
        ingredientsField.setText(Html.fromHtml(StringUtils.join(ingredientList, "<br>")));

        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    public void filterView(String string) {
        filterString = string;
        recipeItems = unfilteredItems.stream().filter(n -> n.getRecipeName().toLowerCase().contains(string.toLowerCase())).collect(Collectors.toList());
        if (onlyFavorites) recipeItems = recipeItems.stream().filter(n -> n.getIsFavorite()).collect(Collectors.toList());
        if (filterIngredients.size() > 0) {
            MainActivity mainActivity = (MainActivity)mContext;
            for (String ingredient : filterIngredients) {
                recipeItems = recipeItems.stream().filter(n -> mainActivity.DB.isIngredientInRecipe(ingredient, n.getRecipeName())).collect(Collectors.toList());
            }
        }
        notifyDataSetChanged();
    }

    public void toggleFavorites() {
        onlyFavorites = !onlyFavorites;
        filterView(filterString);
    }

    public void setFilterIngredients(List<String> ingredients) {
        filterIngredients = ingredients;
        filterView(filterString);
    }
}
