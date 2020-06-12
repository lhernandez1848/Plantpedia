package io.github.lhernandez1848.plantpedia;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class MainActivityFragment extends Fragment implements View.OnClickListener {

    private ViewGroup container;
    private LayoutInflater inflater;
    GlobalMethods globalMethods;

    Toolbar toolbar;
    RelativeLayout layout_main;
    private ImageButton btnAddPlantsFromHome, btnSearchPlantsList, btnAllPlantsList;

    public MainActivityFragment() {
    }


    private View initializeUserInterface() {
        View view;

        if(container!=null){
            container.removeAllViewsInLayout();
        }

        int orientation = Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            view = inflater.inflate(R.layout.fragment_activity_main_vertical, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_activity_main_horizontal, container, false);
        }

        globalMethods = new GlobalMethods(getContext());

        toolbar = (Toolbar) view.findViewById(R.id.homeToolbar);
        btnAddPlantsFromHome = (ImageButton) view.findViewById(R.id.btnAddPlantsFromHome);
        btnSearchPlantsList = (ImageButton) view.findViewById(R.id.btnSearchPlantsList);
        btnAllPlantsList = (ImageButton) view.findViewById(R.id.btnAllPlantsList);
        layout_main = (RelativeLayout) view.findViewById( R.id.mainLayout);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnAddPlantsFromHome.setOnClickListener(this);
        btnSearchPlantsList.setOnClickListener(this);
        btnAllPlantsList.setOnClickListener(this);

        return view;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Instantiate our container and inflater handles.
        this.container = container;
        this.inflater = inflater;

        setHasOptionsMenu(true);

        // Display the desired layout and return the view.
        return initializeUserInterface();
    }

    // This is called when the user rotates the device.
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        // Create the new layout.
        View view = initializeUserInterface();

        // Display the new layout on the screen.
        container.addView(view);

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAddPlantsFromHome){
            startActivity(new Intent(getContext(), AddPlantActivity.class));
        } else if (view.getId() == R.id.btnAllPlantsList){
            startActivity(new Intent(getContext(), ViewAllActivity.class));
        } else if (view.getId() == R.id.btnSearchPlantsList){
            startActivity(new Intent(getContext(), SearchActivity.class));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnSearch:
                startActivity(new Intent(
                        getContext(), SearchActivity.class));
                return true;
            case R.id.btnAddPlant:
                startActivity(new Intent(
                        getContext(), AddPlantActivity.class));
                return true;
            case R.id.btnViewAll:
                startActivity(new Intent(
                        getContext(), ViewAllActivity.class));
                return true;
            case R.id.btnWaterRecommendations:
                startActivity(new Intent(
                        getContext(), RecommendationsActivity.class));
                return true;
            case R.id.btnLogOut:
                globalMethods.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
