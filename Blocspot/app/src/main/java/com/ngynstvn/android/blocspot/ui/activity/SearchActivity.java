package com.ngynstvn.android.blocspot.ui.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.yelp.YelpAPI;
import com.ngynstvn.android.blocspot.ui.adapter.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends Activity {

    private static final String TAG = "Test (" + SearchActivity.class.getSimpleName() + ")";

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private SQLiteDatabase database = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper().getReadableDatabase();
    private Cursor cursor;

    private ArrayList<String> resultList = new ArrayList<>();

    private static final String CONSUMER_KEY = "Y0Myyxe8h_YEsHjfo-j6yg";
    private static final String CONSUMER_SECRET = "1qMgDlgy15dRMjh5ImZqjtEAXRg";
    private static final String TOKEN = "6gp-qC2WHzIw6DKpTu-725G73pXmIxXx";
    private static final String TOKEN_SECRET = "H7yDe6WlFTMvSYcEi-TIDE0CUZk";

    private YelpAPI yelpAPI;
    private String jsonResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void getResults(String jsonString) {

        parseJSON(jsonString);

        searchAdapter = new SearchAdapter(BlocspotApplication.getSharedInstance(), cursor);
        recyclerView = (RecyclerView) findViewById(R.id.rv_activity_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(BlocspotApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(searchAdapter);

    }

    private void parseJSON(String jsonString) {

        // Parse the JSON string here

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("businesses");

            for(int i = 0; i < jsonArray.length(); i++) {
                String location_name = jsonArray.optJSONObject(i).getString("name");
                String address = jsonArray.optJSONObject(i).optJSONObject("location").getJSONArray("address").getString(0);
                String city = jsonArray.optJSONObject(i).optJSONObject("location").getString("city");
                String state = jsonArray.optJSONObject(i).optJSONObject("location").getString("state_code");
                double latitude = jsonArray.optJSONObject(i).optJSONObject("location").optJSONObject("coordinate").getDouble("latitude");
                double longitude = jsonArray.optJSONObject(i).optJSONObject("location").optJSONObject("coordinate").getDouble("longitude");

                Log.v(TAG, "Place #" + i + ": " + location_name + " | " + address + " | " + city +
                        " | " + state + " | " + latitude + " | " + longitude);
            }

            Log.v(TAG, "Current # of business JSON objects = " + jsonArray.length());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
