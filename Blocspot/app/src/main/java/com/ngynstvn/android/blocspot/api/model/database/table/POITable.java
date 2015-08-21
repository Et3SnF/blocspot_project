package com.ngynstvn.android.blocspot.api.model.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class POITable extends Table {

    // Class variables

    private static final String TAG = "Test " + POITable.class.getSimpleName() + ")";

    private static final String NAME = "poi_table";
    private static final String COLUMN_LOCATION_NAME = "location_name";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_CITY = "city";
    private static final String COLUMN_STATE = "state";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGTITUDE = "longitude";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_HAS_VISITED = "has_visited";

    // Hold off on the distance to POI part for now...

    // Getters

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCreateStatement() {

        Log.v(TAG, "getCreateStatement() called");

        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_LOCATION_NAME + " TEXT,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_CITY + " TEXT,"
                + COLUMN_STATE + " TEXT,"
                + COLUMN_LATITUDE + " DOUBLE,"
                + COLUMN_LONGTITUDE + " DOUBLE,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_HAS_VISITED + " INTEGER);"; // 0 for false, 1 for true
    }

    // Builder Class

    public static class Builder implements Table.Builder {

        ContentValues values = new ContentValues();

        public Builder setLocationName(String name) {
            values.put(COLUMN_LOCATION_NAME, name);
            return this;
        }

        public Builder setCategory(String category) {
            values.put(COLUMN_CATEGORY, category);
            return this;
        }

        public Builder setAddress(String address) {
            values.put(COLUMN_ADDRESS, address);
            return this;
        }

        public Builder setCity(String city) {
            values.put(COLUMN_CITY, city);
            return this;
        }

        public Builder setState(String state) {
            values.put(COLUMN_STATE, state);
            return this;
        }

        public Builder setLatitude(double latitude) {
            values.put(COLUMN_LATITUDE, latitude);
            return this;
        }

        public Builder setLongitude(double longitude) {
            values.put(COLUMN_LONGTITUDE, longitude);
            return this;
        }

        public Builder setDescription(String description){
            values.put(COLUMN_DESCRIPTION, description);
            return this;
        }

        public Builder setHasVisited(int hasVisited) {
            values.put(COLUMN_HAS_VISITED, hasVisited);
            return this;
        }

        @Override
        public long insert(SQLiteDatabase writableDB) {
            return writableDB.insert(NAME, null, values);
        }
    }

    // Getters

    public static String getLocationName(Cursor cursor) {
        return getString(cursor, COLUMN_LOCATION_NAME);
    }

    public static String getCategory(Cursor cursor) {
        return getString(cursor, COLUMN_CATEGORY);
    }

    public static String getAddress(Cursor cursor) {
        return getString(cursor, COLUMN_ADDRESS);
    }

    public static String getCity(Cursor cursor) {
        return getString(cursor, COLUMN_CITY);
    }

    public static String getState(Cursor cursor) {
        return getString(cursor, COLUMN_STATE);
    }

    public static double getLatitude(Cursor cursor) {
        return getDouble(cursor, COLUMN_LATITUDE);
    }

    public static double getLongtitude(Cursor cursor) {
        return getDouble(cursor, COLUMN_LONGTITUDE);
    }

    public static String getColumnDescription(Cursor cursor) {
        return getString(cursor, COLUMN_DESCRIPTION);
    }

    public static int getColumnHasVisited(Cursor cursor) {
        return getInteger(cursor, COLUMN_HAS_VISITED);
    }
}
