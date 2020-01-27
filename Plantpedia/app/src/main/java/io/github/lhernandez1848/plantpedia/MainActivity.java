package io.github.lhernandez1848.plantpedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener{

    private Button addPlant;
    private Button viewAllPlants;
    private Button searchPlants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addPlant = (Button) findViewById(R.id.btnAddPlantsFromHome);
        viewAllPlants = (Button) findViewById(R.id.btnAllPlantsList);
        searchPlants = (Button) findViewById(R.id.btnSearchPlantsList);

        addPlant.setOnClickListener(this);
        viewAllPlants.setOnClickListener(this);
        searchPlants.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAddPlantsFromHome) {
            startActivity(new Intent(
                    getApplicationContext(), AddPlant.class));
        }
        if (v.getId() == R.id.btnAllPlantsList) {
            startActivity(new Intent(
                    getApplicationContext(), ViewAllPlants.class));
        }
        if (v.getId() == R.id.btnSearchPlantsList) {
            startActivity(new Intent(
                    getApplicationContext(), SearchActivity.class));
        }
    }

}
