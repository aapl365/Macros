package com.example.aapl.apptest;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Aapl on 1/11/17.
 */

public class FoodCursorAdapter extends CursorAdapter {

    public FoodCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem_food, parent, false);
    }

    // Binds all data to a listitem_food.xml in Listview
    // !! calories commented out due to formatting !!
    // Only shows name as of now
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvBody = (TextView) view.findViewById(R.id.tvBody);

        /* UNCOMMENT FOR CALORIES
        TextView tvPriority = (TextView) view.findViewById(R.id.tvPriority);
        */

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

        /* UNCOMMENT FOR CALORIES
        double calories = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("calories")));

        //rounds calories to tenth's place
        String rounded_cal = String.format("%.1f", calories);
        */

        // Populate fields with extracted properties
        tvBody.setText(name);

        /* UNCOMMENT FOR CALORIES
        tvPriority.setText(rounded_cal);
        */
    }

    //strikes through body of cursor position
    public void strikeThrough(View view, Context context, Cursor cursor){
        TextView tvBody = (TextView) view.findViewById(R.id.tvBody);
        tvBody.setPaintFlags(tvBody.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

}
