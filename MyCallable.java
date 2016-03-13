package com.mobdev.cityexplorer.myapplication;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

public class MyCallable implements Callable<String> {
        String types,key;
        double lat, lon, radius;
        int token;

        /*MyCallable receives the parameters that will be used for Places Querying*/
        public MyCallable(String keyT, String typesT,double latT,double lonT,double radiusT,int tokenT)
        {
            key = keyT;
            types = typesT;
            lat = latT;
            lon = lonT;
            radius = radiusT;
            token = tokenT;

        }
    /*This call makes an HTTP request on the Google places server to query for the places
    * based off the filters given as parameters*/
    @Override
    public String call() throws Exception {
        String uriString = "https://maps.googleapis.com/maps/api/place/search/json?"
                + "radius="+radius+"&key="+key+"&location="+lat+","+lon+"&types="+types;
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet request = new HttpGet();
            java.net.URI uriJava = new java.net.URI(uriString);
            request.setURI(uriJava);
            HttpResponse response = httpclient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                out.close();
                return responseString;
            }
            else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        }
        catch(IOException | URISyntaxException e){};
        return null;
        //create List of LatLng objects
    }
}
