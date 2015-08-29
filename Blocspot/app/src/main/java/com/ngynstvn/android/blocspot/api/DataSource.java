package com.ngynstvn.android.blocspot.api;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.api.model.Category;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocspot.api.model.database.table.POITable;
import com.ngynstvn.android.blocspot.ui.UIUtils;

import java.util.ArrayList;

public class DataSource {

    // For log messaging

    private static final String TAG = "Test (" + DataSource.class.getSimpleName() + ")";

    // Member variables

    private POI poi1;
    private POI poi2;
    private POI poi3;
    private POI poi4;
    private POI poi5;
    private POI poi6;

    private POITable poiTable;
    private DatabaseOpenHelper databaseOpenHelper;
    private ArrayList<POI> poiArrayList = new ArrayList<>();
    private ArrayList<Category> categoryArrayList = new ArrayList<>();

    // Constructor

    public DataSource(Context context) {

        Log.v(TAG, "DataSource instantiated");

        poiTable = new POITable();

        new Handler().postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                databaseOpenHelper = new DatabaseOpenHelper(BlocspotApplication.getSharedInstance(), poiTable);
            }
        });

        context.deleteDatabase("blocspot_db");

        // Insert fake data here to test database insertion. WILL REMOVE LATER
        fakeDataTest();
    }

    // ---- Test insertion with fake data.... Will remove later! ---- //

    public void fakeDataTest() {

        Log.v(TAG, "fakeDataTest() called");

        poi1 = new POI(1, "Glendale Galleria", "Social", 0, "100 W Broadway", "Glendale", "CA",
                0.00f, 0.00f, "A very well known mall in the city. Across from Americana.",
                false, 2.3f);

        poi2 = new POI(2, "Boba 7", "Alcohol", 0, "518 7th St", "Los Angeles", "CA", 0.00f, 0.00f,
                "This place serves alcoholic boba. What an interesting place!", false, 9.3f);

        poi3 = new POI(3, "Perch", "Nightclub", 0, "448 S Hill St", "Los Angeles", "CA", 0.00f, 0.00f,
                "A night club out in downtown Los Angeles. Beautiful view of downtown when at the top!"
                , false, 9.6f);

        poi4 = new POI(4, "Walt Disney Concert Hall", "Entertainment", 0, "111 S Grand Avenue", "Los Angeles", "CA",
                0.00f, 0.00f, "I never knew what this building was. Oddly shaped but I found out it's " +
                "related to Disney! D:", false, 8.7f);

        poi5 = new POI(5, "DogHaus", "Food", 0, "3817 W Olive Ave", "Burbank", "CA", 0.00f, 0.00f,
                "I heard this place has crazy hot dogs! Not like those typical dodger dogs!"
                , false, 5.0f);

        poi6 = new POI(6, "7-Eleven", "Food", 0, "843 Glenoaks Blvd", "Glendale", "CA", 0.00f, 0.00f,
                "Just a convenience store..."
                , false, 5.0f);

        Handler handler = new Handler();

        // post and post-delayed

        handler.post(new Runnable() {
            @Override
            public void run() {

                poi1.setLatLngValue();
                poi2.setLatLngValue();
                poi3.setLatLngValue();
                poi4.setLatLngValue();
                poi5.setLatLngValue();
                poi6.setLatLngValue();

                Log.v(TAG, "LatLng conversion successful.");

                poiArrayList.add(poi1);
                poiArrayList.add(poi2);
                poiArrayList.add(poi3);
                poiArrayList.add(poi4);
                poiArrayList.add(poi5);
                poiArrayList.add(poi6);

                Log.v(TAG, "POI object addition successful.");

                insertPOIToDB(poi1);
                insertPOIToDB(poi2);
                insertPOIToDB(poi3);
                insertPOIToDB(poi4);
                insertPOIToDB(poi5);
                insertPOIToDB(poi6);

                Log.v(TAG, "Database insertion successful.");

                categoryArrayList.add(new Category(poi1.getCategory(),
                        UIUtils.generateRandomColor(android.R.color.white)));
                categoryArrayList.add(new Category(poi2.getCategory(),
                        UIUtils.generateRandomColor(android.R.color.white)));
                categoryArrayList.add(new Category(poi3.getCategory(),
                        UIUtils.generateRandomColor(android.R.color.white)));
                categoryArrayList.add(new Category(poi4.getCategory(),
                        UIUtils.generateRandomColor(android.R.color.white)));
                categoryArrayList.add(new Category(poi5.getCategory(),
                        UIUtils.generateRandomColor(android.R.color.white)));
                categoryArrayList.add(new Category(poi6.getCategory(),
                        UIUtils.generateRandomColor(android.R.color.white)));

            }
        });
    }

    public ArrayList<POI> getPoiArrayList() {
        Log.v(TAG, "getPOIArrayList() called");
        return poiArrayList;
    }

    public ArrayList<Category> getCategoryArrayList() {
        return categoryArrayList;
    }

    // Test method to insert into database

    public long insertPOIToDB(POI poi) {

        Log.v(TAG, "insertPOIToDB() called");

        if (poi == null) {
            return -1L;
        }

        // To convert boolean into integer equivalent
        // since DBs don't take boolean

        int boolInt = 0;

        if (poi.isHasVisited()) {
            boolInt = 1;
        } else {
            boolInt = 0;
        }

        return new POITable.Builder()
                .setLocationName(poi.getLocationName())
                .setCategory(poi.getCategory())
                .setCategoryColor(poi.getCategoryColor())
                .setAddress(poi.getAddress())
                .setCity(poi.getCity())
                .setState(poi.getState())
                .setLatitude(poi.getLatitudeValue())
                .setLongitude(poi.getLongitudeValue())
                .setDescription(poi.getDescription())
                .setHasVisited(boolInt)
                .insert(databaseOpenHelper.getWritableDatabase());
    }

    // --------- Database Methods ---------- //

    // Add POI item from DB

    static POI itemFromCursor(final Cursor cursor) {

        Log.v(TAG, "itemFromCursor() called");

        POI poi = new POI(POITable.getRowId(cursor));

        if (POITable.getColumnHasVisited(cursor) == 1) {
            poi.setHasVisited(true);
        }
        else if (POITable.getColumnHasVisited(cursor) == 0) {
            poi.setHasVisited(false);
        }

        return new POI(POITable.getRowId(cursor), POITable.getLocationName(cursor), POITable.getCategory(cursor),
                POITable.getCategoryColor(cursor), POITable.getAddress(cursor), POITable.getCity(cursor),
                POITable.getState(cursor), POITable.getLatitude(cursor), POITable.getLongtitude(cursor),
                POITable.getColumnDescription(cursor), poi.isHasVisited(), 0.2f);
    }

}
