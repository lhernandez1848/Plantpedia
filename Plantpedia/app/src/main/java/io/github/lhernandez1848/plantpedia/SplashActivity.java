package io.github.lhernandez1848.plantpedia;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        if (!checkStoragePermission()){
            requestStoragePermission();
        }else{
            if(!checkCameraPermission()){
                requestCameraPermission();
            }
        }

        if(checkCameraPermission() && checkStoragePermission()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(2000);
                        startActivity(new Intent(
                                getApplicationContext(), MainActivity.class));
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    public boolean checkCameraPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkStoragePermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!checkCameraPermission()){
                    requestCameraPermission();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        showMessage();
                    }
                }
            }
        }
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(
                        getApplicationContext(), MainActivity.class));

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        showMessage();
                    }
                }
            }
        }

    }

    private void showMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Permission Denied");
        builder.setMessage("This app requires access to your device storage and camera. " +
                "Allow access when prompted or change the permissions in app settings");
        builder.setIcon(R.drawable.plant);
        builder.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);
                }
            });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
