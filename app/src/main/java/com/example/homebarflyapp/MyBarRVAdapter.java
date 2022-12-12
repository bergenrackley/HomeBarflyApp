package com.example.homebarflyapp;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homebarfly.model.AddIngredient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyBarRVAdapter extends RecyclerView.Adapter<MyBarRVAdapter.ViewHolder>{

    private List<AddIngredient> ingredientItems;
    private int selectedPos = RecyclerView.NO_POSITION;
    private Context mContext;
    private boolean editMode = false;

    public MyBarRVAdapter(List<AddIngredient> listItems) {
        ingredientItems = listItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameTextView;
        public ConstraintLayout gridLayout;
        public ImageView deleteIngredientIcon;
        public ImageView gridOverlay;
        public ImageView ingredientImage;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView)itemView.findViewById(R.id.ingredientIconName);
            gridLayout = (ConstraintLayout)itemView.findViewById((R.id.ingredientLayout));
            deleteIngredientIcon = (ImageView)itemView.findViewById(R.id.deleteIngredientIcon);
            gridOverlay = (ImageView)itemView.findViewById(R.id.myBarOverlay);
            ingredientImage = (ImageView)itemView.findViewById(R.id.ingredientIcon);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getBindingAdapterPosition() == RecyclerView.NO_POSITION) return;

            notifyItemChanged(selectedPos);
            selectedPos = getBindingAdapterPosition();
            notifyItemChanged(selectedPos);

            if (editMode) {
                if (ingredientItems.get(selectedPos).getIngredientName() == "add_ingredient_placeholder") {
                    showAddIngredientDialog(view);
                } else {
                    if (ingredientItems.get(selectedPos).getState()) {
                        MainActivity mainActivity = (MainActivity)mContext;
                        mainActivity.DB.removeMyBar(ingredientItems.get(selectedPos).getIngredientName());
                        ingredientItems.remove(selectedPos);
                        selectedPos = RecyclerView.NO_POSITION;
                        notifyDataSetChanged();
                    }
                }
            }

        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View contactView = inflater.inflate(R.layout.mybar_rv_grid, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AddIngredient ingredientItem = ingredientItems.get(position);

        TextView nameView = holder.nameTextView;
        nameView.setText(ingredientItem.getIngredientName());
        ImageView gridOverlay = holder.gridOverlay;
        ImageView deleteIngredientIcon = holder.deleteIngredientIcon;
        ImageView ingredientImage = holder.ingredientImage;

        try
        {
            InputStream ims = mContext.getAssets().open("images/ingredients/" + ingredientItem.getFileName());
            Drawable d = Drawable.createFromStream(ims, null);
            ingredientImage.setImageDrawable(d);
            ims.close();
        }
        catch(IOException ex)
        {
        }

        //ingredientImage.setImageResource(R.drawable.null_shape);

        ingredientItem.setState(selectedPos == position);

        if (editMode) {
            if (ingredientItem.getIngredientName() == "add_ingredient_placeholder") {
                ingredientImage.setImageResource(R.drawable.null_shape);
                deleteIngredientIcon.setVisibility(View.VISIBLE);
                deleteIngredientIcon.setImageResource(R.drawable.ic_baseline_add_24);
                gridOverlay.setVisibility(View.INVISIBLE);
                nameView.setVisibility(View.INVISIBLE);
            } else if (selectedPos == position) {
                gridOverlay.setVisibility(View.VISIBLE);
                deleteIngredientIcon.setVisibility(View.VISIBLE);
                nameView.setVisibility(View.INVISIBLE);
            } else {
                gridOverlay.setVisibility(View.INVISIBLE);
                deleteIngredientIcon.setVisibility(View.INVISIBLE);
                nameView.setVisibility(View.VISIBLE);
            }
        } else {
            gridOverlay.setVisibility(View.INVISIBLE);
            deleteIngredientIcon.setVisibility(View.INVISIBLE);
            nameView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return ingredientItems.size();
    }

    private void showAddIngredientDialog(View view) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.addingredient_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        MainActivity mainActivity = (MainActivity)mContext;
        Cursor ingredients = mainActivity.DB.getIngredients();
        List<AddIngredient> data = new ArrayList<AddIngredient>();
        ingredients.moveToFirst();
        int nameIndex = ingredients.getColumnIndex("Ingredients.name");
        int typeIndex = ingredients.getColumnIndex("Ingredients.type");
        int fileNameIndex = ingredients.getColumnIndex("Ingredients.fileName");
        while (!ingredients.isAfterLast()) {
            data.add( new AddIngredient(ingredients.getString(nameIndex), ingredients.getString(typeIndex), ingredients.getString(fileNameIndex)));
            ingredients.moveToNext();
        }

        Collections.sort(data);

        RecyclerView recyclerView = dialog.findViewById(R.id.ingredientSearchRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        AddIngredientRVAdapter adapter = new AddIngredientRVAdapter(data);
        recyclerView.setAdapter(adapter);

        SearchView searchView = dialog.findViewById(R.id.ingredientSearchBox);
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

        Button closeButton = dialog.findViewById(R.id.closeIngredientButton);
        closeButton.setOnClickListener((e) -> {
            for (AddIngredient ingredient : adapter.getAddedIngredients()) {
                mainActivity.DB.insertMyBar(ingredient.getIngredientName(), ingredient.getIngredientType(), ingredient.getFileName());
            }
            dialog.dismiss();
            mainActivity.reloadFragment("mybar");
        });

        Button cancelButton = dialog.findViewById(R.id.cancelIngredientButton);
        cancelButton.setOnClickListener((e) -> {
            dialog.dismiss();
        });

        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    public void changeEditMode() {
        editMode = !editMode;
        if (editMode) {
            ingredientItems.add(0, new AddIngredient("add_ingredient_placeholder", "default.png"));
        } else {
            if (ingredientItems.get(0).getIngredientName() == "add_ingredient_placeholder") ingredientItems.remove(0);
        }
        selectedPos = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }
}
