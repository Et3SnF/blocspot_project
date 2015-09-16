package com.ngynstvn.android.blocspot.ui.activity;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import com.ngynstvn.android.blocspot.api.model.PlaceResult;
import com.ngynstvn.android.blocspot.api.yelp.YelpAPI;
import com.ngynstvn.android.blocspot.ui.fragment.AssignCategoryDialog;
import com.ngynstvn.android.blocspot.ui.fragment.CatDialogFragment;
import com.ngynstvn.android.blocspot.ui.fragment.ListFragment;
import com.ngynstvn.android.blocspot.ui.fragment.MapsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BlocspotActivity extends AppCompatActivity implements
        ListFragment.ListFragDelegate, MapsFragment.MapFragDelegate, AssignCategoryDialog.PostTask {

    // ------ Class variables ----- //

    private static final String TAG = "Test (" + BlocspotActivity.class.getSimpleName() + ")";
    private static final String FTS_TABLE = "yelp_search_table";
    private static int backpress_counter = 0;

    private static DataSource dataSource = BlocspotApplication.getSharedDataSource();
    private static SQLiteDatabase database = BlocspotApplication.getSharedDataSource()
            .getDatabaseOpenHelper().getWritableDatabase();

    // ----- Member variables ----- //

        // Toolbar Variables

    private Toolbar toolbar;
    private Menu menu;
    private MenuItem item;

        // Fragment variables

    private MapsFragment mapsFragment;
    private ListFragment listFragment;

        // SearchView variables

    private SearchManager searchManager;
    private SearchView searchView;
    private YelpAPI yelpAPI;

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

        // ---- Attach CatDialogFragment ---- //

        getFragmentManager().beginTransaction()
                .add(R.id.fl_activity_blocspot, mapsFragment).commit();

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

        switch (item.getItemId()) {

            case R.id.action_list_mode:

                if (item.getTitle().equals(getResources().getString(R.string.list_mode_text))) {
                    this.item = item;
                    item.setTitle(getResources().getString(R.string.map_mode_text));
                    item.setIcon(R.drawable.menu_map_mode_selector);

                    getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot,
                            new ListFragment()).commit();

                    searchView.onActionViewCollapsed();
                    searchView.setQuery("", false);

                    Log.v(TAG, "BlocspotActivity List Mode Pressed");
                } else if (item.getTitle().equals(getResources().getString(R.string.map_mode_text))) {
                    this.item = item;
                    item.setTitle(getResources().getString(R.string.list_mode_text));
                    item.setIcon(R.drawable.menu_list_mode_selector);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.fl_activity_blocspot, MapsFragment.returnMapMarkers()).commit();

                    searchView.onActionViewCollapsed();
                    searchView.setQuery("", false);

                    Log.v(TAG, "BlocspotActivity Map Mode Pressed");
                }

                return true;

            case R.id.action_search:
                Log.v(TAG, "BlocspotActivity " + item.getTitle() + " Icon Pressed");
                return false;

            case R.id.action_filter:
                Log.v(TAG, item.getTitle() + " Icon Pressed");
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
            runSearch(query, "Los Angeles, CA");
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
                        String ratingURL = jsonArray.optJSONObject(i).getString("rating_img_url_small");;
                        String logoURL = jsonArray.optJSONObject(i).getString("snippet_image_url");

                        // To log the search results

//                        Log.v(TAG, "Place #" + (i+1) + ": " + location_name + " | " + address + " | "
//                                + city + " | " + state + " | " + latitude + " | " + longitude);

                        // Add result to the fts virtual table

                        dataSource.addSearchResult(new PlaceResult(0, location_name, address, city,
                                state, latitude, longitude, placeURL, ratingURL, logoURL));
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

            getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot, mapsFragment).commit();
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

    /**
     *
     *  AssignCategoryDialog.PostTask Implemented Methods
     *
     */

    @Override
    public void onComplete(int poi_position) {
        getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot,
                ListFragment.newInstance(poi_position)).commit();
    }
}
