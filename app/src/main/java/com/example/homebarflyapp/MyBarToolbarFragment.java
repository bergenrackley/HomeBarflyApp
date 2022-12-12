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

public class MyBarToolbarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mybar_toolbar_fragment, container, false);

        ImageView editIcon = view.findViewById(R.id.editMyBarIcon);
        editIcon.setOnClickListener((e) -> {
            FragmentManager fm = getParentFragmentManager();
            MyBarFragment fragm = (MyBarFragment)fm.findFragmentById(R.id.fragment_container);
            fragm.editMyBar(view);
        });

        return view;
    }
}
