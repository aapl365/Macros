package com.example.aapl.apptest;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import static java.lang.Math.toIntExact;


public class DatabaseWindow extends AppCompatActivity {
    DatabaseHelper myDB;
    SimplexHelper simplex;
    ActivityChangeButtonHandler activityButtonHandler;

    Button button_toCalc, button_toDb;

    /**
     * Contains two onItemClickListeners for moving database around.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_window);
        myDB = DatabaseHelper.getInstance(this);
        simplex = SimplexHelper.getInstance();
        activityButtonHandler = new ActivityChangeButtonHandler(this);
        final FoodCursorAdapter foodAdapt = new FoodCursorAdapter(this, myDB.getAllData());


        // Setting views
        ListView dataLV = (ListView)findViewById(R.id.ListView_database);
        final ListView solutionLV = (ListView)findViewById(R.id.ListView_solution);
        button_toCalc = (Button) findViewById(R.id.button_toCalc);
        button_toDb = (Button) findViewById(R.id.button_toDb);

        // Setting button onClick
        button_toCalc.setOnClickListener(activityButtonHandler);
        button_toDb.setOnClickListener(activityButtonHandler);

        // Set dataLV Adapter
        dataLV.setAdapter(foodAdapt);

        //populates solutionLV with current FoodList contents
        final FoodArrayAdapter ArrayAdapt = new FoodArrayAdapter(DatabaseWindow.this, android.R.layout.simple_list_item_1, simplex.getFoodList());
        solutionLV.setAdapter(ArrayAdapt);

        /*crosses out database FoodItems already on FoodList
        for(int i = 0; i < simplex.getFoodListSize(); i++){
            Cursor cursor_strikeThrough = myDB.getMatchingName(simplex.getFoodName(i));
            foodAdapt.strikeThrough(view, this, cursor_strikeThrough);
        }*/

        /* Uncomment to Switch to new cursor and update contents of ListView
        todoAdapter.changeCursor(newCursor);
        */

        /*  Clicker for dataLV (left side)
        *   -   clicking a database item creates a FoodItem, adds it to FoodList, and displays it on solutionLV
        */
        dataLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = foodAdapt.getCursor();
                cursor.moveToPosition(position);
                /*  uncomment to retrieve selected food _id if needed
                 *  long food_id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                */

                //checks if the selected FoodItem is on FoodList
                boolean match = false;
                for(int i = 0; i < simplex.getFoodListSize(); i++) {
                    if (cursor.getString(1).equals(simplex.getFoodName(i))){
                        match = true;
                    }
                }

                //regular stuff if no match
                if(!match) {
                    //adds selected database FoodItem to FoodList
                    simplex.addFoodItem(toFoodItem(cursor));

                    /*crossing off database list
                    foodAdapt.strikeThrough(view, DatabaseWindow.this, cursor);
                    */

                    //populates solution ListView
                    FoodArrayAdapter ArrayAdapt2 = new FoodArrayAdapter(DatabaseWindow.this, android.R.layout.simple_list_item_1, simplex.getFoodList());
                    solutionLV.setAdapter(ArrayAdapt2);

                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    Toast.makeText(DatabaseWindow.this, name + " added to list", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(DatabaseWindow.this, "Food already added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*  Clicker for solutionLV (right side)
        *   -   on click, removes from FoodList and redraws LV
        */
        solutionLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    //checking if casting long to int leads to error
                    if(id < Integer.MIN_VALUE || id > Integer.MAX_VALUE){
                        throw new IllegalArgumentException();
                    }
                    simplex.getFoodList().remove((int)id);
                    FoodArrayAdapter ArrayAdapt2 = new FoodArrayAdapter(DatabaseWindow.this, android.R.layout.simple_list_item_1, simplex.getFoodList());
                    solutionLV.setAdapter(ArrayAdapt2);
                }catch(IllegalArgumentException error){
                    Toast.makeText(DatabaseWindow.this, "Why TF is your list so big? Casting went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_database, menu);
        return true;
    }


    /**
     * Handles click of menu add button
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        activityButtonHandler.viewAddtoDatabase();

        return super.onOptionsItemSelected(item);
    }


    //takes a cursor from SQlite database and creates a FoodItem out of columns
    public FoodItem toFoodItem(Cursor cursor){
        String name, measurement;
        Double carbs, fats, proteins, value;

        //retrieves data from SQL entry
        name = cursor.getString(1);
        carbs = Double.parseDouble(cursor.getString(3));
        fats = Double.parseDouble(cursor.getString(4));
        proteins = Double.parseDouble(cursor.getString(5));
        measurement = cursor.getString(6);
        value = Double.parseDouble(cursor.getString(7));

        //creates new FoodItem
        FoodItem dummy = new FoodItem(name, carbs, fats, proteins, measurement, value);

        return dummy;
    }
}
