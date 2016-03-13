package com.mobdev.cityexplorer.myapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wildcat on 4/13/2015.
 * Launched from clicking on a specific trek.
 * Displays the subcategories associated with that trek.
 * Passes data to the map to be used for the pins and navigation.
 */
public class SubCategories extends ListActivity {

    private ItemAdapter m_adapter;
    List<String> listURL = new ArrayList<String>();
    private ArrayList<Item> subNames = new ArrayList<Item>();

    ListView listView1;

    Double [] daLat;
    Double [] daLongit;

    String sTrekName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_categories);

        Button navButton = (Button)findViewById(R.id.bStartTrek);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> routesArray= new ArrayList<String>();
                for(int i =0; i<daLat.length-1;i++) {
                    String navURL = makeURL(daLat[i], daLongit[i], daLat[i+1], daLongit[i+1]);
                    String jsonString = getJSONFromUrl(navURL);
                    routesArray.add(jsonString);
                }
                    routesArray.add(sTrekName);
                    Intent intent = new Intent(SubCategories.this, PhillyHeatMap.class);
                    intent.putExtra("routesArray", routesArray);
                    startActivity(intent);
            }
        });


        listView1 = (ListView) findViewById(android.R.id.list);

        //bundle is received from TrekCategories
        //queries based on this trekName to find the subdestinations
        Bundle extras = getIntent().getExtras();
        sTrekName = extras.getString("trekName");

        ParseQuery<ParseObject> qTrek = ParseQuery.getQuery("SubCategory");
        qTrek.whereEqualTo("TrekName", sTrekName);

        try {
            List<ParseObject> parseList = qTrek.find();

            daLat= new Double[parseList.size()];
            daLongit = new Double[parseList.size()];

            for (int i = 0; i < parseList.size(); i++){
                String subName = parseList.get(i).getString("SubName");
                String sVotes = parseList.get(i).getString("Votes");
                String sDescription = parseList.get(i).getString("Description");
                String sUrl = parseList.get(i).getString("URL");
                daLat[i] = parseList.get(i).getDouble("Lat");
                daLongit[i] = parseList.get(i).getDouble("Longit");
                int iImageID = parseList.get(i).getInt("imageID");
                listURL.add(0,sUrl);
                //creates a new Item from the Item class
                subNames.add(new Item(subName,sVotes,sDescription, sUrl, iImageID));
            }
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        m_adapter = new ItemAdapter(this, R.layout.list_item_sub, subNames);
        listView1.setAdapter(m_adapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                //launches the website of the subcategory that is clicked
                Toast.makeText(
                        getApplicationContext(),
                        listURL.get(position)+"", Toast.LENGTH_SHORT)
                        .show();
                i.setData(Uri.parse(listURL.get(position)));
                startActivity(i);
            }
        });

    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }

    public String getJSONFromUrl(String url) {
        InputStream is = null;
        String json = "";
        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            json = sb.toString();
            is.close();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;
    }

    public void getRoutes(){

    }
}