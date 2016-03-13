package com.mobdev.cityexplorer.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wildcat on 4/5/2015.
 * This class is launched from the map screen.
 */
public class TrekCategories extends Activity {

    List<ParseObject> catParseList;
    List<ParseObject> trekParseList;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    //needed for adapter
    List<String> listDataHeader = new ArrayList<String>();
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trek_cat_main);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(TrekCategories.this, SubCategories.class);
                intent.putExtra("trekName", listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
                startActivity(intent);
                return false;
            }
        });

    }
    /*
 * Preparing the list data
 */
    private void prepareListData() {

        //query to find the Trek categories
        ParseQuery<ParseObject> queryCategory = ParseQuery.getQuery("TrekCategory");
        try {
            catParseList = queryCategory.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        //initializes list and hashmap
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        //adds data to header list, and puts data in the child hashmap
        //calls populateTrekLists which gets the treks based on that category
        for (int i = 0; i < catParseList.size(); i++){
            listDataHeader.add(i,catParseList.get(i).getString("Name"));
            listDataChild.put(listDataHeader.get(i), populateTrekLists(listDataHeader.get(i)));
        }

    }
    private ArrayList<String> populateTrekLists(String sTrekCategory){
        ArrayList<String> trekListArray = new ArrayList<String>();

        //gets the treks based on the category passed in
        ParseQuery<ParseObject> queryTrek = ParseQuery.getQuery("Trek");
        try {
            queryTrek.whereEqualTo("Category", sTrekCategory);
            trekParseList = queryTrek.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < trekParseList.size(); i++){
            trekListArray.add(i,trekParseList.get(i).getString("Name"));
        }
        //returns the array list for the child
        return trekListArray;
    }
    protected void onDestroy(){
        super.onDestroy();

    }

}