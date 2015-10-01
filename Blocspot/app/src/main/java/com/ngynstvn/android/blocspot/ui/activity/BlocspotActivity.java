package com.ngynstvn.android.blocspot.ui.activity;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.yelp.YelpAPI;
import com.ngynstvn.android.blocspot.ui.Utils;
import com.ngynstvn.android.blocspot.ui.adapter.PlaceAdapter;
import com.ngynstvn.android.blocspot.ui.fragment.AssignCategoryDialog;
import com.ngynstvn.android.blocspot.ui.fragment.CatDialogFragment;
import com.ngynstvn.android.blocspot.ui.fragment.EditNoteDialog;
import com.ngynstvn.android.blocspot.ui.fragment.ListFragment;
import com.ngynstvn.android.blocspot.ui.fragment.MapsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BlocspotActivity extends AppCompatActivity implements
        ListFragment.ListFragDelegate, MapsFragment.MapFragDelegate, CatDialogFragment.CatDialogFragDelegate {

    // ------ Class variables ----- //

    private static final String TAG = "Test (" + BlocspotActivity.class.getSimpleName() + ")";
    private static final String FTS_TABLE = "yelp_search_table";
    private static final String POI_TABLE = "poi_table";
    private static final String FILTER_POI_TABLE = "filter_poi_table";
    private static int backpress_counter = 0;

    private static DataSource dataSource = BlocspotApplication.getSharedDataSource();
    private static SQLiteDatabase database = BlocspotApplication.getSharedDataSource()
            .getDatabaseOpenHelper().getWritableDatabase();

    // ----- Member variables ----- //

        // Toolbar Variables

    private Toolbar toolbar;
    private Menu menu;
    private MenuItem item;
    private MenuItem modeItem;

        // Fragment variables

    private MapsFragment mapsFragment;
    private ListFragment listFragment;
    private CatDialogFragment catDialogFragment;

        // SearchView variables

    private SearchManager searchManager;
    private SearchView searchView;
    private YelpAPI yelpAPI;

        // Map variables

    private double latitude = 34.05;
    private double longitude = -118.25;
    private float zoom = 15.0f;

    // ----- LIFECYCLE METHODS ----- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//      Debug.startMethodTracing("BlocspotActivity");
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocspot);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_blocspot);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_location_on_white_24dp);

        listFragment = new ListFragment();
        listFragment.setListFragDelegate(this);

        // ---- Display map fragment ---- //

        mapsFragment = new MapsFragment();
        mapsFragment.setMapFragDelegate(this);

        getFragmentManager().beginTransaction()
                .replace(R.id.fl_activity_blocspot, mapsFragment, Utils.MAPS_FRAGMENT).commit();
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart() called");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.e(TAG, "onRestart() called");
        // Only called whenever app comes back from a stop
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();
//        Debug.stopMethodTracing();
    }

    // --------------------------------------- //

    // ----- Toolbar Methods ----- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu() called");
        getMenuInflater().inflate(R.menu.menu_items, menu);
        this.menu = menu;

        MenuItem menuItem = menu.getItem(1);
        modeItem = menuItem;

        // This is only compatible with honeycomb or higher

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    searchView.onActionViewCollapsed();
                    mapsFragment.removeCurSrchMarkers();
                    database.execSQL("Delete from " + FTS_TABLE + ";");
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e(TAG, "onConfigurationChanged() called");
        super.onConfigurationChanged(newConfig);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.e(TAG, "onOptionsItemSelected() called");

        MenuItem menuItem = menu.getItem(1);

        switch (item.getItemId()) {

            case R.id.action_list_mode:

                if (item.getTitle().equals(getResources().getString(R.string.list_mode_text))) {
                    this.item = item;

                    if(listFragment.isFragmentUIActive()) {
                        item.setTitle(getResources().getString(R.string.map_mode_text));
                        item.setIcon(R.drawable.menu_map_mode_selector);
                    }

                    getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot,
                            new ListFragment(), Utils.LIST_FRAGMENT).commit();

                    searchView.onActionViewCollapsed();
                    searchView.setQuery("", false);

                    item = menuItem;
                    item.setEnabled(false);
                    item.setVisible(false);

                    Log.v(TAG, "BlocspotActivity List Mode Pressed");
                } else if (item.getTitle().equals(getResources().getString(R.string.map_mode_text))) {
                    this.item = item;

                    mapsFragment.removeAllPOIMarkers();
                    mapsFragment.addFilteredPOIMarkers();

                    getFragmentManager().beginTransaction()
                            .replace(R.id.fl_activity_blocspot, MapsFragment.newInstance(latitude,
                                    longitude, zoom), Utils.MAPS_FRAGMENT).commit();

                    searchView.onActionViewCollapsed();
                    searchView.setQuery("", false);

                    if(mapsFragment.isFragmentUIActive()) {
                        item.setTitle(getResources().getString(R.string.list_mode_text));
                        item.setIcon(R.drawable.menu_list_mode_selector);
                    }

                    item = menuItem;
                    item.setEnabled(true);
                    item.setVisible(true);

                    Log.v(TAG, "BlocspotActivity Map Mode Pressed");
                }

                return true;

            case R.id.action_search:
                Log.v(TAG, "BlocspotActivity " + item.getTitle() + " Icon Pressed");
                return false;

            case R.id.action_filter:
                Log.v(TAG, item.getTitle() + " Icon Pressed");
                this.item = item;
                showCategoryDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     *
     * SearchView Methods
     *
     */

    @Override
    public void onBackPressed() {
        backpress_counter++;

        Log.v(TAG, "onBackPressed() called");
        searchView.setQuery("", true);
        mapsFragment.removeCurSrchMarkers();
        searchView.onActionViewCollapsed();
        database.execSQL("Delete from " + FTS_TABLE + ";");

        if(backpress_counter > 2) {
            finish();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.v(TAG, "onNewIntent() called");
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            runSearch(query, Utils.latlngToZipCode(TAG, MapsFragment.getLatitude(), MapsFragment.getLongitude()));
        }
    }

    private void runSearch(final String term, final String location) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                database.execSQL("Delete from " + FTS_TABLE);
                yelpAPI = YelpAPI.newInstance();
            }

            @Override
            protected String doInBackground(Void... params) {
                return yelpAPI.searchForBusinessesByLocation(term, location);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.v(TAG, "Raw JSON data: " + s);
                getResults(s);
            }

        }.execute();

    }

    private void getResults(final String jsonString) {
        // Parse the JSON string here

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {

                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("businesses");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        String location_name = jsonArray.optJSONObject(i).getString("name");
                        String address = jsonArray.optJSONObject(i).optJSONObject("location")
                                .getJSONArray("address").getString(0);
                        String city = jsonArray.optJSONObject(i).optJSONObject("location").getString("city");
                        String state = jsonArray.optJSONObject(i).optJSONObject("location")
                                .getString("state_code");
                        double latitude = jsonArray.optJSONObject(i).optJSONObject("location")
                                .optJSONObject("coordinate").getDouble("latitude");
                        double longitude = jsonArray.optJSONObject(i).optJSONObject("location")
                                .optJSONObject("coordinate").getDouble("longitude");
                        String placeURL = jsonArray.optJSONObject(i).getString("mobile_url");
                        String ratingURL = jsonArray.optJSONObject(i).getString("rating_img_url_large");
                        String logoURL = jsonArray.optJSONObject(i).getString("image_url");

                        // Add result to the fts virtual table

                        dataSource.addSearchResult(new POI(0, location_name, "", 0, address, city,
                                state, latitude, longitude, "", placeURL, ratingURL, logoURL, false));
                    }

                    Log.v(TAG, "Current # of business JSON objects = " + jsonArray.length());
                    return jsonArray.length();
                }
                catch (JSONException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Integer size) {

                try {
                    if(size == 0) {
                        Toast.makeText(BlocspotApplication.getSharedInstance(), "Unable to find places. "
                                + "Search again.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (NullPointerException e) {
                    Log.v(TAG, "Null integer encountered during JSON parsing.");
                }

                mapsFragment.removeCurSrchMarkers();
                mapsFragment.addNewResultMarkers();
            }

        }.execute();
    }

    /**
     *
     *  ListFragment.ListFragDelegate Implemented Methods
     *
     */

    @Override
    public void onListItemClicked(ListFragment listFragment, POI poi) {

        Log.v(TAG, "onListItemClicked(...) working");

        if (mapsFragment != null) {

            item.setIcon(R.drawable.menu_list_mode_selector);

            // Change the title back to list mode so it goes back to ListFragment
            item.setTitle(getResources().getString(R.string.list_mode_text));

            modeItem.setEnabled(true);
            modeItem.setVisible(true);
            
            getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot, mapsFragment, Utils.MAPS_FRAGMENT).commit();
            mapsFragment.goToPOI(poi);

        } else {
            Log.v(TAG, " Problem: MapsFragment object is null");
        }

    }

    /**
     *
     *  MapsFragment.MapFragDelegate Implemented Methods
     *
     */

    // ----- Separate Methods ----- //

    void showCategoryDialog() {
        DialogFragment newFragment = CatDialogFragment.newInstance(R.string.alert_dialog_two_button_title);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onCoordinatesSaved(MapsFragment mapsFragment, double latitude, double longitude
            , float zoom) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
    }

    /**
     *
     * CatDialogFragment.CatDialogFragmentDelegate Implemented Methods
     *
     */

    @Override
    public void onFilterButtonClicked(CatDialogFragment catDialogFragment) {
        Log.v(TAG, "onFilterButtonClicked() called");
        ArrayList<String> categories = new ArrayList<>();

        for(String category : Utils.newSPrefInstance(Utils.FILTER_LIST).getAll().keySet()) {
            if(Utils.newSPrefInstance(Utils.FILTER_LIST).getBoolean(category, false)) {
                categories.add(category);
            }
            else if(!Utils.newSPrefInstance(Utils.FILTER_LIST).getBoolean(category, false)) {
                categories.remove(category);
            }
        }

        MapsFragment test1 = (MapsFragment) getFragmentManager().findFragmentByTag(Utils.MAPS_FRAGMENT);
        ListFragment test2 = (ListFragment) getFragmentManager().findFragmentByTag(Utils.LIST_FRAGMENT);

        if(test1 != null && test1.isVisible()) {

            BlocspotApplication.getSharedDataSource().filterFromDB(POI_TABLE, categories);

            modeItem.setEnabled(true);
            modeItem.setVisible(true);

            mapsFragment.removeAllPOIMarkers();
            mapsFragment.addFilteredPOIMarkers();

            getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot,
                    MapsFragment.newInstance(), Utils.MAPS_FRAGMENT).commit();

            return;
        }
        else if(test2 != null && test2.isVisible()) {

            BlocspotApplication.getSharedDataSource().filterFromDB(POI_TABLE, categories);

            getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot,
                    ListFragment.newInstance(), Utils.LIST_FRAGMENT).commit();
            return;
        }

        categories.clear();
    }
}
