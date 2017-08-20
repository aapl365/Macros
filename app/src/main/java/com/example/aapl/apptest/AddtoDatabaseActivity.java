package com.example.aapl.apptest;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.IllegalFormatException;

public class AddtoDatabaseActivity extends AppCompatActivity {
    //initializing layout items
    DatabaseHelper myDB;
    EditText editName, editCarbs, editFats, editProteins, editMeasurement, editValue;
    Button buttonAdd, buttonDelete, buttonView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_database);

        /*Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_add_to_database);
        layout.addView(textView);
        */

        myDB = DatabaseHelper.getInstance(this);
        editName = (EditText)findViewById(R.id.editText_name);
        editCarbs = (EditText)findViewById(R.id.editText_carbs);
        editFats = (EditText)findViewById(R.id.editText_fats);
        editProteins = (EditText)findViewById(R.id.editText_proteins);
        editMeasurement = (EditText)findViewById(R.id.editText_measurement);
        editValue = (EditText)findViewById(R.id.editText_value);
        buttonAdd = (Button)findViewById(R.id.button_add);
        buttonDelete = (Button)findViewById(R.id.button_delete);
        buttonView = (Button)findViewById(R.id.button_viewData);

        addFood();
        deleteFood();
        viewDatabase();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_to_database, menu);
        return true;
    }
    */

    /**
     * Handles click of back button
     * @param item
     * @return
     */

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // not sure what view to use, or if it matters
        viewDatabase(findViewById(R.id.button_showScrape));

        return super.onOptionsItemSelected(item);
    }*/

    public void addFood(){
        buttonAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = false;
                        boolean isString = true;
                        boolean isNegative = false;
                        String enteredName, enteredCarbs, enteredFats, enteredProteins, enteredMeasurement, enteredValue;
                        enteredName = editName.getText().toString();
                        enteredCarbs = editCarbs.getText().toString();
                        enteredFats = editFats.getText().toString();
                        enteredProteins = editProteins.getText().toString();
                        enteredMeasurement = editMeasurement.getText().toString();
                        enteredValue = editValue.getText().toString();

                        errors: try{
                            //checks to make sure Strings are entered in Name and Measurement
                            if(enteredName == null || enteredName.isEmpty()){
                                isString = false;
                                break errors;
                            }
                            if(enteredMeasurement == null || enteredMeasurement.isEmpty()){
                                isString = false;
                                break errors;
                            }

                            //checks to make sure numbers are entered in required fields
                            Double.valueOf(enteredCarbs);
                            Double.valueOf(enteredFats);
                            Double.valueOf(enteredProteins);
                            Double.valueOf(enteredValue);

                            //checks for negatives
                            if(enteredCarbs.charAt(0) == '-'){
                                isNegative = true;
                                break errors;
                            }
                            if(enteredFats.charAt(0) == '-'){
                                isNegative = true;
                                break errors;
                            }
                            if(enteredProteins.charAt(0) == '-'){
                                isNegative = true;
                                break errors;
                            }
                            if(enteredValue.charAt(0) == '-'){
                                isNegative = true;
                                break errors;
                            }

                            isInserted = myDB.insertData(enteredName,
                                    enteredCarbs,
                                    enteredFats,
                                    enteredProteins,
                                    enteredMeasurement,
                                    enteredValue);
                        }catch(NumberFormatException DoubleError){
                            Toast.makeText(AddtoDatabaseActivity.this, "Number is Invalid", Toast.LENGTH_LONG).show();
                        }catch(StringIndexOutOfBoundsException EmptyField){
                            Toast.makeText(AddtoDatabaseActivity.this, "Field Empty", Toast.LENGTH_LONG).show();
                        }

                        if(isInserted && isString && !isNegative){
                            Toast.makeText(AddtoDatabaseActivity.this, "Food Added Dood", Toast.LENGTH_LONG).show();
                        }else if(!isString){
                            Toast.makeText(AddtoDatabaseActivity.this, "Name or Measurement Field is Empty", Toast.LENGTH_LONG).show();
                        }else if(isNegative){
                            Toast.makeText(AddtoDatabaseActivity.this, "Value is Negative", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(AddtoDatabaseActivity.this, "Failed to Add", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void deleteFood(){
        buttonDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int deletedRow = myDB.deleteData(editName.getText().toString());
                        if(deletedRow > 0){
                            Toast.makeText(AddtoDatabaseActivity.this, "Food Deleted", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(AddtoDatabaseActivity.this, "Item Not Found", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void viewDatabase(){
        buttonView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res =  myDB.getAllData();
                        if(res.getCount() == 0){
                            showAlert("Error ", "Nothing Found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while(res.moveToNext()){
                            buffer.append("ID: " + res.getString(0) + "\n");
                            buffer.append("Name: " + res.getString(1) + "\n");
                            buffer.append("Calories: " + res.getString(2) + "\n");
                            buffer.append("Carbs: " + res.getString(3) + "\n");
                            buffer.append("Fats: " + res.getString(4) + "\n");
                            buffer.append("Proteins: " + res.getString(5) + "\n");
                            buffer.append("Measurement: " + res.getString(6) + "\n");
                            buffer.append("Value: " + res.getString(7) + "\n\n");
                        }

                        showAlert("Food Database", buffer.toString());
                    }
                }
        );
    }

    public void showAlert(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}