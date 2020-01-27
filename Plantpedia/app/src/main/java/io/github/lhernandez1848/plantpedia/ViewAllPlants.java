package io.github.lhernandez1848.plantpedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewAllPlants extends AppCompatActivity {
    private PlantDB db;
    private ListView allPlantsListView;
    public static String selectedPlant = "";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_all_plants);

        allPlantsListView = (ListView) findViewById(R.id.viewAllListView);

        db = new PlantDB(this);

        loadReasults();
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
            case R.id.btnHome:
                startActivity(new Intent(
                        getApplicationContext(), MainActivity.class));
                return true;
            case R.id.btnSearch:
                startActivity(new Intent(
                        getApplicationContext(), SearchActivity.class));
                return true;
            case R.id.btnAddPlant:
                startActivity(new Intent(
                        getApplicationContext(), AddPlant.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadReasults(){
        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data = db.getAllPlants();

        // create the resource, from, and to variables
        int resource = R.layout.listview_item;
        String[] from = {"name", "comments"};
        int[] to = {R.id.nameTextView, R.id.commentsTextView};

        // create and set the adapter
        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        allPlantsListView.setAdapter(adapter);

        allPlantsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i,
                                    long id) {
                selectedPlant = adapterView.getItemAtPosition(i).toString();
                selectedPlant = selectedPlant.substring(selectedPlant.indexOf("name=") + 5,
                        selectedPlant.indexOf("}"));

                Intent viewAll = new Intent(getApplicationContext(), SelectedPlant.class); //'this' is Activity A
                viewAll.putExtra("FROM_ACTIVITY", "VIEWALL");
                startActivity(viewAll);
//
//                startActivity(new Intent(
//                        getApplicationContext(), SelectedPlant.class));
            }
        });
    }

    public static String getSelectedPlantFromViewAll(){
        return selectedPlant;
    }
}
