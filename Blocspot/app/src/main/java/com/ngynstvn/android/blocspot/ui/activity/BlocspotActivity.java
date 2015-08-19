package com.ngynstvn.android.blocspot.ui.activity;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.intent.GeofenceTransitionsIntentService;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocspot.api.model.database.table.POITable;
import com.ngynstvn.android.blocspot.ui.fragment.ListFragment;
import com.ngynstvn.android.blocspot.ui.fragment.MapsFragment;

import java.util.ArrayList;

public class BlocspotActivity extends AppCompatActivity implements ListFragment.ListFragDelegate,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        MapsFragment.MapsFragmentDelegate, ResultCallback {

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

    private GoogleApiClient googleApiClient;

    private Geofence.Builder geoFenceBuilder;
    private ArrayList<Geofence> geofenceArrayList;
    private PendingIntent geofencePendingIntent;

    private double latitude = 0.00d;
    private double longitude = 0.00d;

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // ----- LIFECYCLE METHODS ----- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//      Debug.startMethodTracing("BlocspotActivity");
        Log.e(TAG, "BlocspotActivity onCreate called");
        setContentView(R.layout.activity_blocspot);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_blocspot);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_location_on_white_24dp);

        databaseOpenHelper = new DatabaseOpenHelper(this, poiTable);
        listFragment = new ListFragment();
        listFragment.setListFragDelegate(this);

        // ---- Display map fragment ---- //

        mapsFragment = new MapsFragment();
        mapsFragment.setMapsFragmentDelegate(this);
        geofenceArrayList = new ArrayList<>();

        getFragmentManager().beginTransaction()
                .add(R.id.fl_activity_blocspot, mapsFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Debug.stopMethodTracing();
    }

    // --------------------------------------- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "BlocspotActivity onCreateOptionsMenu called");
        getMenuInflater().inflate(R.menu.menu_items, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
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

        if (mapsFragment != null) {

            item.setIcon(R.drawable.menu_list_mode_selector);

            // Change the title back to list mode so it goes back to ListFragment
            item.setTitle(getResources().getString(R.string.list_mode_text));

            getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot, mapsFragment).commit();
            mapsFragment.goToPOI(poi);

        } else {
            Log.v(TAG, BlocspotActivity.class.getSimpleName() + " Problem: MapsFragment object is null");
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        final Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     *
     * MapsFragment.MapsFragmentDelegate Methods
     *
     */

    @Override
    public void onGeofenceAdded(MapsFragment mapsFragment) {
        LocationServices.GeofencingApi.addGeofences(googleApiClient, getGeofencingRequest(),
                getGeofencePendingIntent()).setResultCallback(this);
    }

    @Override
    public void googleApiClientConnected(MapsFragment mapsFragment) {
        googleApiClient.connect();
    }

    @Override
    public void googleApiClientDisconnected(MapsFragment mapsFragment) {

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceArrayList);
        return builder.build();
    }

    // PendingIntents are responsible for starting an IntentService

    private PendingIntent getGeofencePendingIntent() {

        if(geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onRemovingGeofences(MapsFragment mapsFragment) {
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, getGeofencePendingIntent())
                .setResultCallback(this);
    }

    @Override
    public double getLatitude(MapsFragment mapsFragment) {
        return this.latitude;
    }

    @Override
    public double getLongitude(MapsFragment mapsFragment) {
        return this.longitude;
    }

    @Override
    public void onResult(Result result) {

    }
}
