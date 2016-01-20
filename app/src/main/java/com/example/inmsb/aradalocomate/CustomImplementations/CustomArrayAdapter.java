package com.example.inmsb.aradalocomate.CustomImplementations;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by inmsb on 12/17/2015.
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {

    public CustomArrayAdapter(Context context, int textViewResourceId, ArrayList<String> listItems) {
        super(context, textViewResourceId,listItems);
    }

    private int[] colors = new int[] { 0x30ffffff, 0x30808080 };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        int colorPos = position % colors.length;
        view.setBackgroundColor(colors[colorPos]);
        return view;
    }
}
