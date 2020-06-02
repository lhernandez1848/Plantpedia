package io.github.lhernandez1848.plantpedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

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

import io.github.lhernandez1848.plantpedia.adapters.RecommendationsAdapter;
import io.github.lhernandez1848.plantpedia.models.Plant;

public class RecommendationsActivity extends AppCompatActivity implements WaterDateDialog.WaterDateDialogListener {

    // declare classes, adapters, models
    private DatabaseReference databaseReference;
    private RecommendationsAdapter recListAdapter;
    private GlobalMethods globalMethods;

    // declare variables
    ArrayList<Plant> plantList;
    int lwDayDB, lwMonthDB, lwYearDB;
    String plantName, userId, resultCount;

    // declare widgets
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView recommendationResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        setTitle("Watering Recommendations");

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userId = FirebaseAuth.getInstance().getUid();

        resultCount = "No plants need water today";

        // initialize widgets
        toolbar = (Toolbar) findViewById(R.id.recommendationToolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recommendRecyclerView);
        recommendationResult = (TextView) findViewById(R.id.recommendationResult);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plantList = new ArrayList<>();
        recListAdapter = new RecommendationsAdapter(this, plantList);

        setSupportActionBar(toolbar);

        // initialize and display home icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        globalMethods = new GlobalMethods(this);

        loadRecommendations();
    }

    // get data from database and load corresponding fields
    private void loadRecommendations() {
        recyclerView.setAdapter(recListAdapter);

        Query query = databaseReference.child("plant").child(FirebaseAuth.getInstance().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // loop through result list
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String nameDB = data.child("name").getValue().toString();
                    String lastWateredDB = data.child("dateWatered").getValue().toString();
                    String waterFreqDB = data.child("waterFrequency").getValue().toString();
                    String imageDB = data.child("image").getValue().toString();

                    String[] sDate = lastWateredDB.split("/");
                    lwDayDB = Integer.parseInt(sDate[0]);
                    lwMonthDB = Integer.parseInt(sDate[1]);
                    lwYearDB = Integer.parseInt(sDate[2]);

                    // check if watering day conditions are met
                    if (globalMethods.isWateringDay(lwDayDB, lwMonthDB, lwYearDB, Integer.parseInt(waterFreqDB))){
                        String dayTempDB = data.child("dayTemp").getValue().toString();
                        String nightTempDB = data.child("nightTemp").getValue().toString();
                        String humStartDB = data.child("startHumidity").getValue().toString();
                        String humEndDB = data.child("endHumidity").getValue().toString();
                        String commentDB = data.child("comment").getValue().toString();

                        Plant plant = new Plant(nameDB, 0, 0, Integer.parseInt(dayTempDB)
                                , Integer.parseInt(nightTempDB), Integer.parseInt(humStartDB),
                                Integer.parseInt(humEndDB), lastWateredDB,
                                Integer.parseInt(waterFreqDB), commentDB, imageDB);

                        plantList.add(plant);

                        recListAdapter.notifyDataSetChanged();

                        recListAdapter.SetOnRecAdapterListener((view, name) -> {
                            plantName = name;

                            globalMethods.changeWaterDateDialog(getSupportFragmentManager());
                        });

                        if (recListAdapter.getItemCount() > 0) {
                            resultCount = recListAdapter.getItemCount() + " plant(s) to water";
                        }
                    }

                    recommendationResult.setText(resultCount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});

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
    public void applyWaterDate() {
        globalMethods.updateDatabase(databaseReference, plantName, userId);

        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

}
