package io.github.lhernandez1848.plantpedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

import org.joda.time.DateTimeComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.github.lhernandez1848.plantpedia.adapters.PlantListAdapter;
import io.github.lhernandez1848.plantpedia.models.Plant;

public class RecommendationsActivity extends AppCompatActivity {

    // declare classes, adapters, models
    private DatabaseReference databaseReference;
    private PlantListAdapter plantListAdapter;
    private GlobalMethods globalMethods;

    // declare variables
    ArrayList<Plant> plantList;
    int currentDay, currentMonth, currentYear, lwDayDB, lwMonthDB, lwYearDB;

    // declare widgets
    Toolbar toolbar;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        setTitle("Watering Recommendations");

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // initialize widgets
        toolbar = (Toolbar) findViewById(R.id.recommendationToolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recommendRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plantList = new ArrayList<>();
        plantListAdapter = new PlantListAdapter(this, plantList);

        setSupportActionBar(toolbar);
        recyclerView.setAdapter(plantListAdapter);

        // initialize and display home icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        globalMethods = new GlobalMethods(this);

        loadRecommendations();
    }

    private void loadRecommendations() {
        Query query = databaseReference.child("plant").child(FirebaseAuth.getInstance().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String nameDB = data.child("name").getValue().toString();
                    String lastWateredDB = data.child("dateWatered").getValue().toString();
                    String waterFreqDB = data.child("waterFrequency").getValue().toString();
                    String imageDB = data.child("image").getValue().toString();

                    String[] sDate = lastWateredDB.split("/");
                    lwDayDB = Integer.parseInt(sDate[0]);
                    lwMonthDB = Integer.parseInt(sDate[1]);
                    lwYearDB = Integer.parseInt(sDate[2]);

                    if (isWateringDay(lwDayDB, lwMonthDB, lwYearDB, Integer.parseInt(waterFreqDB))){
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

                        plantListAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});

    }

    public Boolean isWateringDay(int day, int month, int year, int waterFreq){
        Date cDate = new Date();
        Date lwDate = new GregorianCalendar(year, month - 1, day).getTime();

        Calendar waterCalendar = Calendar.getInstance();
        waterCalendar.setTime(lwDate);
        waterCalendar.add(Calendar.DAY_OF_MONTH, waterFreq);

        Date rDate = waterCalendar.getTime();

        return DateTimeComparator.getDateOnlyInstance().compare(cDate, rDate) == 0;
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
