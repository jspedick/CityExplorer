package com.mobdev.cityexplorer.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhillyHeatMap extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    protected ArrayList<LatLng> locationList = null;

    protected ArrayList<String> routesArray = null;

    private int filter_choice = 0;

    private boolean firstFire = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_philly_heat_map);
        locationList = getIntent().getParcelableArrayListExtra("locationList");
        routesArray = getIntent().getStringArrayListExtra("routesArray");
        setUpMapIfNeeded();
        Button trekLauncherButton = (Button)findViewById(R.id.trek_launcher_new);
        trekLauncherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhillyHeatMap.this, TrekCategories.class);
                startActivity(intent);
            }
        });
        //Create the drop down menu for the filter
        Spinner heatmapFilter = (Spinner)findViewById(R.id.heatmapFilter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_dropdown, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        heatmapFilter.setAdapter(adapter);
        heatmapFilter.setPrompt("Select Filter");
        heatmapFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(firstFire) {
                    filter_choice = position;
                    Intent intent2 = new Intent(PhillyHeatMap.this, PhillyHeatMapPlaces.class);
                    intent2.putExtra("filter_choice", filter_choice);
                    startActivity(intent2);
                }
                firstFire= true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(routesArray!=null) {
            for(int i=0;i<routesArray.size()-1;i++)
                drawPath(routesArray.get(i));
            placeMarkers(routesArray.get(routesArray.size()-1));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng centerCity = new LatLng(39.952584,-75.165222);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerCity, 12));
        if(locationList != null && locationList.size()>0)
        {
            addHeatMap(locationList);
        }
    }

    private void addHeatMap(ArrayList<LatLng> list){
        // Create a heat map tile provider, passing it the latlngs of the police stations.
       HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    /*This function draws the path using line segments given by the decoded string*/
    public void drawPath(String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
           // LatLng destination = list.get(1);
            //subDestinations.add(destination);

            for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                        .width(10)
                        .color(Color.BLUE).geodesic(true));
            }

        }
        catch (JSONException e) {

        }
    }

    /*This was an algorithm to decode the string that has the encoded route for the lines to be
    * drawn. Obtained from Stackoverflow.com*/
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    /*Based on the trek that the user has selected markers will be placed on their locations
    * displaying the name of the place in the title*/
    public void placeMarkers(String trekName){

            ParseQuery<ParseObject> qTrek = ParseQuery.getQuery("SubCategory");
            List<ParseObject> subDestination = null;
            try {
                qTrek.whereEqualTo("TrekName", trekName);
                subDestination = qTrek.find();
                for(int i = 0; i< subDestination.size(); i++) {
                    Double lat = subDestination.get(i).getDouble("Lat");
                    Double lon = subDestination.get(i).getDouble("Longit");
                    String subName = subDestination.get(i).getString("SubName");
                    LatLng coordinate = new LatLng(lat,lon);
                    mMap.addMarker(new MarkerOptions()
                            .position(coordinate)
                            .title(subName));
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }

    }

}
