package com.ngynstvn.android.blocspot.api;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.api.model.Category;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocspot.api.model.database.fts_table.FTSTable;
import com.ngynstvn.android.blocspot.api.model.database.table.CategoryTable;
import com.ngynstvn.android.blocspot.api.model.database.table.POITable;
import com.ngynstvn.android.blocspot.ui.UIUtils;

import java.lang.ref.WeakReference;

public class DataSource {

    // For log messaging

    private static final String TAG = "Test (" + DataSource.class.getSimpleName() + ")";
    private static final String POI_TABLE = "poi_table";
    private static final String CATEGORY_TABLE = "category_table";
    private static final String FTS_TABLE = "yelp_search_table";
    private static int counter;

    // ---- ----- //

    // Member variables

    private final int BASE_COLOR = android.R.color.white;
    private final String DB_NAME = "blocspot_db";

    private DatabaseOpenHelper databaseOpenHelper;
    private POITable poi_table;
    private CategoryTable categoryTable;
    private FTSTable ftsTable;

//    private ArrayList<POI> poiArrayList = new ArrayList<>();
//    private ArrayList<Category> categoryArrayList = new ArrayList<>();
//    private Map<String, Integer> catNameColorMap = new HashMap<String, Integer>();

    // Constructor

    public DataSource(Context context) {

        Log.v(TAG, "DataSource instantiated");

        counter++;

        if(counter == 1) {
            context.deleteDatabase(DB_NAME);
            poi_table = new POITable();
            categoryTable = new CategoryTable();
            ftsTable = new FTSTable();

            databaseOpenHelper = new DatabaseOpenHelper(BlocspotApplication.getSharedInstance(),
                    poi_table, categoryTable, ftsTable);

            dbFakeData();
            dbFakeCategoryData();
        }

        Log.v(TAG, "Instantiation counter: " + counter);
    }

    // ----- Interface and Interface Delegation material ----- //

    public static interface PostTask {
        public void onFetchingComplete();
    }

    public static interface DataSourceDelegate {
        public void onQueryComplete(Cursor cursor);
    }

    private WeakReference<DataSourceDelegate> dataSourceDelegate;

    public void setDataSourceDelegate(DataSourceDelegate dataSourceDelegate) {
        this.dataSourceDelegate = new WeakReference<DataSourceDelegate>(dataSourceDelegate);
    }

    public DataSourceDelegate getDataSourceDelegate() {
        return dataSourceDelegate.get();
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
                .setDescription("")
//                .setDescription("A very well known mall in the city. Across from Americana.")
                .setPlaceURL("http://www.m.yelp.com/biz/glendale-galleria-glendale")
                .setRatingURL("http://s3-media2.fl.yelpcdn.com/assets/2/www/img/ccf2b76faa2c/ico/stars/v1/stars_large_4.png")
                .setLogoURL("http://s3-media3.fl.yelpcdn.com/bphoto/Bq5fsdWuvuy98v4S7SnCsQ/ms.jpg")
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
                .setPlaceURL("http://www.m.yelp.com/biz/boba-7-los-angeles-2")
                .setRatingURL("http://s3-media4.fl.yelpcdn.com/assets/2/www/img/9f83790ff7f6/ico/stars/v1/stars_large_4_half.png")
                .setLogoURL("http://s3-media3.fl.yelpcdn.com/bphoto/Bq5fsdWuvuy98v4S7SnCsQ/ms.jpg")
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
                .setPlaceURL("http://www.m.yelp.com/biz/walt-disney-concert-hall-los-angeles")
                .setRatingURL("http://s3-media2.fl.yelpcdn.com/assets/2/www/img/ccf2b76faa2c/ico/stars/v1/stars_large_4.png")
                .setLogoURL("http://s3-media3.fl.yelpcdn.com/bphoto/Bq5fsdWuvuy98v4S7SnCsQ/ms.jpg")
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
                .setPlaceURL("http://www.m.yelp.com/biz/dog-haus-burbank?osq=doghaus")
                .setRatingURL("http://s3-media2.fl.yelpcdn.com/assets/2/www/img/ccf2b76faa2c/ico/stars/v1/stars_large_4.png")
                .setLogoURL("http://s3-media3.fl.yelpcdn.com/bphoto/Bq5fsdWuvuy98v4S7SnCsQ/ms.jpg")
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
                .setPlaceURL("http://www.m.yelp.com/biz/urban-ramen-los-angeles-2")
                .setRatingURL("http://s3-media2.fl.yelpcdn.com/assets/2/www/img/ccf2b76faa2c/ico/stars/v1/stars_large_4.png")
                .setLogoURL("http://s3-media3.fl.yelpcdn.com/bphoto/Bq5fsdWuvuy98v4S7SnCsQ/ms.jpg")
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

    // Getting database opener

    public DatabaseOpenHelper getDatabaseOpenHelper() {
        return databaseOpenHelper;
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

        Log.v(TAG, "addNewPOI() called");

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
                .setPlaceURL(poi.getPlaceURL())
                .setRatingURL(poi.getRatingImgURL())
                .setLogoURL(poi.getLogoURL())
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

    public long addSearchResult(POI poi) {

//        Log.v(TAG, "addSearchResult() called");

        if(poi == null) {
            return -1L;
        }
        else if(poi.getLatitudeValue() == 0 && poi.getLongitudeValue() == 0) {
            return -1L;
        }

        int boolInt = 0;

        if (poi.isHasVisited()) {
            boolInt = 1;
        } else {
            boolInt = 0;
        }

        return new FTSTable.Builder()
                .setLocationName(poi.getLocationName())
                .setCategory(poi.getCategoryName())
                .setCategoryColor(poi.getCategoryColor())
                .setAddress(poi.getAddress())
                .setCity(poi.getCity())
                .setState(poi.getState())
                .setLatitude(poi.getLatitudeValue())
                .setLongitude(poi.getLongitudeValue())
                .setDescription(poi.getDescription())
                .setPlaceURL(poi.getPlaceURL())
                .setRatingURL(poi.getRatingImgURL())
                .setLogoURL(poi.getLogoURL())
                .setHasVisited(boolInt)
                .insert(databaseOpenHelper.getWritableDatabase());
    }

    public void fetchFilteredPOIs(final PostTask postTask, final String... categories) {

        Log.v(TAG, "fetchFilteredPOIs() called");

        // Perform AsyncTask at startup

        new AsyncTask<Void, Void, POI>() {

            @Override
            protected POI doInBackground(Void... params) {

                Log.v(TAG, "doInBackground() performing in " + Thread.currentThread().getName());

                Cursor cursor = POITable.getFilteredPOIs(databaseOpenHelper.getWritableDatabase(), categories);
                POI poi = null;

                if(cursor.moveToFirst()) {

                    do {
                        poi = poiFromCursor(cursor);
                    }
                    while(cursor.moveToNext());

                    cursor.close();

                }

                return poi;

            }

            @Override
            protected void onPostExecute(POI poi) {
                postTask.onFetchingComplete();
            }

        }.execute();

    }

    public void removePOIFromDB(final int item_position)  {

        Log.v(TAG, "removePOIFromDB() called");

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                getDatabaseOpenHelper().getWritableDatabase().delete(POI_TABLE, "_id = " + item_position, null);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                requeryDB(POI_TABLE);
            }

        }.execute();
    }

    public void removeCatFromDB(final int item_position)  {

        Log.v(TAG, "removePOIFromDB() called");

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                getDatabaseOpenHelper().getWritableDatabase().delete(CATEGORY_TABLE, "_id = " + item_position, null);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                requeryDB(CATEGORY_TABLE);
            }

        }.execute();
    }
    
    public void requeryDB(final String tableName) {

        Log.v(TAG, "reQueryDB() called");

        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(Void... params) {

                Cursor cursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                        .getReadableDatabase().query(true, tableName, null, null, null, null, null, null, null);

                return cursor;
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                getDataSourceDelegate().onQueryComplete(cursor);
            }

        }.execute();
    }

    public boolean checkIfItemIsInPOIdB(String dbName, int rowId, String dbField, String value) {

//        Log.v(TAG, "checkIfItemIsInPOIdB() called");

        SQLiteDatabase database = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper().getWritableDatabase();
        Cursor cursor = database.rawQuery("Select * from " + dbName + " where _id = "  + String.valueOf(rowId)
                + " and " + dbField + " like '" + value + "';", null);

        if(cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }

        return true;
    }

    public boolean checkIfItemIsInCatDB(String dbName, String dbField, String value) {

//        Log.v(TAG, "checkIfItemIsInCatDB() called");

        SQLiteDatabase database = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper().getWritableDatabase();
        Cursor cursor = database.rawQuery("Select * from " + dbName + " where " + dbField
                + " like '" + value + "';", null);

        if(cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }

        return true;
    }

    /*
     *
     *  Convenience methods to retrieve data from database
     *
     */

    // Important Method to retrieve data from database

    public static POI poiFromCursor(Cursor cursor) {

//        Log.v(TAG, "poiFromCursor() called");

        POI poi = new POI(POITable.getRowId(cursor));

        if(POITable.getColumnHasVisited(cursor) == 1) {
            poi.setHasVisited(true);
        }
        else if(POITable.getColumnHasVisited(cursor) == 0) {
            poi.setHasVisited(false);
        }

        return new POI(POITable.getRowId(cursor), POITable.getLocationName(cursor), POITable.getCategory(cursor),
                POITable.getCategoryColor(cursor), POITable.getAddress(cursor), POITable.getCity(cursor),
                POITable.getState(cursor), POITable.getLatitude(cursor), POITable.getLongitude(cursor),
                POITable.getColumnDescription(cursor), POITable.getPlaceURL(cursor), POITable.getRatingURL(cursor),
                POITable.getLogoURL(cursor), poi.isHasVisited());
    }

    public static Category catFromCursor(Cursor cursor) {

//        Log.v(TAG, "catFromCursor() called");

        Category category = new Category(CategoryTable.getRowId(cursor));

        if(POITable.getColumnHasVisited(cursor) == 1) {
            category.setIsCatChecked(true);
        }
        else if(POITable.getColumnHasVisited(cursor) == 0) {
            category.setIsCatChecked(false);
        }

        return new Category(CategoryTable.getRowId(cursor), CategoryTable.getCategoryName(cursor),
                CategoryTable.getCategoryColor(cursor), category.getIsCatChecked());
    }

}
