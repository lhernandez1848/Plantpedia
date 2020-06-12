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

import io.github.lhernandez1848.plantpedia.adapters.PlantListAdapter;
import io.github.lhernandez1848.plantpedia.models.Plant;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView viewAllResultCount;
    Toolbar toolbar;

    // declare classes, adapters, models
    private DatabaseReference databaseReference;
    private PlantListAdapter plantListAdapter;
    private GlobalMethods globalMethods;

    // declare variables
    ArrayList<Plant> plantList;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        setTitle("View All");

        // initialize and display toolbar
        toolbar = findViewById(R.id.viewAllToolbar);
        setSupportActionBar(toolbar);

        // initialize and display home icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize widgets
        recyclerView = (RecyclerView) findViewById(R.id.allRecyclerView);
        viewAllResultCount = (TextView) findViewById(R.id.viewAllResultCount);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        globalMethods = new GlobalMethods(this);

        // recyclerView settings
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plantList = new ArrayList<>();
        plantListAdapter = new PlantListAdapter(this, plantList);
        recyclerView.setAdapter(plantListAdapter);

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

    public void loadReasults(){
        Query query = databaseReference.child("plant").child(FirebaseAuth.getInstance().getUid()).orderByChild("name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String resultMessage = "You haven't added any plants yet. Try adding some now!";

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String nameDB = data.child("name").getValue().toString();
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

                if (plantListAdapter.getItemCount() > 0) {
                    resultMessage = plantListAdapter.getItemCount() + " plant(s) in database";
                }
                viewAllResultCount.setText(resultMessage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});
    }

}
