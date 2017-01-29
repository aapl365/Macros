package com.example.aapl.apptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.AppTest.MESSAGE";
    DatabaseHelper FoodDB;
    SimplexHelper simplex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FoodDB = DatabaseHelper.getInstance(this);
        simplex = SimplexHelper.getInstance();
    }

    /*************** Buttons for screen selection ***************/
    public void addtoDatabase(View view) {
        Intent intent = new Intent(this, AddtoDatabaseActivity.class);
        startActivity(intent);
    }

    public void viewDatabase(View view){
        Intent intent = new Intent(this, DatabaseWindow.class);
        startActivity(intent);
    }

    public void viewCalc(View view){
        Intent intent = new Intent(this, SimplexCalcActivity.class);
        startActivity(intent);
    }
    /* End functions for buttons*/
}
