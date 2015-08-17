package com.ngynstvn.android.blocspot.api;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocspot.api.model.database.table.POITable;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataSource {

    // Member variables

    // For log messaging

    private static final String TAG = "Test";

    private POI poi1;

    private POITable poiTable;
    private DatabaseOpenHelper databaseOpenHelper;
    private ExecutorService executorService;
    public ArrayList<POI> poiArrayList = new ArrayList<>();

    // Constructor

    public DataSource(Context context) {

        poiTable = new POITable();

        databaseOpenHelper = new DatabaseOpenHelper(context, poiTable);
        executorService = Executors.newSingleThreadExecutor();

        context.deleteDatabase("blocspot_db");

        // Insert fake data here to test database insertion. WILL REMOVE LATER
        fakeDataTest();
    }

    // ---- Test insertion with fake data.... Will remove later! ---- //

    public void fakeDataTest() {

        poi1 = new POI(1, "Japanese American National Museum", "Museum", "100 N Central Avenue", "Los Angeles", "CA",
                0.00f, 0.00f, "A museum about the history of Japanese Americans during World War II",
                false, 0.25f);

        poi1.setLatLngValue();

        poiArrayList.add(poi1);

        insertPOIToDB(poi1);
        Log.v(TAG, getClass().getSimpleName() + " Database insertion successful.");
    }

    public ArrayList<POI> getPoiArrayList() {
        return poiArrayList;
    }

    // Test method to insert into database

    public long insertPOIToDB(POI poi) {

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

        POI poi = new POI(POITable.getRowId(cursor));

        if (POITable.getColumnHasVisited(cursor) == 1) {
            poi.setHasVisited(true);
        } else if (POITable.getColumnHasVisited(cursor) == 0) {
            poi.setHasVisited(false);
        }

        return new POI(POITable.getRowId(cursor), POITable.getLocationName(cursor), POITable.getCategory(cursor),
                POITable.getAddress(cursor), POITable.getCity(cursor), POITable.getState(cursor),
                POITable.getLatitude(cursor), POITable.getLongtitude(cursor), POITable.getColumnDescription(cursor),
                poi.isHasVisited(), 0.2f);
    }

    // This allows to run things in a thread outside of UI thread. Use it when needed.

    void submitTask(Runnable task) {

        if (executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.submit(task);
    }
}
