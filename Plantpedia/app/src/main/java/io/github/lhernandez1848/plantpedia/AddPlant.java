package io.github.lhernandez1848.plantpedia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddPlant extends AppCompatActivity
        implements View.OnClickListener {

    private ImageView imageView;
    private Button btnCamera;
    private Button btnGallery;
    private Button addPlantToDB;
    private EditText nameAdd;
    private EditText typeAdd;
    private EditText lifeCycleAdd;
    private EditText sunAdd;
    private EditText waterAdd;
    private EditText temperatureAdd;
    private EditText commentAdd;

    private PlantDB db;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plant);

        imageView = (ImageView) findViewById(R.id.addImageView);
        btnCamera = (Button) findViewById(R.id.btnTakePicture);
        btnGallery = (Button) findViewById(R.id.btnGallery);
        addPlantToDB = (Button) findViewById(R.id.btnAddPlantToDB);

        nameAdd = (EditText) findViewById(R.id.addNameEditText);
        typeAdd = (EditText) findViewById(R.id.addTypeEditText);
        lifeCycleAdd = (EditText) findViewById(R.id.addLifeCycleEditText);
        sunAdd = (EditText) findViewById(R.id.addSunEditText);
        waterAdd = (EditText) findViewById(R.id.addWaterEditText);
        temperatureAdd = (EditText) findViewById(R.id.addTemperatureEditText);
        commentAdd = (EditText) findViewById(R.id.addCommentEditText);

        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        addPlantToDB.setOnClickListener(this);

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
    public void onClick(View view) {
        if (view.getId() == R.id.btnTakePicture){
            dispatchTakePictureIntent();
        }
        if (view.getId() == R.id.btnGallery){
            chooseImageIntent();
        }
        if (view.getId() == R.id.btnAddPlantToDB){
            String sPlantName = nameAdd.getText().toString();
            String sPlantType = typeAdd.getText().toString();
            String sPlantLife = lifeCycleAdd.getText().toString();
            String sPlantSun = sunAdd.getText().toString();
            String sPlantWater = waterAdd.getText().toString();
            String sPlantTemperature = temperatureAdd.getText().toString();
            String sPlantComment = commentAdd.getText().toString();

            if(sPlantName.equals("")){
                Toast.makeText(this, "Plant name must not be empty",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    String imageUri = insertImage(sPlantName).toString();

                    db.insertPlant(sPlantName, sPlantType, sPlantLife, sPlantSun,
                            sPlantWater, sPlantTemperature, sPlantComment, imageUri);
                    Toast.makeText(this, "Plant added",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(this, sPlantName + " already in the database",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
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

}
