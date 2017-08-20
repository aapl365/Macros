package com.example.aapl.apptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.AppTest.MESSAGE";
    DatabaseHelper FoodDB;
    SimplexHelper simplex;
    ActivityChangeButtonHandler activityButtonHandler;

    Button button_toCalc, button_toDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FoodDB = DatabaseHelper.getInstance(this);
        simplex = SimplexHelper.getInstance();
        activityButtonHandler = new ActivityChangeButtonHandler(this);

        // Setting views
        button_toCalc = (Button) findViewById(R.id.button_toCalc);
        button_toDb = (Button) (Button) findViewById(R.id.button_toDb);

        // Setting button onClick
        button_toCalc.setOnClickListener(activityButtonHandler);
        button_toDb.setOnClickListener(activityButtonHandler);
    }
}
