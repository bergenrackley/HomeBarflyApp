package com.example.homebarflyapp;

import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homebarflyapp.model.AddIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyBarFragment extends Fragment {
    private boolean editMode = false;
    private View mView;
    List<AddIngredient> baseSpiritList = new ArrayList<AddIngredient>();
    List<AddIngredient> liqueurList = new ArrayList<AddIngredient>();
    List<AddIngredient> mixinsList = new ArrayList<AddIngredient>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.mybar_fragment, container, false);

        MainActivity mainActivity = (MainActivity)getActivity();

        FragmentTransaction toolbarTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
        toolbarTransaction.replace(R.id.toolbar_fragment_container, MyBarToolbarFragment.class, null);
        toolbarTransaction.commit();

        Cursor myBar = mainActivity.DB.getMyBar();
        myBar.moveToFirst();
        int nameIndex = myBar.getColumnIndex("MyBar.name");
        int typeIndex = myBar.getColumnIndex("MyBar.type");
        int fileNameIndex = myBar.getColumnIndex("MyBar.fileName");

        while (!myBar.isAfterLast()) {
            switch (myBar.getString(typeIndex)) {
                case "Base Spirit":
                    baseSpiritList.add(new AddIngredient(myBar.getString(nameIndex), myBar.getString(fileNameIndex)));
                    break;
                case "Liqueur":
                    liqueurList.add(new AddIngredient(myBar.getString(nameIndex), myBar.getString(fileNameIndex)));
                    break;
                case "Mix-in":
                    mixinsList.add(new AddIngredient(myBar.getString(nameIndex), myBar.getString(fileNameIndex)));
                    break;
                default:
                    break;
            }
            myBar.moveToNext();
        }
        RecyclerView baseSpiritsRV = mView.findViewById(R.id.baseSpiritsRV);
        setUpRV(baseSpiritsRV, baseSpiritList);
        RecyclerView liqueursRV = mView.findViewById(R.id.liqueursRV);
        setUpRV(liqueursRV, liqueurList);
        RecyclerView mixinsRV = mView.findViewById(R.id.mixinsRV);
        setUpRV(mixinsRV, mixinsList);

        return mView;
    }

    public void editMyBar(View toolBarView) {
        ImageView editIcon = toolBarView.findViewById(R.id.editMyBarIcon);
        RecyclerView baseSpiritsRV = mView.findViewById(R.id.baseSpiritsRV);
        RecyclerView liqueursRV = mView.findViewById(R.id.liqueursRV);
        RecyclerView mixinsRV = mView.findViewById(R.id.mixinsRV);

        editMode = !editMode;
        if (!editMode) {
            editIcon.setImageResource(R.drawable.ic_baseline_edit_24);
        } else {
            editIcon.setImageResource(R.drawable.ic_baseline_clear_24);
        }

        rvEditMode(baseSpiritsRV);
        rvEditMode(liqueursRV);
        rvEditMode(mixinsRV);

    }

    private void setUpRV(RecyclerView recyclerView, List<AddIngredient> dataSet) {
        Collections.sort(dataSet);

        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        MyBarRVAdapter adapter = new MyBarRVAdapter(dataSet);
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
    }

    private void rvEditMode(RecyclerView recyclerView) {
        MyBarRVAdapter adapter = (MyBarRVAdapter)recyclerView.getAdapter();
        adapter.changeEditMode();
    }

}
