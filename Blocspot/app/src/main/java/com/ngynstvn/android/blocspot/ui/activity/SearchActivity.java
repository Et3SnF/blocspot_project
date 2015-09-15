package com.ngynstvn.android.blocspot.ui.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.yelp.YelpAPI;
import com.ngynstvn.android.blocspot.ui.adapter.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends Activity {

    private static final String TAG = "Test (" + SearchActivity.class.getSimpleName() + ")";
    private static final String FTS_TABLE = "yelp_search_table";

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private SQLiteDatabase database = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper().getReadableDatabase();
    private Cursor cursor;

    private YelpAPI yelpAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate() called");
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();

        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            runSearch(query, "Los Angeles, CA");
        }
    }

    private void runSearch(final String term, final String location) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
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

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("businesses");

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

                        Log.v(TAG, "Place #" + (i+1) + ": " + location_name + " | " + address + " | " + city +
                                " | " + state + " | " + latitude + " | " + longitude);

                        // Add result to the fts virtual table

                        BlocspotApplication.getSharedDataSource().addSearchResult(new POI(0, location_name,
                                null, 0, address, city, state, latitude, longitude, null, false, 0.00f));
                    }

                    Log.v(TAG, "Current # of business JSON objects = " + jsonArray.length());
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.v(TAG, "onBackPressed() called");
        BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper().getWritableDatabase()
                .execSQL("Delete from " + FTS_TABLE + ";");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy() called");
        BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper().getWritableDatabase()
                .execSQL("Delete from " + FTS_TABLE + ";");
    }
}
