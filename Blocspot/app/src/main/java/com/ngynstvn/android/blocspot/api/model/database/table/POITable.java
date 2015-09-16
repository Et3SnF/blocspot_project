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
    private static final String COLUMN_CATEGORY_COLOR = "category_color";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_CITY = "city";
    private static final String COLUMN_STATE = "state";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PLACE_URL = "place_url";
    private static final String COLUMN_RATING_URL = "rating_url";
    private static final String COLUMN_LOGO_URL = "logo_url";
    private static final String COLUMN_HAS_VISITED = "has_visited";

    // Hold off on the distance to POI part for now...

    // SELECT DISTINCT(category) from poi_table --> Query to get categories and then you store them
    // an array using cursor

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
                + COLUMN_CATEGORY_COLOR + " INTEGER,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_CITY + " TEXT,"
                + COLUMN_STATE + " TEXT,"
                + COLUMN_LATITUDE + " DOUBLE,"
                + COLUMN_LONGITUDE + " DOUBLE,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_PLACE_URL + " TEXT,"
                + COLUMN_RATING_URL + " TEXT,"
                + COLUMN_LOGO_URL + " TEXT, "
                + COLUMN_HAS_VISITED + " INTEGER);"; // 0 for false, 1 for true
    }

    // NOTE rawquery() method: sql method must not end in ';'

    public static Cursor getAllPOIs(SQLiteDatabase readonlyDatabase) {
        // Select * FROM poi_table order has_visited;
        return readonlyDatabase.rawQuery("SELECT * FROM " + NAME + " ORDER BY " + COLUMN_HAS_VISITED, null);
    }

    // Getting filtered table data BY CATEGORY

    public static Cursor getFilteredPOIs(SQLiteDatabase readOnlyDatabase, String... strings) {
        // Select * from poi_table where category = "blah1" or category="pigeons" or ...;

        String conditions = "";

        if(strings.length == 0) {
            return readOnlyDatabase.rawQuery("Select * from " + NAME, null);
        }
        else if(strings.length == 1) {
            conditions += "category = " + "'" + strings[strings.length-1] + "'";
        }

        for(int i = 0; i < strings.length-1; i++) {
            conditions += "category = " + "'" + strings[i] + "'" + " or ";
        }

        conditions += "category = " + "'" + strings[strings.length-1] + "'";

        return readOnlyDatabase.rawQuery("Select * from " + NAME + " where " + conditions, null);
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

        public Builder setCategoryColor(int categoryColor) {
            values.put(COLUMN_CATEGORY_COLOR, categoryColor);
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
            values.put(COLUMN_LONGITUDE, longitude);
            return this;
        }

        public Builder setDescription(String description){
            values.put(COLUMN_DESCRIPTION, description);
            return this;
        }

        public Builder setPlaceURL(String placeURL) {
            values.put(COLUMN_PLACE_URL, placeURL);
            return this;
        }

        public Builder setRatingURL(String ratingURL) {
            values.put(COLUMN_RATING_URL, ratingURL);
            return this;
        }

        public Builder setLogoURL(String logoURL) {
            values.put(COLUMN_LOGO_URL, logoURL);
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

    public static int getCategoryColor(Cursor cursor) {
        return getInteger(cursor, COLUMN_CATEGORY_COLOR);
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

    public static double getLongitude(Cursor cursor) {
        return getDouble(cursor, COLUMN_LONGITUDE);
    }

    public static String getColumnDescription(Cursor cursor) {
        return getString(cursor, COLUMN_DESCRIPTION);
    }

    public static String getPlaceURL(Cursor cursor) {
        return getString(cursor, COLUMN_PLACE_URL);
    }

    public static String getRatingURL(Cursor cursor) {
        return getString(cursor, COLUMN_RATING_URL);
    }

    public static String getLogoURL(Cursor cursor) {
        return getString(cursor, COLUMN_LOGO_URL);
    }

    public static int getColumnHasVisited(Cursor cursor) {
        return getInteger(cursor, COLUMN_HAS_VISITED);
    }
}
