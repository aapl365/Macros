package com.example.aapl.apptest;

import java.util.ArrayList;

/**
 * Created by Aapl on 1/11/17.
 */

/*  Note
 *  - to make the math work with >3 items, I needed more restricting equations than
 *      simply limiting carbs, fat, and protein.
 *      I chose to limit every item to only MAXCALPERCENT in the createSimplexTableau() function.
 *      ATM, no food may take up more than .8 of total calories.
 */
public class SimplexHelper {
    private static SimplexHelper sInstance;
    private ArrayList<FoodItem> FoodList;
    private Double[] DesiredSolution;

    public static synchronized SimplexHelper getInstance(){
        if(sInstance == null) {
            sInstance = new SimplexHelper();
        }
        return sInstance;
    }

    private SimplexHelper(){
        FoodList = new ArrayList<FoodItem>();
        DesiredSolution = new Double[3];
    }

    //************************ DesiredSolution functions ************************
    public void setCarbs(Double carbs){
        DesiredSolution[0] = carbs;
    }

    public void setFats(Double fats){
        DesiredSolution[1] = fats;
    }

    public void setProteins(Double proteins){
        DesiredSolution[2] = proteins;
    }

    public Double getCarbs(Double carbs){
        return DesiredSolution[0];
    }

    public Double getFats(Double fats){
        return DesiredSolution[1];
    }

    public Double getProteins(Double proteins){
        return DesiredSolution[2];
    }

    //************************ FoodList functions ************************
    public void addFoodItem(FoodItem food){
        FoodList.add(food);
    }

    public ArrayList<FoodItem> getFoodList(){ return FoodList; }

    public String getFoodName(int i){
        return FoodList.get(i).name;
    }

    public String getFoodMeasurement(int i){
        return FoodList.get(i).measurement;
    }

    public double getFoodValue(int i) { return FoodList.get(i).value; }

    public int getFoodListSize(){
        return FoodList.size();
    }

    public void clearFoodList(){
        FoodList.clear();
    }

    //************************ Tableau functions ************************
    // these functions do all the math

    /* double[][] createSimplexTableau(ArrayList<FoodItem>, double[])
	 *	- creates SimplexTableau for later Simplex Method Maximization
	 *	- m x n matrix
	 *	- m = 3 + #items + 1
	 *		- 3 rows for macros
	 *		- #items rows for (<90%)
	 *		- 1 row for maximized equation
	 *	- n = #items * 3 + 1
	 *		- #items columns for macros
	 *		- m columns for slack variables
	*/
    public double[][] createSimplexTableau(){
        int NUMITEMS = FoodList.size();
        double[][] tableau = new double[4 + NUMITEMS][NUMITEMS + (4+NUMITEMS)];
        int LASTROW = tableau.length-1;
        int LASTCOL = tableau[0].length-1;

        // populate macros
        for(int t = 0; t < NUMITEMS; t++){
            tableau[0][t] = FoodList.get(t).carbohydrates;
        }
        for(int t = 0; t < NUMITEMS; t++){
            tableau[1][t] = FoodList.get(t).fats;
        }
        for(int t = 0; t < NUMITEMS; t++){
            tableau[2][t] = FoodList.get(t).proteins;
        }

        //populate constraint equations
        //calories of one item may not exceed MAXCALPERCENT*totalCal
        double totalCal = DesiredSolution[0] * 4 + DesiredSolution[1] * 9 + DesiredSolution[2] * 4;
        double MAXCALPERCENT = 0.8;
        for(int itemIndex = 0; itemIndex < FoodList.size(); itemIndex++){
            tableau[3 + itemIndex][itemIndex] = FoodList.get(itemIndex).calories;
            tableau[3 + itemIndex][LASTCOL] = MAXCALPERCENT * totalCal;
        }

        //populate object function
        for(int col = 0; col < NUMITEMS; col++){
            tableau[LASTROW][col] = (-1) * FoodList.get(col).calories;
        }

        //populate slack variables
        for(int row = 0; row < LASTROW; row++){
            tableau[row][row + NUMITEMS] = 1.0;
        }

        //populate solution
        for(int row = 0; row < DesiredSolution.length; row++){
            tableau[row][LASTCOL] = DesiredSolution[row];
        }

        return tableau;
    }


    /* int findEnteringCol(int, double[][])
     *	- returns entering column (smallest entry of bottom row)
     *	- returns -1 if fail
    */
    public int findEnteringCol(double[][] tableau){
        int index = -1;
        int LASTROW = tableau.length-1;
        double min = tableau[LASTROW][0];

        for(int col = 0; col < tableau[0].length; col++){
            if(tableau[LASTROW][col] <= min && 1/tableau[LASTROW][col] < 0){
                min = tableau[LASTROW][col];
                index = col;
            }
        }
        return index;
    }


    /*	int findDepartingRow(int, double[][], int)
     *	- return entering column (smallest ratio of sol/[row][enteringCol])
     *	- returns -1 if fail
    */
    public int findDepartingRow(double[][] tableau, int enteringCol){
        int index = -1;
        int LASTROW = tableau.length-1;
        int LASTCOL = tableau[0].length-1;
        //System.out.println(enteringCol);
        double min = (tableau[0][LASTCOL]) / (tableau[0][enteringCol]);
        //sets index if need be
        if(min > 0){ index = 0;}

        //make sure min is non-negative
        int i = 0;
        while(min < 0 && i<LASTROW){
            min = (tableau[i][LASTCOL]) / (tableau[i][enteringCol]);
            i++;
        }
        index = i;

        //finding min ratio
        for(int row = 0; row < LASTROW; row++){
            double comparedRatio = (tableau[row][LASTCOL]) / (tableau[row][enteringCol]);
            if(comparedRatio <= min && comparedRatio > 0){
                min = comparedRatio;
                index = row;
            }
        }

        return index;
    }

    /*	boolean pivotRow(int, double[][])
     *	- pivots the Row
     *	- uses findEnteringCol() and findDepartingRow()
     *	- return success/fail
     */
    public boolean pivotRow(double[][] tableau){
        int enteringCol = findEnteringCol(tableau);
        int departingRow = findDepartingRow(tableau, enteringCol);
        boolean success = false;
        double pivotVal = tableau[departingRow][enteringCol];

        //stage 1: divide row by pivotVal
        for(int c = 0; c < tableau[0].length; c++){
            tableau[departingRow][c] = tableau[departingRow][c] / pivotVal;
            if(c == tableau[0].length-1){	success = true;}
        }

        //success check
        if(!success){ return success;}

        //stage 2: subtract DepartingRow from other Rows (Row Reduce)
        for(int r = 0; r < tableau.length; r++){
            double pivotRatio = tableau[r][enteringCol];
            for(int c = 0; c < tableau[0].length; c++){
                if(r != departingRow){
                    tableau[r][c] = tableau[r][c] - (tableau[departingRow][c] * pivotRatio);
                }
            }
        }
        return success;
    }

    /*	boolean checkCompletion(int items, double[][] tableau)
     *	- checks bottom row of tableau for negatives
     * 	- true: non-negative bottom row
     * 	- false: negative in bottom row
     */
    public boolean checkCompletion(double[][] tableau){
        boolean success = false;
        for(int c = 0; c < tableau[0].length; c++){
            if(tableau[tableau.length-1][c] != 0 && tableau[tableau.length-1][c] < 0) { return success;}
        }
        success = true;
        return success;
    }

    /*	double getResults(int FoodCol, double[][] tableau)
	 * 	- returns result for given FoodCol index
	 *
	 */
    public double getResults(int FoodCol, double[][] tableau){
        int lastCol = tableau[0].length-1;
        for(int row = 0; row < tableau.length-1; row++){
            if(tableau[row][FoodCol] == 1.0){
                return tableau[row][lastCol];
            }
        }
        return 0.0;
    }

}
