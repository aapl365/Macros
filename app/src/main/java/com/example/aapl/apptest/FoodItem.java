package com.example.aapl.apptest;

/**
 * Created by Aapl on 1/11/17.
 */

public class FoodItem {
    String name;
    double calories;
    double carbohydrates;
    double fats;
    double proteins;
    //maybe make this enum?
    String measurement;	//0:Ounces	1:Grams
    double value;

    //--------------Constructors------------
    //default constructor
    public FoodItem(){
        name = "none";
        calories = 0;
        carbohydrates = 0;
        fats = 0;
        proteins = 0;
        measurement = "grams";
        value = 0;
    }

    /*	constructor
     *	- calories calculated based off of macros
     *	- carbs: 4 cal
     *	- fat: 9 cal
     *	- protein: 4 cal
     */
    public FoodItem(String foodName, double c, double f, double p, String m, double v){
        name = foodName;
        calories = 4.0*c + 9.0*f + 4.0*p;
        carbohydrates = c;
        fats = f;
        proteins = p;
        measurement = m;
        value = v;
    }
}
