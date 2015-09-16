package com.ngynstvn.android.blocspot.api.model;

public class PlaceResult extends Model {

    // Class variables

    private static String TAG = "Test (" + PlaceResult.class.getSimpleName() + ")";

    // Member variables and initialization

    private String locationName = "";
    private String address = "";
    private String city = "";
    private String state = "";
    private double latitudeValue = 0.00d;
    private double longitudeValue = 0.00d;
    private String placeURL = "";
    private String ratingImgURL = "";
    private String logoURL = "";

    // Constructor

    public PlaceResult(long rowId, String locationName, String address, String city, String state,
                       double latitudeValue, double longitudeValue, String placeURL,
                       String ratingImgURL, String logoURL) {
        super(rowId);
        this.locationName = locationName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.latitudeValue = latitudeValue;
        this.longitudeValue = longitudeValue;
        this.placeURL = placeURL;
        this.ratingImgURL = ratingImgURL;
        this.logoURL = logoURL;
    }

    // Setters and Getters

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public double getLatitudeValue() {
        return latitudeValue;
    }

    public void setLatitudeValue(double latitudeValue) {
        this.latitudeValue = latitudeValue;
    }

    public double getLongitudeValue() {
        return longitudeValue;
    }

    public void setLongitudeValue(double longitudeValue) {
        this.longitudeValue = longitudeValue;
    }

    public String getPlaceURL() {
        return placeURL;
    }

    public void setPlaceURL(String placeURL) {
        this.placeURL = placeURL;
    }

    public String getRatingImgURL() {
        return ratingImgURL;
    }

    public void setRatingImgURL(String ratingImgURL) {
        this.ratingImgURL = ratingImgURL;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

}
