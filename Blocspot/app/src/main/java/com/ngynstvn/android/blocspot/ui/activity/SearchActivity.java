package com.ngynstvn.android.blocspot.ui.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.ui.adapter.SearchAdapter;

public class SearchActivity extends Activity {

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private SQLiteDatabase database = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper().getReadableDatabase();
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();

        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    private void showResults(String query) {

        String[] split = query.split(" ");
        String queryMessage = null;

        if(split.length == 1) {
            queryMessage = "Select * from poi_table where location_name like '%" + split[0] +"%'";
        }
        else if(split.length == 2) {
            queryMessage = "Select * from poi_table where location_name like '%" + split[0] +"%' or " +
                    "location_name like '" + split[1] +"'";
        }
        else {
            queryMessage = "Select * from poi_table where location_name like '%" + split[0] +"%' or " +
                    "location_name like '%" + split[1] +"%' or location_name like '%" + split[2] + "%'";
        }

        cursor = database.rawQuery(queryMessage, null);
        searchAdapter = new SearchAdapter(BlocspotApplication.getSharedInstance(), cursor);
        recyclerView = (RecyclerView) findViewById(R.id.rv_activity_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(BlocspotApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(searchAdapter);
    }

}
