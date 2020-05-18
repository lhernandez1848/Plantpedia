package io.github.lhernandez1848.plantpedia;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import io.github.lhernandez1848.plantpedia.models.Plant;

public class EditPlantActivity extends AppCompatActivity implements View.OnClickListener,
        ChooseImageMethodDialog.ChooseImageMethodDialogListener,
        AdapterView.OnItemSelectedListener, View.OnFocusChangeListener {

    String plantName;

    // declare widgets
    private ImageView imageView;
    private Button addPlantToDB;
    private TextView lastWatered, nameError;
    private EditText editName, editWater, tempDay, tempNight, humidityStart, humidityEnd, editComment;
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
        setContentView(R.layout.activity_edit_plant);
        setTitle("Edit Plant");

        // initialize and display toolbar
        toolbar = findViewById(R.id.editToolbar);
        setSupportActionBar(toolbar);

        // initialize and display home icon
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        userId = FirebaseAuth.getInstance().getUid();

        // initialization of widgets
        imageView = (ImageView) findViewById(R.id.editImageView);
        addPlantToDB = (Button) findViewById(R.id.btnEditPlantToDB);
        lastWatered = (TextView) findViewById(R.id.txtEditLastWateredDatePicker);
        nameError = (TextView) findViewById(R.id.etEditNameError);
        editName = (EditText) findViewById(R.id.editNameEditText);
        editWater = (EditText) findViewById(R.id.editWaterEditText);
        tempDay = (EditText) findViewById(R.id.etEditTempDay);
        tempNight = (EditText) findViewById(R.id.etEditTempNight);
        humidityStart = (EditText) findViewById(R.id.etEditHumidityStart);
        humidityEnd = (EditText) findViewById(R.id.etEditHumidityEnd);
        editComment = (EditText) findViewById(R.id.editCommentEditText);
        typeAdd = (Spinner) findViewById(R.id.editTypeSpinner);
        sunAdd = (Spinner) findViewById(R.id.editSunSpinner);

        Intent intent = getIntent();

        // initialization of variables
        typeSpinnerFields = new String[]{"Select Plant Type", "Annual", "Bamboo", "Bulb",
                "Cactus - Succulent", "Climber", "Conifer", "Fern", "Fruit", "Grass", "Herb",
                "Palm - Cycad", "Perennial", "Rose", "Shrub", "Tree"};
        sunlightSpinnerFields = new String[]{"Select Sunlight Requirement", "Dense Shade",
                "Full Shade", "Full Sun", "Light Shade", "Partial Shade"};
        lastWateredDateSet = "";
        plantName = intent.getStringExtra("plantName");
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
        editName.setOnFocusChangeListener(this);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        globalMethods = new GlobalMethods(this);

        loadEditSpinners();

        if (!plantName.isEmpty()){
            loadEditText();
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.editImageView){
            dispatchTakePictureIntent();
        }
        if (view.getId() == R.id.btnEditPlantToDB){
            editPlant();
        }
        if (view.getId() == R.id.editImageView){
            showImageMethodDialog();
        }
        if (view.getId() == R.id.txtEditLastWateredDatePicker){
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
    public void loadEditSpinners(){
        ArrayAdapter<String> typeArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, typeSpinnerFields);
        ArrayAdapter<String> sunlightArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, sunlightSpinnerFields);

        typeAdd.setAdapter(typeArrayAdapter);
        sunAdd.setAdapter(sunlightArrayAdapter);
    }

    public void loadEditText(){
        Query lastQuery = databaseReference.child("plant").child(userId).orderByKey();
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    String nameDB = Objects.requireNonNull(data.child("name").getValue()).toString();

                    if (nameDB.equals(plantName)) {
                        String typeDB = Objects.requireNonNull(data.child("typeId").getValue()).toString();
                        String sunDB = Objects.requireNonNull(data.child("sunlightId").getValue()).toString();
                        String imageDB = Objects.requireNonNull(data.child("image").getValue()).toString();
                        lastWateredDateSet = Objects.requireNonNull(data.child("dateWatered").getValue()).toString();
                        String waterFreqDB = Objects.requireNonNull(data.child("waterFrequency").getValue()).toString();
                        String dayTempDB = Objects.requireNonNull(data.child("dayTemp").getValue()).toString();
                        String nightTempDB = Objects.requireNonNull(data.child("nightTemp").getValue()).toString();
                        String humStartDB = Objects.requireNonNull(data.child("startHumidity").getValue()).toString();
                        String humEndDB = Objects.requireNonNull(data.child("endHumidity").getValue()).toString();
                        String commentDB = Objects.requireNonNull(data.child("comment").getValue()).toString();

                        Uri imageUri = Uri.parse(imageDB);

                        editName.setText(nameDB);

                        lastWatered.setText(lastWateredDateSet);
                        editWater.setText(waterFreqDB);
                        tempDay.setText(dayTempDB);
                        tempNight.setText(nightTempDB);
                        humidityStart.setText(humStartDB);
                        humidityEnd.setText(humEndDB);
                        editComment.setText(commentDB);

                        type_id = Integer.parseInt(typeDB);
                        sunlight_id = Integer.parseInt(sunDB);

                        typeAdd.setSelection(type_id);
                        sunAdd.setSelection(sunlight_id);


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


    // write plant data to the database
    private void editPlant() {
        String sPlantName = editName.getText().toString();
        String sPlantComment = editComment.getText().toString();

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

            updatePlantDB(sPlantName, sPlantComment);
        }
    }

    private void updatePlantDB(final String name, final String comment) {

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

                        Plant plant = new Plant(name, type_id, sunlight_id, dayTemp, nightTemp, humStart,
                                humEnd, lastWateredDateSet, plantWater, comment, imageUri);

                        //  Map<String, Object> childUpdates = new HashMap<>();

                        Map<String, Object> detailValues = plant.toPlantDetails();
                        // childUpdates.put(key, detailValues);

                        databaseReference.child("plant").child(userId).child(key).setValue(detailValues)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Write was successful!
                                        Toast.makeText(getApplicationContext(), name + " updated", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});
    }


    // get number fields or leave as zero
    private void getNumberInputs() {
        try {
            plantWater = Integer.parseInt(editWater.getText().toString());
        } catch (NumberFormatException e){ }

        try {
            dayTemp = Integer.parseInt(tempDay.getText().toString());
        } catch (NumberFormatException e){ }

        try {
            nightTemp = Integer.parseInt(tempNight.getText().toString());
        } catch (NumberFormatException e){ }

        try {
            humStart = Integer.parseInt(humidityStart.getText().toString());
        } catch (NumberFormatException e){ }

        try {
            humEnd = Integer.parseInt(humidityEnd.getText().toString());
        } catch (NumberFormatException e){ }

    }

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
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }
}
