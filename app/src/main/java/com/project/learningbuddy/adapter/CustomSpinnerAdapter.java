package com.project.learningbuddy.adapter;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> items;
    private Typeface customTypeface; // Your custom Typeface

    public CustomSpinnerAdapter(Context context, int resource, List<String> items, Typeface customTypeface) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
        this.customTypeface = customTypeface;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(customTypeface); // Set the custom Typeface
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(customTypeface); // Set the custom Typeface
        return view;
    }
}
