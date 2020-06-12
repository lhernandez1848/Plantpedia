package io.github.lhernandez1848.plantpedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.github.lhernandez1848.plantpedia.adapters.PlantListAdapter;
import io.github.lhernandez1848.plantpedia.models.Plant;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText plantNameSearch;
    private Button btnSearch;
    private RecyclerView recyclerView;
    private TextView resultCount;
    Toolbar toolbar;

    private DatabaseReference databaseReference;
    private PlantListAdapter plantListAdapter;
    private GlobalMethods globalMethods;

    ArrayList<Plant> plantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Search Plant");

        // initialize and display toolbar
        toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);

        // initialize and display home icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize widgets
        recyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);
        plantNameSearch = (EditText) findViewById(R.id.plantSearchInput);
        resultCount = (TextView) findViewById(R.id.searchResultCount);
        btnSearch = (Button) findViewById(R.id.btnSearchPlant);

        // set listeners
        btnSearch.setOnClickListener(this);

        // recyclerView settings
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plantList = new ArrayList<>();
        plantListAdapter = new PlantListAdapter(this, plantList);
        recyclerView.setAdapter(plantListAdapter);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        globalMethods = new GlobalMethods(this);
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

    public void loadReasults(final String sPlantName){
        Query query = databaseReference.child("plant").child(FirebaseAuth.getInstance().getUid()).orderByChild("name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String resultMessage = "No plants found";

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String nameDB = data.child("name").getValue().toString();
                    String regexName = ".*" + sPlantName.toLowerCase() + ".*";

                    if (nameDB.toLowerCase().matches(regexName)){
                        String lastWateredDB = data.child("dateWatered").getValue().toString();
                        String waterFreqDB = data.child("waterFrequency").getValue().toString();
                        String dayTempDB = data.child("dayTemp").getValue().toString();
                        String nightTempDB = data.child("nightTemp").getValue().toString();
                        String humStartDB = data.child("startHumidity").getValue().toString();
                        String humEndDB = data.child("endHumidity").getValue().toString();
                        String commentDB = data.child("comment").getValue().toString();
                        String imageDB = data.child("image").getValue().toString();

                        Plant plant = new Plant(nameDB, 0, 0, Integer.parseInt(dayTempDB)
                                , Integer.parseInt(nightTempDB), Integer.parseInt(humStartDB),
                                Integer.parseInt(humEndDB), lastWateredDB,
                                Integer.parseInt(waterFreqDB), commentDB, imageDB);

                        plantList.add(plant);

                        plantListAdapter.notifyDataSetChanged();
                    }
                }

                if (plantListAdapter.getItemCount() > 0 ) {
                    resultMessage = plantListAdapter.getItemCount() + " plant(s) found";
                }

                resultCount.setText(resultMessage);
                resultCount.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}