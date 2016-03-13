package com.mobdev.cityexplorer.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class PhillyHeatMapPlaces extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "PhillyHeatMapPlaces";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    private static final String GOOGLE_API_KEY = "AIzaSyBI3NjrVgs4zb6x7_i3Q-GxUYA5bhdp0Dk";

    private ArrayList<LatLng> locationList = new ArrayList<LatLng>();

    private final HttpClient client = new DefaultHttpClient();

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;
    private String responseString = null;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //make the bars the default filter, otherwise receive filter in intent
        int filter_choice = getIntent().getIntExtra("filter_choice",0);
        ArrayList<String> typesArray = new ArrayList<String>();
        typesArray.add("bar");
        typesArray.add("store");
        typesArray.add("food");

        //Querying the north and south of Philly to get a better distribution in heat map
        ArrayList<Double> latArray = new ArrayList<Double>();
        latArray.add(39.916188);
        latArray.add(40.006843);
        ArrayList<Double> lonArray = new ArrayList<Double>();
        lonArray.add(-75.171504);
        lonArray.add(-75.142965);

        //Helping threading issues
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
        if(filter_choice!=3)
        for(int i = 0; i<2; i++) {
            Callable callable = new MyCallable(GOOGLE_API_KEY,
                                                typesArray.get(filter_choice),
                                                latArray.get(i), lonArray.get(i),
                                                5400,i);
            FutureTask<String> futureTask = new FutureTask<String>(callable);
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.execute(futureTask);
            while (true) {
                try {
                    if (futureTask.isDone()) {
                        responseString = futureTask.get();
                        break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    break;
                }
            }
            if (responseString != null) {
                extractLatLon(responseString);
            }
        }
        Intent intent = new Intent(PhillyHeatMapPlaces.this, PhillyHeatMap.class);
        intent.putParcelableArrayListExtra("locationList",locationList);
        startActivity(intent);
    }
    /*This function receives a json sring of the results from the Places query and parses it for
    * lat and lon coordinates*/
    public void extractLatLon(String json){
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray results = obj.getJSONArray("results");
            for(int i=0; i<results.length(); i++)
            {
                JSONObject location = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                String lat = location.getString("lat");
                String lng = location.getString("lng");
                LatLng coordinates = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                locationList.add(coordinates);
            }
        } catch (Throwable t) {}
    }

    /**
     * Called when the Activity is made visible.
     * A connection to Play Services need to be initiated as
     * soon as the activity is visible. Registers {@code ConnectionCallbacks}
     * and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        // TODO: Start making API requests.
    }

    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }
        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }
}
