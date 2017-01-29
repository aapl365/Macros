package com.example.aapl.apptest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Aapl on 1/10/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    public static final String DATABASE_NAME = "Food.db";
    public static final String TABLE_NAME = "food_table";
    public static final String FOOD_COLUMN_ID = "_id";
    public static final String FOOD_COLUMN_NAME = "name";
    public static final String FOOD_COLUMN_CALORIES = "calories";
    public static final String FOOD_COLUMN_CARBOHYDRATES = "carbs";
    public static final String FOOD_COLUMN_FATS= "fats";
    public static final String FOOD_COLUMN_PROTEINS = "proteins";
    public static final String FOOD_COLUMN_MEASUREMENT = "measurement";
    public static final String FOOD_COLUMN_MEASUREMENT_VALUE = "value";

    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_NAME + "(_id INTEGER PRIMARY KEY, name TEXT, calories DOUBLE, carbs DOUBLE, fats DOUBLE, proteins DOUBLE, measurement TEXT, value DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String given_name, String given_carbs, String given_fats, String given_proteins, String given_measurement, String given_value){
        SQLiteDatabase db = this.getWritableDatabase();

        //math for calories
        double carbVal = Double.parseDouble(given_carbs);
        double fatVal = Double.parseDouble(given_fats);
        double proteinVal = Double.parseDouble(given_proteins);
        double calories = carbVal*4 + fatVal*9 + proteinVal*4;

        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_COLUMN_NAME, given_name);
        contentValues.put(FOOD_COLUMN_CALORIES, Double.toString(calories));
        contentValues.put(FOOD_COLUMN_CARBOHYDRATES, given_carbs);
        contentValues.put(FOOD_COLUMN_FATS, given_fats);
        contentValues.put(FOOD_COLUMN_PROTEINS, given_proteins);
        contentValues.put(FOOD_COLUMN_MEASUREMENT, given_measurement);
        contentValues.put(FOOD_COLUMN_MEASUREMENT_VALUE, given_value);
        long result = db.insert(TABLE_NAME, null, contentValues);

        //checks if successful insert
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String testQ = "SELECT * FROM " + TABLE_NAME + " ORDER BY name";
        Cursor result = db.rawQuery(testQ, null);
        return result;
    }

    public boolean updateData(String given_name, String given_calories, String given_carbs, String given_fats, String given_proteins, String given_measurement, String given_value){
        SQLiteDatabase db = this.getWritableDatabase();

        //math for calories
        double carbVal = Double.parseDouble(given_carbs);
        double fatVal = Double.parseDouble(given_fats);
        double proteinVal = Double.parseDouble(given_proteins);
        double calories = carbVal*4 + fatVal*9 + proteinVal*4;

        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_COLUMN_CALORIES, Double.toString(calories));
        contentValues.put(FOOD_COLUMN_CARBOHYDRATES, given_carbs);
        contentValues.put(FOOD_COLUMN_FATS, given_fats);
        contentValues.put(FOOD_COLUMN_PROTEINS, given_proteins);
        contentValues.put(FOOD_COLUMN_MEASUREMENT, given_measurement);
        contentValues.put(FOOD_COLUMN_MEASUREMENT_VALUE, given_value);
        db.update(TABLE_NAME, contentValues, "name = ?", new String[] {given_name});
        return true;
    }

    public Cursor getMatchingName(String search){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE name = " + search;
        Cursor result = db.rawQuery(query ,null);
        return result;
    }

    public int deleteData(String given_name){
        SQLiteDatabase db = this.getWritableDatabase();
        //this line to delete current table
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        return db.delete(TABLE_NAME, "name = ?", new String[] {given_name});
    }
}
