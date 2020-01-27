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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class EditPlant extends AppCompatActivity implements View.OnClickListener {

    String plantInfo = SelectedPlant.getEditPlantName();
    String oldName = "";

    private ImageView editImageView;
    private Button btnCameraEdit;
    private Button btnGalleryEdit;
    private Button editPlant;
    private TextView displayName;
    private EditText nameEdit;
    private EditText typeEdit;
    private EditText lifeCycleEdit;
    private EditText sunEdit;
    private EditText waterEdit;
    private EditText temperatureEdit;
    private EditText commentEdit;
    String[] data = new String[7];

    private PlantDB db;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_plant);

        editImageView = (ImageView) findViewById(R.id.editImageView);
        btnCameraEdit = (Button) findViewById(R.id.btnTakePictureEdit);
        btnGalleryEdit = (Button) findViewById(R.id.btnGalleryEdit);
        editPlant = (Button) findViewById(R.id.btnEditPlant);

        displayName = (TextView) findViewById(R.id.editPlantSelected);

        nameEdit = (EditText) findViewById(R.id.editNameEditText);
        typeEdit = (EditText) findViewById(R.id.editTypeEditText);
        lifeCycleEdit = (EditText) findViewById(R.id.editLifeCycleEditText);
        sunEdit = (EditText) findViewById(R.id.editSunEditText);
        waterEdit = (EditText) findViewById(R.id.editWaterEditText);
        temperatureEdit = (EditText) findViewById(R.id.editTemperatureEditText);
        commentEdit = (EditText) findViewById(R.id.editCommentEditText);

        btnCameraEdit.setOnClickListener(this);
        btnGalleryEdit.setOnClickListener(this);
        editPlant.setOnClickListener(this);

        db = new PlantDB(this);

        loadEditText();
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
        if (view.getId() == R.id.btnTakePictureEdit){
            dispatchTakePictureIntent();
        }
        if (view.getId() == R.id.btnGalleryEdit){
            chooseImageIntent();
        }
        if (view.getId() == R.id.btnEditPlant){
            String sPlantName = nameEdit.getText().toString();
            String sPlantType = typeEdit.getText().toString();
            String sPlantLife = lifeCycleEdit.getText().toString();
            String sPlantSun = sunEdit.getText().toString();
            String sPlantWater = waterEdit.getText().toString();
            String sPlantTemperature = temperatureEdit.getText().toString();
            String sPlantComment = commentEdit.getText().toString();

            try {
                if(sPlantName.equals("")){
                    Toast.makeText(this, "Plant name must not be empty",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    String img = insertImage(sPlantName).toString();

                    db.updatePlantName(oldName, sPlantName, sPlantType, sPlantLife, sPlantSun,
                            sPlantWater, sPlantTemperature, sPlantComment, img);
                    Toast.makeText(this, "Plant updated",
                            Toast.LENGTH_SHORT).show();
                    loadEditText();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void loadEditText(){
        data = db.getPlantInfo(plantInfo);
        displayName.setText("Edit Plant");

        Uri imageUri = Uri.parse(data[7]);
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            editImageView.setImageBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        oldName = data[0];

        nameEdit.setText(data[0]);
        typeEdit.setText(data[1]);
        lifeCycleEdit.setText(data[2]);
        sunEdit.setText(data[3]);
        waterEdit.setText(data[4]);
        temperatureEdit.setText(data[5]);
        commentEdit.setText(data[6]);
    }

    private void chooseImageIntent() {
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_LOAD_IMAGE);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap takeImageBitmap = (Bitmap) extras.get("data");
            editImageView.setImageBitmap(takeImageBitmap);
        }
        if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            try {
                Bitmap loadImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                editImageView.setImageBitmap(loadImageBitmap);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public Uri insertImage(String sPlantName){
        Bitmap bitmap = ((BitmapDrawable)editImageView.getDrawable()).getBitmap();
        String savedImage = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, sPlantName , null);  // Saves the image.
        Uri savedImageURI = Uri.parse(savedImage);
        return savedImageURI;
    }
}
