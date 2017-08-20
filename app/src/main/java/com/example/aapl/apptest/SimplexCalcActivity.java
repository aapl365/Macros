package com.example.aapl.apptest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;

public class SimplexCalcActivity extends AppCompatActivity {

    SimplexHelper simplex;
    ActivityChangeButtonHandler activityButtonHandler;
    EditText editCarbsSol, editFatsSol, editProteinsSol, editScrapeSearch;
    TextView textResuts;
    Button button_calc, button_clear, button_scrape, button_toCalc, button_toDb;;
    boolean asFood = false;
    boolean isScraping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplex_calc);
        simplex = SimplexHelper.getInstance();
        activityButtonHandler = new ActivityChangeButtonHandler(this);

        // Setting views
        editCarbsSol = (EditText)findViewById(R.id.editText_DesiredCarbs);
        editFatsSol = (EditText)findViewById(R.id.editText_DesiredFats);
        editProteinsSol = (EditText)findViewById(R.id.editText_DesiredProteins);
        button_calc = (Button) findViewById(R.id.button_calcSimplex);
        button_clear = (Button) findViewById(R.id.button_clearCalc);
        button_toCalc = (Button) findViewById(R.id.button_toCalc);
        button_toDb = (Button) findViewById(R.id.button_toDb);

        // Setting button onClick
        button_toCalc.setOnClickListener(activityButtonHandler);
        button_toDb.setOnClickListener(activityButtonHandler);

        // remove after testing
        button_scrape = (Button) findViewById(R.id.button_showScrape);
        editScrapeSearch = (EditText)findViewById(R.id.editText_ScrapeWord);
        textResuts = (TextView)findViewById(R.id.textView2);
        // end removal

        // Setting button onClick
        simplexCalculation();
        clearCalc();
        scrapeWiki();
    }

    public void simplexCalculation(){
        button_calc.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isNegative = false;

                        //calculates if fields are positive doubles
                        errors: try {
                            String carbField, fatField, proteinField;
                            carbField = editCarbsSol.getText().toString();
                            fatField = editFatsSol.getText().toString();
                            proteinField = editProteinsSol.getText().toString();

                            //checks for negatives
                            if(carbField.charAt(0) == '-'){
                                isNegative = true;
                                break errors;
                            }
                            if(fatField.charAt(0) == '-'){
                                isNegative = true;
                                break errors;
                            }
                            if(proteinField.charAt(0) == '-'){
                                isNegative = true;
                                break errors;
                            }

                            //throws NumberFormatException if fields are not numeric
                            simplex.setCarbs(Double.valueOf(carbField));
                            simplex.setFats(Double.valueOf(fatField));
                            simplex.setProteins(Double.valueOf(proteinField));

                            double[][] tableau = simplex.createSimplexTableau();

                            //actual simplex algorithm
                            while (!simplex.checkCompletion(tableau)) {
                                simplex.pivotRow(tableau);
                            }

                            //writing out solution in word form
                            for (int i = 0; i < simplex.getFoodListSize(); i++) {
                                TextView textView = new TextView(SimplexCalcActivity.this);
                                textView.setTextSize(20);

                                //rounds solution to 2 places
                                double result = simplex.getResults(i, tableau) * simplex.getFoodValue(i);
                                String rounded_result = String.format("%.2f", result);

                                String message = rounded_result + " " + simplex.getFoodMeasurement(i) + " of " + simplex.getFoodName(i) + "\n";
                                textView.setText(message);

                                ViewGroup layout = (LinearLayout) findViewById(R.id.LinearLayout_solution);
                                layout.addView(textView);
                            }
                        }catch(NumberFormatException DoubleError){
                            Toast.makeText(SimplexCalcActivity.this, "Numbers Not Entered", Toast.LENGTH_SHORT).show();
                        }catch(StringIndexOutOfBoundsException EmptyError){
                            Toast.makeText(SimplexCalcActivity.this, "Field Empty", Toast.LENGTH_SHORT).show();
                        }
                        if(isNegative){
                            Toast.makeText(SimplexCalcActivity.this, "Negative Numbers", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void clearCalc(){
        button_clear.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  uncomment to also clear FoodList
                        //simplex.clearFoodList();
                        ViewGroup layout = (LinearLayout) findViewById(R.id.LinearLayout_solution);
                        layout.removeAllViews();
                        Toast.makeText(SimplexCalcActivity.this, "Cleared", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private class ScrapeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... searched) {
            try {
                StringBuilder builder = new StringBuilder();
                for(String s : searched) {
                    builder.append(s);
                }
                String test = builder.toString();
                String website = "https://en.wikipedia.org/wiki/" + test;
                Document doc = Jsoup.connect(website).get();
                Element table = doc.select("table:contains(nutritional)").get(0);
                Element row = table.select("tr:contains(protein)").get(0);
                Element col = row.select("td:contains(g)").get(0);
                String value = col.select("div").get(0).text();
                asFood = false;
                isScraping = false;
                return value;
            } catch (IOException badConnect) {
                return "IOException";
            } catch (IndexOutOfBoundsException elementNotFound) {
                asFood = true;
                isScraping = false;
                return "Not Found";
            }
        }

        @Override
        protected void onPostExecute(String result) {
                textResuts.setText(result);
        }
    }

    /**
     * Perhaps change to google "nutrition ____" and then scrape wiki page.
     * To be moved once "add" button is completed in database actionbar
     */
    public void scrapeWiki() {
        button_scrape.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String entered = editScrapeSearch.getText().toString();
                        Toast.makeText(SimplexCalcActivity.this, "Entered: " + entered, Toast.LENGTH_LONG).show();
                        isScraping = true;
                        new ScrapeTask().execute(entered);

                        // buffer to check when backround task is done
                        // need or else program runs too fast to check asFood
                        while(isScraping){
                            // spinning
                        }
                        if (asFood) {
                            isScraping = true;
                            new ScrapeTask().execute(entered + "_as_food");
                        }
                    }
                }
        );
    }

    public void test(String searched) {
        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/" + searched).get();
            Element table = doc.select("table:contains(protein)").get(0);
            Element row = table.select("tr:contains(protein)").get(0);
            String value = row.select("td").get(0).text();
            Toast.makeText(SimplexCalcActivity.this, "Protein is " + value, Toast.LENGTH_LONG).show();
        } catch (IOException badConnect){
            Toast.makeText(SimplexCalcActivity.this, "Could not connect", Toast.LENGTH_LONG).show();
        }
    }
}
