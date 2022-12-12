package com.example.homebarflyapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private RecipeRVAdapter adapter;
    public DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB = new DBHelper(this);
        DB.initDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_main);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, RecipeFragment.class, null);
            transaction.commit();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch(item.getItemId()) {
            case R.id.nav_recipes:
                transaction.setReorderingAllowed(true);
                transaction.replace(R.id.fragment_container, RecipeFragment.class, null);
                transaction.commit();
                break;
            case R.id.nav_ingredients:
                transaction.setReorderingAllowed(true);
                transaction.replace(R.id.fragment_container, MyBarFragment.class, null);
                transaction.commit();
                break;
            case R.id.nav_suggestions:
                transaction.setReorderingAllowed(true);
                transaction.replace(R.id.fragment_container, SuggestionsFragment.class, null);
                transaction.commit();
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void reloadFragment(String fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch(fragment) {
            case "recipe":
                transaction.setReorderingAllowed(true);
                transaction.replace(R.id.fragment_container, RecipeFragment.class, null);
                transaction.commit();
                break;
            case "mybar":
                transaction.setReorderingAllowed(true);
                transaction.replace(R.id.fragment_container, MyBarFragment.class, null);
                transaction.commit();
                break;
            case "suggestion":
                transaction.setReorderingAllowed(true);
                transaction.replace(R.id.fragment_container, SuggestionsFragment.class, null);
                transaction.commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }
}