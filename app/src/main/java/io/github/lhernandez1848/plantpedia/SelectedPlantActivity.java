package io.github.lhernandez1848.plantpedia;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

public class SelectedPlantActivity extends AppCompatActivity implements View.OnClickListener,
        WaterDateDialog.WaterDateDialogListener {


    private ImageView imageView, notificationAlert;
    private TextView nameTextView, typeTextView, tvHumidity, sunTextView, waterTextView,
            temperatureTextView, commentTextView, tvLastWateredDate;
    private Button btnEditPlantInfo, deletePlant;
    Toolbar toolbar;

    // declaration of variables
    String plantName, userId, typeName, sunlight, activityFrom;

    // declare classes
    private DatabaseReference databaseReference;
    private GlobalMethods globalMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_plant);

        Intent intent = getIntent();
        plantName = intent.getStringExtra("plantName");
        activityFrom = intent.getStringExtra("activity");
        setTitle(plantName);

        // initialize and display toolbar
        toolbar = findViewById(R.id.selectedToolbar);
        setSupportActionBar(toolbar);

        // initialize and display home icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        nameTextView = (TextView) findViewById(R.id.plantNameTextView);
        typeTextView = (TextView) findViewById(R.id.plantTypeTextView);
        tvHumidity = (TextView) findViewById(R.id.tvHumidity);
        sunTextView = (TextView) findViewById(R.id.plantSunRequirementsTextView);
        waterTextView = (TextView) findViewById(R.id.plantWaterTextView);
        temperatureTextView = (TextView) findViewById(R.id.plantTemperatureTextView);
        commentTextView = (TextView) findViewById(R.id.plantCommentsTextView);
        tvLastWateredDate = (TextView) findViewById(R.id.tvLastWateredDate);
        imageView = (ImageView) findViewById(R.id.plantImageView);
        notificationAlert = (ImageView) findViewById(R.id.btnNotificationAlert);

        btnEditPlantInfo = (Button) findViewById(R.id.btnEditPlantInfo);
        deletePlant = (Button) findViewById(R.id.btnDeletePlant);

        btnEditPlantInfo.setOnClickListener(this);
        deletePlant.setOnClickListener(this);
        notificationAlert.setOnClickListener(this);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        globalMethods = new GlobalMethods(this);

        userId = FirebaseAuth.getInstance().getUid();

        if (activityFrom.equals("RecommendationsActivity")){
            notificationAlert.setVisibility(View.VISIBLE);
        }

        loadPlantData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnEditPlantInfo){
            // save order details to intent
            Intent intent = new Intent(getApplicationContext(), EditPlantActivity.class);
            intent.putExtra("plantName", plantName);

            // call order details activity
            startActivity(intent);
        }
        if(view.getId() == R.id.btnDeletePlant) {
            deletePlantPopup();
        }
        if (view.getId() == R.id.btnNotificationAlert){
            changeWaterDateDialog();
        }
    }

    private void changeWaterDateDialog() {
        WaterDateDialog waterDateDialog = new WaterDateDialog();

        waterDateDialog.show(getSupportFragmentManager(), "Water Date");
    }

    @Override
    public void applyWaterDate() {
        Calendar c = Calendar.getInstance();
        String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        String year = Integer.toString(c.get(Calendar.YEAR));
        final String newDateWatered = day + "/" + month + "/" + year;

        Query query = databaseReference.child("plant").child(userId).orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    String nameDB = Objects.requireNonNull(data.child("name").getValue()).toString();

                    if (nameDB.equals(plantName)) {
                        int keyInt = Integer.parseInt(data.getKey());
                        String key = Integer.toString(keyInt);

                        databaseReference.child("plant").child(userId).child(key).child("dateWatered").setValue(newDateWatered)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Write was successful!
                                        Toast.makeText(getApplicationContext(), "Water date changed", Toast.LENGTH_SHORT).show();
                                        loadPlantData();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Write failed
                                        Snackbar.make(findViewById(R.id.addPlantLayout), "ERROR: Water date not changed", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});

        notificationAlert.setVisibility(View.GONE);
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

    private void loadPlantData() {
        Query query = databaseReference.child("plant").child(userId).orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    String nameDB = Objects.requireNonNull(data.child("name").getValue()).toString();

                    if (nameDB.equals(plantName)) {
                        String typeDB = Objects.requireNonNull(data.child("typeId").getValue()).toString();
                        String sunDB = Objects.requireNonNull(data.child("sunlightId").getValue()).toString();
                        String imageDB = Objects.requireNonNull(data.child("image").getValue()).toString();
                        String lastWateredDB = Objects.requireNonNull(data.child("dateWatered").getValue()).toString();
                        String waterFreqDB = Objects.requireNonNull(data.child("waterFrequency").getValue()).toString();
                        String dayTempDB = Objects.requireNonNull(data.child("dayTemp").getValue()).toString();
                        String nightTempDB = Objects.requireNonNull(data.child("nightTemp").getValue()).toString();
                        String humStartDB = Objects.requireNonNull(data.child("startHumidity").getValue()).toString();
                        String humEndDB = Objects.requireNonNull(data.child("endHumidity").getValue()).toString();
                        String commentDB = Objects.requireNonNull(data.child("comment").getValue()).toString();

                        loadTextFromIDs(typeDB, sunDB);

                        nameTextView.setText(nameDB);
                        typeTextView.setText(typeName);
                        tvHumidity.setText(humStartDB + "% to " + humEndDB + "%");
                        sunTextView.setText(sunlight);
                        waterTextView.setText(waterFreqDB + " day(s)");
                        temperatureTextView.setText(dayTempDB + "°C by day \n " + nightTempDB + "°C at night");
                        commentTextView.setText(commentDB);
                        tvLastWateredDate.setText(lastWateredDB);

                        Uri imageUri = Uri.parse(imageDB);

                        try {
                            ContentResolver contentResolver = getContentResolver();
                            ImageDecoder.Source imageSrc = ImageDecoder.createSource(contentResolver, imageUri);
                            Drawable drawable = ImageDecoder.decodeDrawable(imageSrc);
                            imageView.setImageDrawable(drawable);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});

    }


    public void loadTextFromIDs(String typeID, String sunID){

        // plant type
        switch (typeID){
            case "1":
                typeName = "Annual";
                break;
            case "2":
                typeName = "Bamboo";
                break;
            case "3":
                typeName = "Bulb";
                break;
            case "4":
                typeName = "Cactus - Succulent";
                break;
            case "5":
                typeName = "Climber";
                break;
            case "6":
                typeName = "Conifer";
                break;
            case "7":
                typeName = "Fern";
                break;
            case "8":
                typeName = "Fruit";
                break;
            case "9":
                typeName = "Grass";
                break;
            case "10":
                typeName = "Herb";
                break;
            case "11":
                typeName = "Palm - Cycad";
                break;
            case "12":
                typeName = "Perennial";
                break;
            case "13":
                typeName = "Rose";
                break;
            case "14":
                typeName = "Shrub";
                break;
            case "15":
                typeName = "Tree";
                break;
                default:
                    typeName = "N/A";
                    break;
        }

        // sunlight needs
        switch (sunID){
            case "1":
                sunlight = "Dense Shade";
                break;
            case "2":
                sunlight = "Full Shade";
                break;
            case "3":
                sunlight = "Full Sun";
                break;
            case "4":
                sunlight = "Light Shade";
                break;
            case "5":
                sunlight = "Partial Shade";
                break;
            default:
                sunlight = "N/A";
                break;
        }
    }

    private void deletePlantPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete Plant");
        builder.setMessage("Are you sure you want to delete " + plantName);
        builder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // removeValue
                        Query query = databaseReference.child("plant").child(userId);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot data : dataSnapshot.getChildren()) {
                                    String nameDB = Objects.requireNonNull(data.child("name").getValue()).toString();

                                    if (nameDB.equals(plantName)) {
                                        String key = data.getKey();
                                        databaseReference.child("plant").child(userId).child(key).removeValue();

                                        Toast.makeText(getApplicationContext(),
                                                plantName + " was deleted from the database", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(
                                                getApplicationContext(), MainActivity.class));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }});

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setIcon(R.drawable.plant);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
