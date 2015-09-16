package com.ngynstvn.android.blocspot.api.model;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.ngynstvn.android.blocspot.BlocspotApplication;

import java.io.Serializable;
import java.util.List;

public class POI extends Model implements Serializable {

    // Class variables

    private static String TAG = "Test (" + POI.class.getSimpleName() + ")";
    private static String CLASS_NAME = POI.class.getSimpleName();

    // Member variables and initialization

    private String locationName = "";
    private String categoryName = "";
    private int categoryColor = 0;
    private String address = "";
    private String city = "";
    private String state = "";
    private double latitudeValue = 0.00d;
    private double longitudeValue = 0.00d;
    private String description = "";
    private boolean hasVisited = false;
    private float distanceToPOI = 0.00f;

    // Constructor

    public POI(long rowId, String locationName, String categoryName, int categoryColor, String address,
               String city, String state, double latitudeValue, double longitudeValue,
               String description, boolean hasVisited, float distanceToPOI) {
        super(rowId);
        this.locationName = locationName;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.address = address;
        this.city = city;
        this.state = state;
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
        this.description = description;
        this.hasVisited = hasVisited;
        this.distanceToPOI = distanceToPOI;
    }

    // Setters and Getters

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(int categoryColor) {
        this.categoryColor = categoryColor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLatLngValue() {

        Geocoder geocoder = new Geocoder(BlocspotApplication.getSharedInstance());
        List<Address> list;
        Address address;
        try {
            list = geocoder.getFromLocationName(getAddress() + " " + getCity() + " " +
                    getState(), 1);

            address = list.get(0);

            // Obtain the values and set them in POI object

            latitudeValue = address.getLatitude();
            longitudeValue = address.getLongitude();
        }
        catch(Exception e) {
            Log.v(TAG, CLASS_NAME + " Unable to insert latitude for " + getAddress() + " "
                    + getCity() + " " + getState());

            Log.v(TAG, CLASS_NAME + " Unable to insert longitude for " + getAddress() + " "
                    + getCity() + " " + getState());
        }
    }

    public double getLatitudeValue() {
        return latitudeValue;
    }

    public double getLongitudeValue() {
        return longitudeValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasVisited() {
        return hasVisited;
    }

    public void setHasVisited(boolean hasVisited) {
        this.hasVisited = hasVisited;
    }

    public float getDistanceToPOI() {
        return distanceToPOI;
    }

    public void setDistanceToPOI(float distanceToPOI) {
        this.distanceToPOI = distanceToPOI;
    }
}
