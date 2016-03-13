package com.mobdev.cityexplorer.myapplication;

/**
 * Created by wildcat on 4/15/2015.
 * This class creates the custom adapter to display the row items for the subcategories
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Item> {

    // declaring ArrayList of items
    private ArrayList<Item> objects;

    /* override the constructor for ArrayAdapter
    * ArrayList<Item> objects is the list of objects we want to display.
    */
    public ItemAdapter(Context context, int textViewResourceId, ArrayList<Item> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    /*
     * overriding the getView method here, will define how each list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, inflate it.
        // to inflate means to show the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_sub, null);
        }

		/*
		 * Variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. 		 *
		 * i refers to the current Item object.
		 */
        Item i = objects.get(position);

        if (i != null) {

            //sets the textviews
            TextView tvSubName = (TextView) v.findViewById(R.id.tvSubName);
            TextView tvOverview = (TextView) v.findViewById(R.id.tvOverview);
            ImageView ivSubPic = (ImageView) v.findViewById(R.id.ivSubPic);

            if (tvSubName != null){
                tvSubName.setText(i.getName());
            }
            if (tvOverview != null){
                tvOverview.setText(i.getDescription());
            }
            if (ivSubPic != null){
                ivSubPic.setImageResource(i.getImageID());
            }
        }

        // the view must be returned to our activity
        return v;
    }
}
