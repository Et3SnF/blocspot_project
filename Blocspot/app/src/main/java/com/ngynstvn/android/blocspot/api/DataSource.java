package com.ngynstvn.android.blocspot.api;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.api.model.Category;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocspot.api.model.database.table.POITable;
import com.ngynstvn.android.blocspot.ui.UIUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class DataSource {

    // For log messaging

    private static final String TAG = "Test (" + DataSource.class.getSimpleName() + ")";

    public static interface DataSourceDelegate {
        public void onFetchingComplete(ArrayList<POI> poiArrayList);
    }

    private WeakReference<DataSourceDelegate> dataSourceDelegate;

    public void setDataSourceDelegate(DataSourceDelegate dataSourceDelegate) {
        this.dataSourceDelegate = new WeakReference<DataSourceDelegate>(dataSourceDelegate);
    }

    public DataSourceDelegate getDataSourceDelegate() {
        return dataSourceDelegate.get();
    }

    // ---- ----- //

    // Member variables

    private final int BASE_COLOR = android.R.color.white;

    private ExecutorService executorService;

    private POI poi1;
    private POI poi2;
    private POI poi3;
    private POI poi4;
    private POI poi5;

    private POITable poiTable;
    private DatabaseOpenHelper databaseOpenHelper;
    private ArrayList<POI> poiArrayList = new ArrayList<>();
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private Map<String, Integer> catNameColorMap = new HashMap<String, Integer>();

    // Constructor

    public DataSource(Context context) {

        Log.v(TAG, "DataSource instantiated");

        context.deleteDatabase("blocspot_db");

        poiTable = new POITable();
        databaseOpenHelper = new DatabaseOpenHelper(BlocspotApplication.getSharedInstance(), poiTable);
        fetchAllPOIs();
    }

    // ---- Test insertion with fake data.... Will remove later! ---- //

    public void dbFakeData() {

        Log.v(TAG, "dbFakeData() called");

        SQLiteDatabase writableDatabase = databaseOpenHelper.getWritableDatabase();

        new POITable.Builder()
                .setLocationName("Glendale Galleria")
                .setCategory("Social")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .setAddress("100 W Broadway")
                .setCity("Glendale")
                .setState("CA")
                .setLatitude(34.1460872)
                .setLongitude(-118.2561726)
                .setDescription("A very well known mall in the city. Across from Americana.")
                .setHasVisited(0)
                .insert(writableDatabase);

        new POITable.Builder()
                .setLocationName("Boba 7")
                .setCategory("Alcohol")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .setAddress("518 7th St")
                .setCity("Los Angeles")
                .setState("CA")
                .setLatitude(34.0410142)
                .setLongitude(-118.2472838)
                .setDescription("This place serves alcoholic boba. What an interesting place!")
                .setHasVisited(1)
                .insert(writableDatabase);

        new POITable.Builder()
                .setLocationName("Perch")
                .setCategory("Nightclub")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .setAddress("448 S Hill St")
                .setCity("Los Angeles")
                .setState("CA")
                .setLatitude(34.0488342)
                .setLongitude(-118.2513587)
                .setDescription("A night club out in downtown Los Angeles. Beautiful view of downtown when at the top! ")
                .setHasVisited(0)
                .insert(writableDatabase);

        new POITable.Builder()
                .setLocationName("Walt Disney Concert Hall")
                .setCategory("Entertainment")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .setAddress("111 S Grand Avenue")
                .setCity("os Angeles")
                .setState("CA")
                .setLatitude(34.0554362)
                .setLongitude(-118.24994)
                .setDescription("I never knew what this building was until I found out it was related to Disney!")
                .setHasVisited(0)
                .insert(writableDatabase);

        new POITable.Builder()
                .setLocationName("DogHaus")
                .setCategory("Food")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .setAddress("3817 W Olive Ave")
                .setCity("Burbank")
                .setState("CA")
                .setLatitude(34.15087)
                .setLongitude(-118.340852)
                .setDescription("I heard this place has crazy hot dogs! Not like those typical dodger dogs!")
                .setHasVisited(0)
                .insert(writableDatabase);

        new POITable.Builder()
                .setLocationName("Urban Ramen")
                .setCategory("Food")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .setAddress("7300 Sunset Blvd")
                .setCity("Los Angeles")
                .setState("CA")
                .setLatitude(34.097537)
                .setLongitude(-118.350048)
                .setDescription("Located in West Hollywood. I heard this ramen place is good!")
                .setHasVisited(0)
                .insert(writableDatabase);
    }

    public void fakeDataTest() {

        Log.v(TAG, "fakeDataTest() called");

        poi1 = new POI(1, "Glendale Galleria", "Social", UIUtils.generateRandomColor(BASE_COLOR),
                "100 W Broadway", "Glendale", "CA", 0.00f, 0.00f, "A very well known mall in the " +
                "city. Across from Americana.", false, 2.3f);

        poi2 = new POI(2, "Boba 7", "Alcohol", UIUtils.generateRandomColor(BASE_COLOR), "518 7th St",
                "Los Angeles", "CA", 0.00f, 0.00f, "This place serves alcoholic boba. What an " +
                "interesting place!", true, 9.3f);

        poi3 = new POI(3, "Perch", "Nightclub", UIUtils.generateRandomColor(BASE_COLOR), "448 S " +
                "Hill St", "Los Angeles", "CA", 0.00f, 0.00f, "A night club out in downtown Los " +
                "Angeles. Beautiful view of downtown when at the top!", false, 9.6f);

        poi4 = new POI(4, "Walt Disney Concert Hall", "Entertainment",
                UIUtils.generateRandomColor(BASE_COLOR), "111 S Grand Avenue", "Los Angeles", "CA",
                0.00f, 0.00f, "I never knew what this building was. Oddly shaped but I found out it's " +
                "related to Disney! D:", false, 8.7f);

        poi5 = new POI(5, "DogHaus", "Food", UIUtils.generateRandomColor(BASE_COLOR), "3817 W Olive" +
                " Ave", "Burbank", "CA", 0.00f, 0.00f, "I heard this place has crazy hot dogs! Not " +
                "like those typical dodger dogs!", false, 5.0f);

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

                insertNewPOI(poi1);
                insertNewPOI(poi2);
                insertNewPOI(poi3);
                insertNewPOI(poi4);
                insertNewPOI(poi5);

                Log.v(TAG, "Database insertion successful.");

//                addToCategoryArrayList(poi1);
//                addToCategoryArrayList(poi2);
//                addToCategoryArrayList(poi3);
//                addToCategoryArrayList(poi4);
//                addToCategoryArrayList(poi5);

                Log.v(TAG, "POI1 - " + "Name: " + poi1.getCategoryName() + " | " + "Color: " + poi1.getCategoryColor());
                Log.v(TAG, "POI2 - " + "Name: " + poi2.getCategoryName() + " | " + "Color: " + poi2.getCategoryColor());
                Log.v(TAG, "POI3 - " + "Name: " + poi3.getCategoryName() + " | " + "Color: " + poi3.getCategoryColor());
                Log.v(TAG, "POI4 - " + "Name: " + poi4.getCategoryName() + " | " + "Color: " + poi4.getCategoryColor());
                Log.v(TAG, "POI5 - " + "Name: " + poi5.getCategoryName() + " | " + "Color: " + poi5.getCategoryColor());

                Log.v(TAG, "Category Map & ArrayList insertion successful.");

            }
        });
    }

    // ----- Separate Methods ----- //

        // Getting lists

    public ArrayList<POI> getPoiArrayList() {
        return poiArrayList;
    }

    public ArrayList<Category> getCategoryArrayList() {
        return categoryArrayList;
    }

        // Adding POI objects to lists

    private void addToCategoryMap(String categoryName) {

        if(catNameColorMap.containsKey(categoryName)) {
            Log.v(TAG, categoryName + " already has a color.");
            return;
        }

        catNameColorMap.put(categoryName, UIUtils.generateRandomColor(Color.WHITE));
    }

//    private void addToCategoryArrayList(POI poi) {
//        categoryArrayList.add(new Category(1, poi.getCategoryName(), poi.getCategoryColor()));
//
//        // Anything that exists in the array list are added to a map
//        catNameColorMap.put(poi.getCategoryName(), poi.getCategoryColor());
//    }
//
//        // Create new category
//
//    private void addNewCategory(String name) {
//        // Validation has already been taken care of. Just add a new category name and color
//
//        int color = UIUtils.generateRandomColor(BASE_COLOR);
//
//        categoryArrayList.add(new Category(1, name, color));
//        catNameColorMap.put(name, color);
//    }

    // --------- Database Methods ---------- //

    // Test method to insert into database

    public long insertNewPOI(POI poi) {

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
                .setCategory(poi.getCategoryName())
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

    public void fetchAllPOIs() {

        Log.v(TAG, "fetchAllPOIs() called");

        // Perform AsyncTask at startup

        new AsyncTask<Void, Void, ArrayList<POI>>() {

            @Override
            protected void onPreExecute() {
                Log.v(TAG, "onPreExecute() performing in " + Thread.currentThread().getName());
                dbFakeData();
            }

            @Override
            protected ArrayList<POI> doInBackground(Void... params) {

                Log.v(TAG, "doInBackground() performing in " + Thread.currentThread().getName());

                ArrayList<POI> poiArrayList = new ArrayList<>();

                Cursor cursor = POITable.getAllPOIs(databaseOpenHelper.getReadableDatabase());

                if(cursor == null || databaseOpenHelper == null) {

                    Log.v(TAG, "Null encountered");

                    Toast.makeText(BlocspotApplication.getSharedInstance(), "Null encountered",
                            Toast.LENGTH_SHORT).show();
                    return null;
                }

                if(cursor.moveToFirst()) {

                    do {
                        poiArrayList.add(itemFromCursor(cursor));
                    }
                    while(cursor.moveToNext());

                }

                return poiArrayList;

            }

            // Runs on UI Thread

            @Override
            protected void onPostExecute(ArrayList<POI> poiArrayList) {

                Log.v(TAG, "onPostExecute() performing in " + Thread.currentThread().getName());
                Log.v(TAG, "Size in Asynchronous ArrayList " + poiArrayList.size() + "");

                if(dataSourceDelegate == null) {
                    Log.v(TAG, "Issue connecting ArrayList with geofence!");
                    return;
                }

                DataSource.this.poiArrayList.addAll(poiArrayList);

                getDataSourceDelegate().onFetchingComplete(poiArrayList);
            }

        }.execute();

    }

    public void fetchAllCategories() {

    }

    public void filterByCategory(final Cursor cursor) {

    }

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
                POITable.getState(cursor), POITable.getLatitude(cursor), POITable.getLongitude(cursor),
                POITable.getColumnDescription(cursor), poi.isHasVisited(), 0.2f);
    }

    // Null column hack - If you want everything in a row to be null, make one of the items null by
    // specifying it in the nullColumnHack parameter



}
