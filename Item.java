package com.mobdev.cityexplorer.myapplication;

/**
 * Created by wildcat on 4/15/2015.
 * This class is used to create the items that will appear in the subcategories. It will be used
 * in the ItemAdapter class.
 */
public class Item {
    private String sName;
    private String sRating;
    private String sDescription;
    private String sUrl;
    private int iImageID;

    public Item(){
    //empty constructor
    }

    public Item(String sName, String sRating, String sDescription, String sUrl, int iImageID){
        //class constructor
        this.sName = sName;
        this.sRating = sRating;
        this.sDescription = sDescription;
        this.sUrl = sUrl;
        this.iImageID = iImageID;
    }

    //get and set variables for each variable
    public String getName() {return sName;}
    public void setName(String sName) {this.sName = sName;}

    public String getRating() {return sRating;}
    public void setRating(String sRating) {this.sRating = sRating;}

    public String getDescription() {return sDescription;}
    public void setDescription(String sDescription) {this.sDescription = sDescription;}

    public String getUrl() {return sUrl;}
    public void setUrl(String sUrl) {this.sUrl = sUrl;}

    public int getImageID() {return iImageID;}
    public void getImageID(int iImageID) {this.iImageID = iImageID;}

}
