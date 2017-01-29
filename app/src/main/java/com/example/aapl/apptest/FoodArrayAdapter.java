package com.example.aapl.apptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aapl on 1/13/17.
 */

public class FoodArrayAdapter extends ArrayAdapter {

    private ArrayList<FoodItem> SolutionList;
    private static LayoutInflater inflater = null;

    public FoodArrayAdapter(Context context, int resource, ArrayList<FoodItem> objects) {
        super(context, resource, objects);
        SolutionList = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View vi = inflater.inflate(R.layout.listitem_food, parent, false);

        // Find fields to populate in inflated template
        TextView tvBody = (TextView) vi.findViewById(R.id.tvBody);
        /* UNCOMMENT FOR CALORIES
        TextView tvPriority = (TextView) vi.findViewById(R.id.tvPriority);
        */

        // Extract properties from cursor
        String name = SolutionList.get(position).name;

        /* UNCOMMENT FOR CALORIES
        double calories = SolutionList.get(position).calories;
        //rounds calories to tenth's place
        String rounded_cal = String.format("%.1f", calories);
        */

        // Populate fields with extracted properties
        tvBody.setText(name);

        /* UNCOMMENT FOR CALORIES
        tvPriority.setText(rounded_cal);
        */

        return vi;
    }
}
