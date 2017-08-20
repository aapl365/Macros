package com.example.aapl.apptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Aapl on 8/19/17.
 */

public class ActivityChangeButtonHandler implements View.OnClickListener{
    AppCompatActivity activity;

    // Constructor
    public ActivityChangeButtonHandler(AppCompatActivity activity) {
        this.activity = activity;
    }

    /**
     * Find which button was clicked, and open the respective activity
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_toDb:
                viewDatabase();
                break;
            case R.id.button_toCalc:
                viewCalc();
                break;
        }
    }

    /*************** Buttons for screen selection ***************/
    public void viewAddtoDatabase() {
        Intent intent = new Intent(activity, AddtoDatabaseActivity.class);
        activity.startActivity(intent);
    }

    private void viewDatabase(){
        Intent intent = new Intent(activity, DatabaseWindow.class);
        activity.startActivity(intent);
    }

    private void viewCalc(){
        Intent intent = new Intent(activity, SimplexCalcActivity.class);
        activity.startActivity(intent);
    }
}
