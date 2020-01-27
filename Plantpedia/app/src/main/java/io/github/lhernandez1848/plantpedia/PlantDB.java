package io.github.lhernandez1848.plantpedia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class PlantDB {
    // database constants
    public static final String DB_NAME = "plant.sqlite";
    public static final int    DB_VERSION = 1;
    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create tables
            db.execSQL("CREATE TABLE plants (id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , name VARCHAR UNIQUE NOT NULL , type VARCHAR, lifeCycle VARCHAR, sunRequirements VARCHAR, water VARCHAR, temperature VARCHAR, comments VARCHAR, image VARCHAR)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE \"plants\"");
            Log.d("Task list", "Upgrading db from version "
                    + oldVersion + " to " + newVersion);

            onCreate(db);
        }
    }

    // database and database helper objects
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    // constructor
    public PlantDB(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        openWriteableDB();
        closeDB();
    }
    // private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    //gets all plants related to the search
    ArrayList<HashMap<String, String>> getPlantName(String sSearchName){
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        openReadableDB();
        String where = "name LIKE ?";
        String[] whereArgs = {"%" + sSearchName + "%"};
        Cursor cursor = db.rawQuery("SELECT name, comments FROM plants WHERE " + where, whereArgs);
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", cursor.getString(0));
            map.put("comments", cursor.getString(1));
            data.add(map);
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return data;
    }

    //Gets all plants in the database
    ArrayList<HashMap<String, String>> getAllPlants(){
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        openReadableDB();
        Cursor cursor = db.rawQuery("SELECT name, comments FROM plants ORDER BY name COLLATE NOCASE ASC", null);
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", cursor.getString(0));
            map.put("comments", cursor.getString(1));
            data.add(map);
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return data;
    }

    // Gets single plant info based on plant name
    String[] getPlantInfo(String sPlantName){
        String[] data = new String[8];
        openReadableDB();
        String where = "name=?";
        String[] whereArgs = {sPlantName};
        Cursor cursor = db.rawQuery("SELECT name, type, lifeCycle, sunRequirements, water, temperature, comments, image FROM plants WHERE " + where, whereArgs);
        cursor.moveToFirst();
        data[0] = cursor.getString(0);
        data[1] = cursor.getString(1);
        data[2] = cursor.getString(2);
        data[3] = cursor.getString(3);
        data[4] = cursor.getString(4);
        data[5] = cursor.getString(5);
        data[6] = cursor.getString(6);
        data[7] = cursor.getString(7);
        cursor.close();
        closeDB();

        return data;
    }

    // Adds a new plant to the database
    void insertPlant(String sName, String sType, String sLife, String sSun, String sWater,
                     String sTemperature, String sComment, String sImage)throws Exception{
        openWriteableDB();
        ContentValues content = new ContentValues();
        content.put("name", sName);
        content.put("type", sType);
        content.put("lifeCycle", sLife);
        content.put("sunRequirements", sSun);
        content.put("water", sWater);
        content.put("temperature", sTemperature);
        content.put("comments", sComment);
        content.put("image", sImage);
        long nResult = db.insert("plants",null, content);
        if(nResult == -1) throw new Exception("no data");
        closeDB();
    }

    void updatePlantName(String sName, String sEditedName, String sEditedType, String sLifeCycle,
                         String sSunRequirements, String sWater, String sTemperature,
                         String sComment, String img)throws Exception{
        openWriteableDB();
        String where = "name=?";
        String[] whereArgs = {sName};
        ContentValues content = new ContentValues();
        content.put("name", sEditedName);
        content.put("type", sEditedType);
        content.put("lifeCycle", sLifeCycle);
        content.put("sunRequirements", sSunRequirements);
        content.put("water", sWater);
        content.put("temperature", sTemperature);
        content.put("comments", sComment);
        content.put("image", img);
        long nResult = db.update("plants", content, where, whereArgs);
        if(nResult == -1) throw new Exception("no data");
        closeDB();
    }

    void deletePlant(String plantName)throws Exception{
        openWriteableDB();
        String where = "name=?";
        String[] whereArgs = {plantName};
        db.execSQL("DELETE FROM plants WHERE " + where, whereArgs);
        closeDB();
    }

}
