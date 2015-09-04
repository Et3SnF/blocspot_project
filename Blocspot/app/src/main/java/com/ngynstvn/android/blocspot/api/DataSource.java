package com.ngynstvn.android.blocspot.api;

import android.content.ContentValues;
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
import com.ngynstvn.android.blocspot.api.model.database.table.CategoryTable;
import com.ngynstvn.android.blocspot.api.model.database.table.POITable;
import com.ngynstvn.android.blocspot.ui.UIUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataSource {

    // For log messaging

    private static final String TAG = "Test (" + DataSource.class.getSimpleName() + ")";
    private static final String POI_TABLE = "poi_table";
    private static final String CATEGORY_TABLE = "category_table";

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
    private final String DB_NAME = "blocspot_db";

    private DatabaseOpenHelper databaseOpenHelper;
    private POITable category;
    private CategoryTable categoryTable;

    private ArrayList<POI> poiArrayList = new ArrayList<>();
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private Map<String, Integer> catNameColorMap = new HashMap<String, Integer>();

    private Handler handler;

    // Constructor

    public DataSource(Context context) {

        Log.v(TAG, "DataSource instantiated");

        context.deleteDatabase(DB_NAME);

        category = new POITable();
        categoryTable = new CategoryTable();

        databaseOpenHelper = new DatabaseOpenHelper(BlocspotApplication.getSharedInstance(),
                category, categoryTable);

        dbFakeData();
        dbFakeCategoryData();

        fetchAllPOIs();
        fetchAllCategories();

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePOIsToDB();
            }
        }, 10000);
    }

    // ---- Test insertion with fake data.... Will remove later! ---- //

    private void dbFakeData() {

        Log.v(TAG, "dbFakeData() called");

        SQLiteDatabase writableDatabase = databaseOpenHelper.getWritableDatabase();

        new POITable.Builder()
                .setLocationName("Glendale Galleria")
                .setCategory("")
                .setCategoryColor(0)
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
                .setCategory("")
                .setCategoryColor(0)
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
                .setCategory("")
                .setCategoryColor(0)
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
                .setCategory("")
                .setCategoryColor(0)
                .setAddress("111 S Grand Avenue")
                .setCity("Los Angeles")
                .setState("CA")
                .setLatitude(34.0554362)
                .setLongitude(-118.24994)
                .setDescription("I never knew what this building was until I found out it was related to Disney!")
                .setHasVisited(0)
                .insert(writableDatabase);

        new POITable.Builder()
                .setLocationName("DogHaus")
                .setCategory("")
                .setCategoryColor(0)
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
                .setCategory("")
                .setCategoryColor(0)
                .setAddress("7300 Sunset Blvd")
                .setCity("Los Angeles")
                .setState("CA")
                .setLatitude(34.097537)
                .setLongitude(-118.350048)
                .setDescription("Located in West Hollywood. I heard this ramen place is good!")
                .setHasVisited(0)
                .insert(writableDatabase);

    }

    private void dbFakeCategoryData() {

        Log.v(TAG, "dbFakeCategoryData() called");

        SQLiteDatabase writableDatabase = databaseOpenHelper.getWritableDatabase();

        new CategoryTable.Builder()
                .setCategoryName("Restaurants")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .insert(writableDatabase);

        new CategoryTable.Builder()
                .setCategoryName("Entertainment")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .insert(writableDatabase);

        new CategoryTable.Builder()
                .setCategoryName("Night Life")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .insert(writableDatabase);

        new CategoryTable.Builder()
                .setCategoryName("Museums")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .insert(writableDatabase);

        new CategoryTable.Builder()
                .setCategoryName("Theme Parks")
                .setCategoryColor(UIUtils.generateRandomColor(BASE_COLOR))
                .insert(writableDatabase);
    }

    // ----- Separate Methods ----- //

        // Getting lists

    public ArrayList<POI> getPoiArrayList() {
        return poiArrayList;
    }

    public ArrayList<Category> getCategoryArrayList() {
        return categoryArrayList;
    }

    public Map<String, Integer> getCategoryMap() {
        return catNameColorMap;
    }

        // Adding POI objects to lists

    private void addToCategoryMap(String categoryName) {

        if(catNameColorMap.containsKey(categoryName)) {
            Log.v(TAG, categoryName + " already has a color.");
            return;
        }

        catNameColorMap.put(categoryName, UIUtils.generateRandomColor(Color.WHITE));
    }

    /**
     *
     * Update POI arrayList after something is updated in database (Category assignment, etc.)
     * Update the category array list if necessary when the category table is changed
     *
     */

    // --------- Database Methods ---------- //

        // PUSHING to DB methods

    public long addNewPOI(POI poi) {

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

    public long addNewCategory(Category category) {

        Log.v(TAG, "addNewCategory() called");

        if (category == null) {
            return -1L;
        }

        return new CategoryTable.Builder()
                .setCategoryName(category.getCategoryName())
                .setCategoryColor(category.getCategoryColor())
                .insert(databaseOpenHelper.getWritableDatabase());
    }

    public void updatePOIsToDB() {

        Log.v(TAG, "updatePOIsToDB() called");

        databaseOpenHelper.getReadableDatabase();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                int hasVisited = 0;

                for(POI poi : BlocspotApplication.getSharedDataSource().getPoiArrayList()) {

                    ContentValues newValues = new ContentValues();

                    if(poi.isHasVisited()) {
                        hasVisited = 1;
                    }
                    else {
                        hasVisited = 0;
                    }

                    newValues.put("location_name", poi.getLocationName());
                    newValues.put("category", poi.getCategoryName());
                    newValues.put("category_color", poi.getCategoryColor());
                    newValues.put("address", poi.getAddress());
                    newValues.put("city", poi.getCity());
                    newValues.put("state", poi.getState());
                    newValues.put("latitude", poi.getLatitudeValue());
                    newValues.put("longitude", poi.getLongitudeValue());
                    newValues.put("description", poi.getDescription());
                    newValues.put("has_visited", hasVisited);

                    databaseOpenHelper.getWritableDatabase().update(POI_TABLE, newValues, "id = ?",
                            new String[]{String.valueOf(poi.getRowId())});
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateCategoriesToDB();
            }

        }.execute();

    }

    public void updateCategoriesToDB() {

        Log.v(TAG, "updateCategoriesToDB() called");

        databaseOpenHelper.getReadableDatabase();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... params) {

                for(Category category : BlocspotApplication.getSharedDataSource().getCategoryArrayList()) {

                    ContentValues newValues = new ContentValues();

                    newValues.put("category", category.getCategoryName());
                    newValues.put("category_color", category.getCategoryColor());

                    databaseOpenHelper.getWritableDatabase().update(CATEGORY_TABLE, newValues, "id = ?",
                            new String[]{String.valueOf(category.getRowId())});
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                fetchAllPOIs();
                fetchAllCategories();
            }

        }.execute();

    }

        // PULLING methods

    public void fetchAllPOIs() {

        Log.v(TAG, "fetchAllPOIs() called");

        // Perform AsyncTask at startup

        new AsyncTask<Void, Void, ArrayList<POI>>() {

            @Override
            protected void onPreExecute() {
                Log.v(TAG, "onPreExecute() performing in " + Thread.currentThread().getName());

                if(!poiArrayList.isEmpty()) {
                    poiArrayList.clear();
                }
            }

            @Override
            protected ArrayList<POI> doInBackground(Void... params) {

                Log.v(TAG, "doInBackground() performing in " + Thread.currentThread().getName());

                ArrayList<POI> poiArrayList = new ArrayList<>();

                if(!poiArrayList.isEmpty()) {
                    poiArrayList.clear();
                }

                Cursor cursor = POITable.getAllPOIs(databaseOpenHelper.getReadableDatabase());

                if(cursor == null || databaseOpenHelper == null) {

                    Log.v(TAG, "Null encountered");

                    Toast.makeText(BlocspotApplication.getSharedInstance(), "Null encountered",
                            Toast.LENGTH_SHORT).show();
                    return null;
                }

                if(cursor.moveToFirst()) {

                    do {
                        poiArrayList.add(poiFromCursor(cursor));
                    }
                    while(cursor.moveToNext());

                    cursor.close();

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

                DataSource.this.poiArrayList = poiArrayList;
                getDataSourceDelegate().onFetchingComplete(poiArrayList);
            }

        }.execute();

    }

    public void fetchAllCategories() {

        new AsyncTask<Void, Void, ArrayList<Category>>() {

            @Override
            protected void onPreExecute() {

                if(!categoryArrayList.isEmpty()) {
                    categoryArrayList.clear();
                }

            }

            @Override
            protected ArrayList<Category> doInBackground(Void... params) {

                ArrayList<Category> categoryArrayList = new ArrayList<>();

                if(!categoryArrayList.isEmpty()) {
                    categoryArrayList.clear();
                }

                Cursor cursor = CategoryTable.getAllCategories(databaseOpenHelper.getReadableDatabase());

                if(cursor == null || databaseOpenHelper == null) {
                    Log.v(TAG, "Null encountered");

                    Toast.makeText(BlocspotApplication.getSharedInstance(), "Null encountered",
                            Toast.LENGTH_SHORT).show();
                    return null;
                }

                if(cursor.moveToFirst()) {
                    do {
                        categoryArrayList.add(catFromCursor(cursor));
                    }
                    while(cursor.moveToNext());

                    cursor.close();
                }

                return categoryArrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<Category> categoryArrayList) {

                if (categoryArrayList.isEmpty()) {
                    // If the category list is empty, do the following if the places have categories in them

                    for (POI poi : DataSource.this.poiArrayList) {
                        DataSource.this.categoryArrayList.add(new Category(poi.getRowId(), poi.getCategoryName(), poi.getCategoryColor()));
                    }

                    return;
                }

                DataSource.this.categoryArrayList = categoryArrayList;
            }

        }.execute();

    }

    public void filterByCategory(final Cursor cursor, String... strings) {
        // Select * from poi_table where category = "blah1" or category="pigeons" or ...;
    }

    // Pulling table row methods

    static POI poiFromCursor(final Cursor cursor) {

//        Log.v(TAG, "poiFromCursor() called");

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

    static Category catFromCursor(final Cursor cursor) {

//        Log.v(TAG, "catFromCursor() called");

        return new Category(CategoryTable.getRowId(cursor), CategoryTable.getCategoryName(cursor),
                CategoryTable.getCategoryColor(cursor));
    }

}
