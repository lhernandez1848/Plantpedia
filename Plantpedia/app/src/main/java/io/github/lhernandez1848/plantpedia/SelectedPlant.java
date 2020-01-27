package io.github.lhernandez1848.plantpedia;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class SelectedPlant extends AppCompatActivity implements View.OnClickListener {

    String plantNameFromSearch = SearchActivity.getSelectedPlantFromSearch();
    String plantNameFromAll = ViewAllPlants.getSelectedPlantFromViewAll();

    private ImageView imageView;
    private TextView nameTextView;
    private TextView typeTextView;
    private TextView lifeCycleTextView;
    private TextView sunTextView;
    private TextView waterTextView;
    private TextView temperatureTextView;
    private TextView commentTextView;
    private Button btnEditPlantInfo;
    private Button deletePlant;

    String[] data = new String[7];
    public static String editPlantInfo = "";

    private PlantDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_plant_view);

        nameTextView = (TextView) findViewById(R.id.plantNameTextView);
        typeTextView = (TextView) findViewById(R.id.plantTypeTextView);
        lifeCycleTextView = (TextView) findViewById(R.id.plantLifeCycleTextView);
        sunTextView = (TextView) findViewById(R.id.plantSunRequirementsTextView);
        waterTextView = (TextView) findViewById(R.id.plantWaterTextView);
        temperatureTextView = (TextView) findViewById(R.id.plantTemperatureTextView);
        commentTextView = (TextView) findViewById(R.id.plantCommentsTextView);
        imageView = (ImageView) findViewById(R.id.plantImageView);

        btnEditPlantInfo = (Button) findViewById(R.id.btnEditPlantInfo);
        btnEditPlantInfo.setOnClickListener(this);

        deletePlant = (Button) findViewById(R.id.btnDeletePlant);
        deletePlant.setOnClickListener(this);

        db = new PlantDB(this);

        Intent mIntent = getIntent();
        String previousActivity= mIntent.getStringExtra("FROM_ACTIVITY");

        if (previousActivity.equals("VIEWALL")){
            loadPlantData(plantNameFromAll);
        }
        else if (previousActivity.equals("SEARCH")){
            loadPlantData(plantNameFromSearch);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnEditPlantInfo){
            editPlantInfo = nameTextView.getText().toString();
            startActivity(new Intent(
                    getApplicationContext(), EditPlant.class));
        }
        if(view.getId() == R.id.btnDeletePlant) {
            deletePlantPopup(nameTextView.getText().toString());
        }
    }

    public static String getEditPlantName(){
        return editPlantInfo;
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

    private void loadPlantData(String sName) {
        data = db.getPlantInfo(sName);

        Uri imageUri = Uri.parse(data[7]);
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            imageView.setImageBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        nameTextView.setText(data[0]);
        typeTextView.setText(data[1]);
        lifeCycleTextView.setText(data[2]);
        sunTextView.setText(data[3]);
        waterTextView.setText(data[4]);
        temperatureTextView.setText(data[5]);
        commentTextView.setText(data[6]);
    }

    private void deletePlantPopup(String plantNameToDelete){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete Plant");
        builder.setMessage("Are you sure you want to delete " + plantNameToDelete);
        builder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String plantNameForDeletion = nameTextView.getText().toString();
                        try {
                            db.deletePlant(plantNameForDeletion);
                            startActivity(new Intent(
                                    getApplicationContext(), MainActivity.class));
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setIcon(R.drawable.plant);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
