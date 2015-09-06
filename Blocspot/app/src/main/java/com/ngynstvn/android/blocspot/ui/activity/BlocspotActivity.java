package com.ngynstvn.android.blocspot.ui.activity;

import android.annotation.TargetApi;
import android.app.DialogFragment;
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
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.ui.fragment.CatDialogFragment;
import com.ngynstvn.android.blocspot.ui.fragment.ListFragment;
import com.ngynstvn.android.blocspot.ui.fragment.MapsFragment;

public class BlocspotActivity extends AppCompatActivity implements
        ListFragment.ListFragDelegate, MapsFragment.MapFragDelegate {

    // ------ Class variables ----- //

    private static final String TAG = "Test (" + BlocspotActivity.class.getSimpleName() + ")";

    // ----- Member variables ----- //

        // Toolbar Variables

    private Toolbar toolbar;
    private Menu menu;
    private MenuItem item;

        // Fragment variables

    private MapsFragment mapsFragment;
    private ListFragment listFragment;

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

        // Fetch item from data base

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
        return super.onCreateOptionsMenu(menu);
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

                    Log.v(TAG, "BlocspotActivity List Mode Pressed");
                } else if (item.getTitle().equals(getResources().getString(R.string.map_mode_text))) {
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

                Log.v(TAG, item.getTitle() + " Icon Pressed");
                showCategoryDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

}
