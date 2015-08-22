package com.ngynstvn.android.blocspot.api;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocspot.api.model.database.table.POITable;

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

    private POITable poiTable;
    private DatabaseOpenHelper databaseOpenHelper;
    private ArrayList<POI> poiArrayList = new ArrayList<>();

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

        poi1 = new POI(1, "Japanese American National Museum", "Museum", "100 N Central Avenue", "Los Angeles", "CA",
                0.00f, 0.00f, "A museum about the history of Japanese Americans during World War II",
                false, 0.25f);

        poi2 = new POI(2, "Boba 7", "Alcohol", "518 7th St", "Los Angeles", "CA", 0.00f, 0.00f,
                "This place serves alcoholic boba. What an interesting place!", false, 0.3f);

        poi3 = new POI(3, "Perch", "Nightclub", "448 S Hill St", "Los Angeles", "CA", 0.00f, 0.00f,
                "A night club out in downtown Los Angeles. Beautiful view of downtown when at the top!"
                , false, 0.4f);

        poi4 = new POI(4, "Walt Disney Concert Hall", "Entertainment", "111 S Grand Avenue", "Los Angeles", "CA",
                0.00f, 0.00f, "I never knew what this building was. Oddly shaped but I found out it's " +
                "related to Disney! D:", false, 0.5f);

        poi5 = new POI(5, "LA City Hall", "Politics", "200 N Spring St", "Los Angeles", "CA", 0.00f, 0.00f,
                "Hmm...I don't know why I put this thing here. Scariest place in Los Angeles in my opinion!"
                , false, 0.9f);

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

                Log.v(TAG, "LatLng conversion successful.");

                poiArrayList.add(poi1);
                poiArrayList.add(poi2);
                poiArrayList.add(poi3);
                poiArrayList.add(poi4);
                poiArrayList.add(poi5);

                Log.v(TAG, "POI object addition successful.");

                insertPOIToDB(poi1);
                insertPOIToDB(poi2);
                insertPOIToDB(poi3);
                insertPOIToDB(poi4);
                insertPOIToDB(poi5);

                Log.v(TAG, "Database insertion successful.");

            }
        });
    }

    public ArrayList<POI> getPoiArrayList() {
        Log.v(TAG, "getPOIArrayList() called");
        return poiArrayList;
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
                .setAddress(poi.getAddress())
                .setCity(poi.getCity())
                .setState(poi.getState())
                .setLatitude(poi.getLatitudeValue())
                .setLongitude(poi.getLongitudeValue())
                .setDescription(poi.getDescription())
                .setHasVisited(boolInt)
                .insert(databaseOpenHelper.getWritableDatabase());
    }


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
                POITable.getAddress(cursor), POITable.getCity(cursor), POITable.getState(cursor),
                POITable.getLatitude(cursor), POITable.getLongtitude(cursor), POITable.getColumnDescription(cursor),
                poi.isHasVisited(), 0.2f);
    }

}
