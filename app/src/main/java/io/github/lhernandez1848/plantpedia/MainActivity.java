package io.github.lhernandez1848.plantpedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener{

    private ImageButton addPlant, viewAllPlants, searchPlants;
    Toolbar toolbar;

    private GlobalMethods globalMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");

        // initialize and display toolbar
        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        addPlant = (ImageButton) findViewById(R.id.btnAddPlantsFromHome);
        viewAllPlants = (ImageButton) findViewById(R.id.btnAllPlantsList);
        searchPlants = (ImageButton) findViewById(R.id.btnSearchPlantsList);

        globalMethods = new GlobalMethods(this);

        addPlant.setOnClickListener(this);
        viewAllPlants.setOnClickListener(this);
        searchPlants.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAddPlantsFromHome) {
            startActivity(new Intent(
                    getApplicationContext(), AddPlantActivity.class));
        }
        if (v.getId() == R.id.btnAllPlantsList) {
            startActivity(new Intent(
                    getApplicationContext(), ViewAllActivity.class));
        }
        if (v.getId() == R.id.btnSearchPlantsList) {
            startActivity(new Intent(
                    getApplicationContext(), SearchActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnSearch:
                startActivity(new Intent(
                        getApplicationContext(), SearchActivity.class));
                return true;
            case R.id.btnAddPlant:
                startActivity(new Intent(
                        getApplicationContext(), AddPlantActivity.class));
                return true;
            case R.id.btnViewAll:
                startActivity(new Intent(
                        getApplicationContext(), ViewAllActivity.class));
                return true;
            case R.id.btnWaterRecommendations:
                startActivity(new Intent(
                        getApplicationContext(), RecommendationsActivity.class));
                return true;
            case R.id.btnLogOut:
                globalMethods.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
