package com.ngynstvn.android.blocspot.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.api.model.POI;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Utils {

    public static final String FILTER_LIST = "filter_list";

    public static int generateRandomColor(int baseColor) {

        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        int baseRed = (baseColor & 0xFF0000) >> 16;
        int baseGreen = (baseColor & 0x00FF00) >> 8;
        int baseBlue = baseColor & 0xFF;

        red = (red + baseRed)/2;
        green = (green + baseGreen)/2;
        blue = (blue + baseBlue)/2;

        return 0xFF000000 | (red << 16) | (green << 8) | blue;

    }

    public static void displayPOIInfo(String TAG, POI poi) {

        // Display information about the POI

        Log.v(TAG, "row_id: " + poi.getRowId() + " | " + poi.getLocationName() + " | " + poi.getCategoryName() +
                " | " + poi.getCategoryColor() + " | " + poi.getAddress() + " | " + poi.getCity()
                + " | " + poi.getState() + " | " + poi.getLatitudeValue() + " | "
                + poi.getLongitudeValue() + " | " + poi.getDescription() + " | "
                + poi.getPlaceURL() + " | " + poi.getRatingImgURL() + " | " + poi.getLogoURL()
                + " | " + poi.isHasVisited());

        // Check blank category names

        if(poi.getCategoryName() == null) {
            Log.v(TAG, "Category name is null");
        }
        else if(poi.getCategoryName() != null){
            Log.v(TAG, "Category name is not null. Length: " + poi.getCategoryName().length());
        }

    }

    public static void logSearchResult(String TAG, String locationName, String address, String city,
                                       String state, double latitude, double longitude, String placeURL,
                                       String ratingURL, String logoURL) {

        // To log the search results

        Log.v(TAG, "Place: " + locationName + " | Address: " + address + " | City: "
                + city + " | State: " + state + " | Latitude: " + latitude + " | Longitude: " + longitude);

    }

    /*
     *
     * LatLng to City converter
     *
     */

    public static String latlngToCity(String TAG, double latitude, double longitude) {

        String city = "";

        try {
            Geocoder geocoder = new Geocoder(BlocspotApplication.getSharedInstance());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            city = addresses.get(0).getLocality();
//            Log.v(TAG, "Location: " + location);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return city;
    }

        /*
     *
     * LatLng to City converter
     *
     */

    public static String latlngToZipCode(String TAG, double latitude, double longitude) {

        String zipCode = null;

        try {
            Geocoder geocoder = new Geocoder(BlocspotApplication.getSharedInstance());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            zipCode = addresses.get(0).getPostalCode();
//            Log.v(TAG, "Zip Code: " + zipCode);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return zipCode;
    }

    public static SharedPreferences newSPrefInstance(String name) {
        return BlocspotApplication.getSharedInstance().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void putSPrefBooleanValue(SharedPreferences sharedPreferences, String fileName, String key, boolean value) {
        sharedPreferences = BlocspotApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void delAllSPrefValues(SharedPreferences sharedPreferences, String fileName) {
        sharedPreferences = BlocspotApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void delSPrefValue(SharedPreferences sharedPreferences, String fileName, String key) {
        sharedPreferences = BlocspotApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
