package io.github.lhernandez1848.plantpedia;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

import java.util.Calendar;
import java.util.Map;

import io.github.lhernandez1848.plantpedia.models.Plant;

public class AddPlantActivity extends AppCompatActivity
        implements View.OnClickListener, ChooseImageMethodDialog.ChooseImageMethodDialogListener,
        AdapterView.OnItemSelectedListener, View.OnFocusChangeListener {

    // declare widgets
    private ImageView imageView;
    private Button addPlantToDB;
    private TextView lastWatered, nameError;
    private EditText nameAdd, waterAdd, tempStart, tempEnd, humidityStart, humidityEnd, commentAdd;
    private Spinner typeAdd, sunAdd;
    private Toolbar toolbar;

    // declare variables
    private String[] typeSpinnerFields, sunlightSpinnerFields;
    private String lastWateredDateSet, imageUri;
    private int type_id, sunlight_id, plantWater, dayTemp, nightTemp, humStart, humEnd, currentYear,
            currentMonth, currentDay, selectedYear, selectedMonth, selectedDay;

    // declare classes
    private DatabaseReference databaseReference;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private GlobalMethods globalMethods;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        setTitle("Add Plant");

        // initialize and display toolbar
        toolbar = findViewById(R.id.addToolbar);
        setSupportActionBar(toolbar);

        // initialize and display home icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        userId = FirebaseAuth.getInstance().getUid();

        // initialization of widgets
        imageView = (ImageView) findViewById(R.id.addImageView);
        addPlantToDB = (Button) findViewById(R.id.btnAddPlantToDB);
        lastWatered = (TextView) findViewById(R.id.txtLastWateredDatePicker);
        nameError = (TextView) findViewById(R.id.etNameError);
        nameAdd = (EditText) findViewById(R.id.addNameEditText);
        waterAdd = (EditText) findViewById(R.id.addWaterEditText);
        tempStart = (EditText) findViewById(R.id.etTempDay);
        tempEnd = (EditText) findViewById(R.id.etTempNight);
        humidityStart = (EditText) findViewById(R.id.etHumidityStart);
        humidityEnd = (EditText) findViewById(R.id.etHumidityEnd);
        commentAdd = (EditText) findViewById(R.id.addCommentEditText);
        typeAdd = (Spinner) findViewById(R.id.addTypeSpinner);
        sunAdd = (Spinner) findViewById(R.id.addSunSpinner);

        // initialization of variables
        typeSpinnerFields = new String[]{"Select Plant Type", "Annual", "Bamboo", "Bulb",
            "Cactus - Succulent", "Climber", "Conifer", "Fern", "Fruit", "Grass", "Herb",
            "Palm - Cycad", "Perennial", "Rose", "Shrub", "Tree"};
        sunlightSpinnerFields = new String[]{"Select Sunlight Requirement", "Dense Shade",
            "Full Shade", "Full Sun", "Light Shade", "Partial Shade"};
        lastWateredDateSet = "";
        type_id = 0;
        sunlight_id = 0;
        plantWater = 0;
        dayTemp = 0;
        nightTemp = 0;
        humStart = 0;
        humEnd = 0;

        // set listeners
        lastWatered.setOnClickListener(this);
        imageView.setOnClickListener(this);
        addPlantToDB.setOnClickListener(this);
        typeAdd.setOnItemSelectedListener(this);
        sunAdd.setOnItemSelectedListener(this);
        nameAdd.setOnFocusChangeListener(this);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        globalMethods = new GlobalMethods(this);

        loadSpinners();
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
    public void onClick(View view) {

        if (view.getId() == R.id.addImageView){
            showImageMethodDialog();
        } else if (view.getId() == R.id.btnAddPlantToDB){
            getPlantInfoToAdd();
        } else if(view.getId() == R.id.txtLastWateredDatePicker){
            launchDatePicker();
        }
    }

    public void launchDatePicker(){
        final Calendar calendar = Calendar.getInstance();

        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                selectedDay = day;
                selectedMonth = month + 1;
                selectedYear = year;
                lastWateredDateSet = selectedDay + "/" + selectedMonth + "/" + selectedYear;

                lastWatered.setText(lastWateredDateSet);
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                R.style.DateSelector, dateSetListener,
                currentYear, currentMonth, currentDay);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    // get fields for adding to database
    public void getPlantInfoToAdd(){
        String sPlantName = nameAdd.getText().toString();
        String sPlantComment = commentAdd.getText().toString();

        getNumberInputs();

        if(sPlantName.equals("")){
            Toast.makeText(this, "Plant name is required",
                    Toast.LENGTH_SHORT).show();
            nameError.setVisibility(View.VISIBLE);
        }
        else {
            try {
                imageUri = insertImage(sPlantName).toString();
            } catch (Exception e){
                imageUri = "";
            }

            writeNewPlant(sPlantName,type_id, sunlight_id,dayTemp, nightTemp, humStart,
                    humEnd, lastWateredDateSet, plantWater, sPlantComment, imageUri);
        }
    }

    // get number fields or leave as zero
    private void getNumberInputs() {
        try {
            plantWater = Integer.parseInt(waterAdd.getText().toString());
        } catch (NumberFormatException e){ }

        try {
            dayTemp = Integer.parseInt(tempStart.getText().toString());
        } catch (NumberFormatException e){ }

        try {
            nightTemp = Integer.parseInt(tempEnd.getText().toString());
        } catch (NumberFormatException e){ }

        try {
            humStart = Integer.parseInt(humidityStart.getText().toString());
        } catch (NumberFormatException e){ }

        try {
            humEnd = Integer.parseInt(humidityEnd.getText().toString());
        } catch (NumberFormatException e){ }

    }

    // write plant data to the database
    private void writeNewPlant(final String name, final int typeId, final int sunlightId,
                               final int dayTemp, final int nightTemp, final int startHumidity,
                               final int endHumidity, final String dateWatered,
                               final int waterFrequency, final String comment,
                               final String imageURI) {

        Query lastQuery = databaseReference.child("plant").child(userId).orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    int keyInt = (Integer.parseInt(data.getKey())) + 1;
                    String key = Integer.toString(keyInt);

                    Plant plant = new Plant(name, typeId, sunlightId, dayTemp, nightTemp, startHumidity,
                            endHumidity, dateWatered, waterFrequency, comment, imageURI);

                  //  Map<String, Object> childUpdates = new HashMap<>();

                    Map<String, Object> detailValues = plant.toPlantDetails();
                   // childUpdates.put(key, detailValues);

                    databaseReference.child("plant").child(userId).child(key).setValue(detailValues)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Write was successful!
                                    Toast.makeText(getApplicationContext(), "Plant added", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Write failed
                                    Snackbar.make(findViewById(R.id.addPlantLayout), "ERROR: Plant not added", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});
    }


    // Intent for picking a photo from Gallery
    private void chooseImageIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_LOAD_IMAGE);
    }

    // Launches device camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // set image view once picture is selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if photo comes from device camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap takeImageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(takeImageBitmap);
        }
        // if photo comes from device gallery
        if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            try {
                Bitmap loadImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imageView.setImageBitmap(loadImageBitmap);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //insert image to storage device
    public Uri insertImage(String sPlantName){
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        String savedImage = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, sPlantName , null);
        return Uri.parse(savedImage);
    }

    // show dialog for choosing image method
    private void showImageMethodDialog() {
        ChooseImageMethodDialog chooseImageMethodDialog = new ChooseImageMethodDialog();

        chooseImageMethodDialog.show(getSupportFragmentManager(), "Choose Image Method");
    }

    // apply image method choice
    @Override
    public void applyImageMethodChoice(String imageMethodChoice) {
        if (imageMethodChoice.equals("gallery")){
            chooseImageIntent();
        } else if (imageMethodChoice.equals("camera")){
            dispatchTakePictureIntent();
        }
    }

    // load the spinners
    public void loadSpinners(){
        ArrayAdapter<String> typeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, typeSpinnerFields);
        ArrayAdapter<String> sunlightArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, sunlightSpinnerFields);

        typeAdd.setAdapter(typeArrayAdapter);
        sunAdd.setAdapter(sunlightArrayAdapter);
    }

    // spinner item selected
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //save selected items to variables
        if (adapterView.getId() == R.id.addTypeSpinner){
            type_id = (int) adapterView.getItemIdAtPosition(i);
        }
        if (adapterView.getId() == R.id.addSunSpinner){
            sunlight_id = (int) adapterView.getItemIdAtPosition(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    // hide name error message if name is not blank
    @Override
    public void onFocusChange(View view, boolean b) {
        if (view.getId() == R.id.addNameEditText){

            if ((nameAdd.getText().toString()).length()>0){
                nameError.setVisibility(View.GONE);
            }
        }
    }
}
