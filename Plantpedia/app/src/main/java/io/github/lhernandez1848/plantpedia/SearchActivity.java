package io.github.lhernandez1848.plantpedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    private PlantDB db;
    private EditText plantNameSearch;
    private Button btnSearch;
    private ListView itemsListView;
    public static String selectedPlant = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_screen);

        plantNameSearch = (EditText) findViewById(R.id.plantSearchInput);
        btnSearch = (Button) findViewById(R.id.btnSearchPlant);
        btnSearch.setOnClickListener(this);

        itemsListView = (ListView) findViewById(R.id.itemsListView);

        db = new PlantDB(this);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSearchPlant) {
            String sPlantName = plantNameSearch.getText().toString();
            if (sPlantName.isEmpty()) {
                Toast.makeText(this, "Cannot search for an empty value",
                        Toast.LENGTH_SHORT).show();
            } else {
                try {
                    loadReasults(sPlantName);
                    hideSoftKeyboard(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadReasults(String sPlantName){
        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data = db.getPlantName(sPlantName);

        // create the resource, from, and to variables
        int resource = R.layout.listview_item;
        String[] from = {"name", "comments"};
        int[] to = {R.id.nameTextView, R.id.commentsTextView};

        // create and set the adapter
        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);

        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i,
                                    long id) {
                selectedPlant = adapterView.getItemAtPosition(i).toString();
                selectedPlant = selectedPlant.substring(selectedPlant.indexOf("name=") + 5,
                        selectedPlant.indexOf("}"));

                Intent search = new Intent(getApplicationContext(), SelectedPlant.class); //'this' is Activity A
                search.putExtra("FROM_ACTIVITY", "SEARCH");
                startActivity(search);

//                startActivity(new Intent(
//                        getApplicationContext(), SelectedPlant.class));
            }
        });
    }

    public static String getSelectedPlantFromSearch(){
        return selectedPlant;
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}