package com.ngynstvn.android.blocspot.ui.activity;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocspot.api.model.database.table.POITable;
import com.ngynstvn.android.blocspot.ui.fragment.ListFragment;
import com.ngynstvn.android.blocspot.ui.fragment.MapsFragment;

public class BlocspotActivity extends AppCompatActivity implements ListFragment.ListFragDelegate {

    // Class variables

    private static final String TAG = "Test";

    // Member variables

    private Toolbar toolbar;
    private Menu menu;
    private MenuItem item;
    private DatabaseOpenHelper databaseOpenHelper;
    private POITable poiTable;
    private DataSource dataSource;
    private MapsFragment mapsFragment;

    private ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        Debug.startMethodTracing("BlocspotActivity");
        Log.e(TAG, "BlocspotActivity onCreate called");
        setContentView(R.layout.activity_blocspot);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_blocspot);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_location_on_white_24dp);

        // Displays an arrow icon
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        databaseOpenHelper = new DatabaseOpenHelper(this, poiTable);

        listFragment = new ListFragment();
        listFragment.setListFragDelegate(this);

        // ---- Display map fragment ---- //

        mapsFragment = new MapsFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.fl_activity_blocspot, mapsFragment).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "BlocspotActivity onCreateOptionsMenu called");
        getMenuInflater().inflate(R.menu.menu_items, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.e(TAG, "BlocspotActivity onPostCreate called");
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Debug.stopMethodTracing();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e(TAG, "BlocspotActivity onConfigurationChanged called");
        super.onConfigurationChanged(newConfig);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.e(TAG, "BlocspotActivity onOptionsItemSelected called");

        switch(item.getItemId()) {

            case R.id.action_list_mode:

                if(item.getTitle().equals(getResources().getString(R.string.list_mode_text))) {
                    this.item = item;
                    item.setTitle(getResources().getString(R.string.map_mode_text));
                    item.setIcon(R.drawable.menu_map_mode_selector);

                    getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot,
                            new ListFragment()).commit();

                    Log.v(TAG, "BlocspotActivity List Mode Pressed");
                }
                else if(item.getTitle().equals(getResources().getString(R.string.map_mode_text))) {
                    this.item = item;
                    item.setTitle(getResources().getString(R.string.list_mode_text));
                    item.setIcon(R.drawable.menu_list_mode_selector);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.fl_activity_blocspot, MapsFragment.returnMapMarkers()).commit();

                    Log.v(TAG, "BlocspotActivity Map Mode Pressed");
                }

                return false;

            case R.id.action_search:

                Toast.makeText(this, item.getTitle() + " Icon is pressed", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "BlocspotActivity " + item.getTitle() + " Icon Pressed");
                return true;

            case R.id.action_filter:

                Toast.makeText(this, item.getTitle() + " Icon is pressed", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "BlocspotActivity " + item.getTitle() + " Icon Pressed");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClicked(ListFragment listFragment, POI poi) {

        Log.v(TAG, BlocspotActivity.class.getSimpleName() + " onListItemClicked(...) working");

        if(mapsFragment != null) {

            item.setIcon(R.drawable.menu_list_mode_selector);

            // Change the title back to list mode so it goes back to ListFragment
            item.setTitle(getResources().getString(R.string.list_mode_text));

            getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot, mapsFragment).commit();
            mapsFragment.goToPOI(poi);

        }
        else {
            Log.v(TAG, BlocspotActivity.class.getSimpleName() + " Problem: MapsFragment object is null");
        }

    }
}
