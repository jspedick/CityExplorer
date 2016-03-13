package com.mobdev.cityexplorer.myapplication;

/**
 * Created by wildcat on 4/14/2015.
 * This class creates the expandable list view for the treks.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    List<ParseObject> infoParseList;

    TextView tvVotes;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    /* Child is for the menu that is dropped down from the parent*/
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        //TextViews to display Name, Time, Cost
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        TextView tvCost = (TextView) convertView.findViewById(R.id.tvCost);
        TextView tvOverview = (TextView) convertView.findViewById(R.id.tvOverview);
        tvVotes = (TextView) convertView.findViewById(R.id.tvVotes);
        ImageView ivTrekPic = (ImageView) convertView.findViewById(R.id.ivTrekPic);
        ImageButton ibVoteUp = (ImageButton) convertView.findViewById(R.id.ibVoteUp);
        ImageButton ibVoteDown = (ImageButton) convertView.findViewById(R.id.ibVoteDown);

        //calls functions to get information about the treks from the childview
        final String [] sGetCostRating = getTrekInfo(childText);
        int iGetTrekPic = getTrekPic(childText);

        //sets the texts based on the information returned above
        tvName.setText(childText);
        tvTime.setText(sGetCostRating[0]);
        tvCost.setText(sGetCostRating[1]);
        tvOverview.setText(sGetCostRating[2]);
        tvVotes.setText(sGetCostRating[3]);

        ivTrekPic.setImageResource(iGetTrekPic);

        //on click listener for upvote, downvotes
        //passes boolean value to updateVote function so it knows to increase or decrease count
        ibVoteUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int updatedVote = updateVote(true,childText);
                Log.d("vote=", "yo" + "");
                tvVotes.setText(String.valueOf(updatedVote));
            }
        });
        ibVoteDown.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int updatedVote = updateVote(false,childText);
                Log.d("vote=","yo" + "");
                tvVotes.setText(String.valueOf(updatedVote));
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    /* Group is for the parent class in the list view*/
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String[] getTrekInfo(String sTrekName){


        String [] aCostRating = new String[4];

        //queries trek based on name
        ParseQuery<ParseObject> qTrekInfo = ParseQuery.getQuery("Trek");
        try {
            qTrekInfo.whereEqualTo("Name", sTrekName);
            infoParseList = qTrekInfo.find();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0; i <infoParseList.size(); i++){
            aCostRating[0] = infoParseList.get(i).getString("Time");
            aCostRating[1] = infoParseList.get(i).getString("Cost");
            aCostRating[2] = infoParseList.get(i).getString("Overview");
            aCostRating[3] = String.valueOf(infoParseList.get(i).getInt("Votes"));
        }
        //returns values to be used in textviews
        return aCostRating;
    }

    public int getTrekPic(String sTrekName){

        //gets picture from the first subcategory to be used for the picture as the trek
        ParseObject pPic = null;
        int iImageID = 0;
        ParseQuery<ParseObject> qTrekPic = ParseQuery.getQuery("SubCategory");
        try {
            qTrekPic.whereEqualTo("TrekName", sTrekName);
            pPic = qTrekPic.getFirst();
            iImageID = pPic.getInt("imageID");
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
        //returns int because that's how R.drawable is stored as
        return iImageID;
    }

    private int updateVote(boolean b, String sTrekName) {

        ParseObject pVote = null;
        int voteCount = 0;
        ParseQuery<ParseObject> qTrekVotes = ParseQuery.getQuery("Trek");
        try {
            qTrekVotes.whereEqualTo("Name", sTrekName);
            pVote = qTrekVotes.getFirst();
            voteCount = pVote.getInt("Votes");

            //depends on whether upvote or downvote was clicked
            if (b == true){
                voteCount = voteCount + 1;
            }
            else if (b == false){
                voteCount = voteCount -1;
            }
            pVote.put("Votes",voteCount);
            pVote.save();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        return voteCount;
    }

}