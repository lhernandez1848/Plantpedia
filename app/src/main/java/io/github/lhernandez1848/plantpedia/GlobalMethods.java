package io.github.lhernandez1848.plantpedia;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTimeComparator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class GlobalMethods {

    private Context _context;

    public GlobalMethods(Context context){
        this._context = context;
    }

    public FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 201;

    public void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();

        Intent intent = new Intent(_context, StartActivity.class);
        intent.putExtra("action", "SIGN_OUT");

        _context.startActivity(intent);
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

    public void changeWaterDateDialog(FragmentManager fragmentManager) {
        WaterDateDialog waterDateDialog = new WaterDateDialog();

        waterDateDialog.show(fragmentManager, "Water Date");
    }

    public void updateDatabase(final DatabaseReference databaseReference, final String plantName, final String userId){
        Calendar c = Calendar.getInstance();
        String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        String year = Integer.toString(c.get(Calendar.YEAR));
        final String newDateWatered = day + "/" + month + "/" + year;

        Query query = databaseReference.child("plant").child(FirebaseAuth.getInstance().getUid()).orderByKey();
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
                                .addOnSuccessListener(aVoid -> {
                                    // Write was successful!
                                    Toast.makeText(_context, "Water date changed", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Write failed
                                    Toast.makeText(_context, "ERROR: Water date not changed", Toast.LENGTH_SHORT).show();
                                });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});
    }


    public boolean checkCameraPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(_context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkStoragePermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(_context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStoragePermission() {
        ActivityCompat.requestPermissions((Activity) _context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions((Activity) _context,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    // show dialog for choosing image method
    public void showImageMethodDialog(FragmentManager fragmentManager) {
        ChooseImageMethodDialog chooseImageMethodDialog = new ChooseImageMethodDialog();

        chooseImageMethodDialog.show(fragmentManager, "Choose Image Method");
    }

}
