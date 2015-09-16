package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.intent.GeofenceTransitionsIntentService;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.model.database.table.POITable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends MapFragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    // Interface for future delegation

    public static interface MapFragDelegate {

    }

    // ----- Setter and getter for ListFragDelegate -----//

    private WeakReference<MapFragDelegate> mapFragDelegate;

    public void setMapFragDelegate(MapFragDelegate mapFragDelegate) {
        this.mapFragDelegate = new WeakReference<MapFragDelegate>(mapFragDelegate);
    }

    public MapFragDelegate getMapFragDelegate() {

        if(mapFragDelegate == null) {
            return null;
        }

        return mapFragDelegate.get();
    }

    // ----- Class variables ----- //

    private static final String TAG = "Test (" + MapsFragment.class.getSimpleName() + ")";
    private static final String BUNDLE_MAP_MODE = MapsFragment.class.getCanonicalName().concat(".MAP_MODE");

    private static final String POI_TABLE = "poi_table";
    private static final String FTS_TABLE = "yelp_search_table";

    // ----- Member variables ----- //

    private GoogleMap googleMap;
    private LatLng position;
    private float zoom;

    // GoogleApiClient variables

    private GoogleApiClient googleApiClient;
    private Location location;

    // Defaulted to center of Los Angeles

    private double latitude = 34.05;
    private double longitude = -118.25;

    // Geofence Variables

    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;

    // Notification Variables

    private NotificationManager notificationManager;
    private int notificationId;

    // Critical method for saving instance state. For now null.

    public static MapsFragment returnMapMarkers() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_MAP_MODE, null);
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.setArguments(bundle);
        return mapsFragment;
    }

    // ---------- Lifecycle methods ----------- //

    @Override
    public void onAttach(Activity activity) {
        Log.e(TAG, "onAttach() called");
        super.onAttach(activity);
        buildGoogleApiClient();
        mapFragDelegate = new WeakReference<MapFragDelegate>((MapFragDelegate) activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        geofenceList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView() called");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated() called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState() called");
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG, "onDestroyView() called");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();
        removeAllGeofences();
        geofenceList.clear();
    }

    // ----------------------------------------- //

    // ------ Connecting to Google API client ----- //

    protected synchronized void buildGoogleApiClient() {
        Log.v(TAG, "buildGoogleApiClient() called");
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (googleApiClient != null) {
            Log.v(TAG, "googleApiClient is connected");
            googleApiClient.connect();
        }
    }

    // Set up the map

    private void startUpMap() {
        Log.v(TAG, "startUpMap() called");

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapsFragment.this.googleMap = googleMap;
                        MapsFragment.this.googleMap.setMyLocationEnabled(true);
                        MapsFragment.this.googleMap.getUiSettings().isCompassEnabled();
                        MapsFragment.this.googleMap.getUiSettings().setZoomControlsEnabled(true);

                        // Goes to center of LA

                        position = new LatLng(latitude, longitude);
                        zoom = 16;

                        MapsFragment.this.googleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(position, zoom));

//                        MapsFragment.this.googleMap.animateCamera(CameraUpdateFactory
//                                .newLatLngZoom(position, zoom), 1500, null);

                        MapsFragment.this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    }

                });

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        addMarkers();
                        Log.v(TAG, "Number of active geofences: " + geofenceList.size() + "");
                        activateGeofences();
                    }
                });
            }
        });

    }

    // Go to certain location on the map

    public void goToPOI(final POI poi) {

        Log.v(TAG, "goToPOI() called");

        // In case parameter is tampered

        if (poi == null) {
            Toast.makeText(BlocspotApplication.getSharedInstance(), "Unable to go to desired point" +
                    " of interest", Toast.LENGTH_SHORT).show();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapsFragment.this.googleMap = googleMap;
                        MapsFragment.this.googleMap.setMyLocationEnabled(true);
                        MapsFragment.this.googleMap.getUiSettings().isCompassEnabled();
                        MapsFragment.this.googleMap.getUiSettings().setZoomControlsEnabled(true);

                        //Goes to center of LA

                        position = new LatLng(poi.getLatitudeValue(), poi.getLongitudeValue());
                        zoom = 15;

                        MapsFragment.this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
                    }
                });

                addMarkers();
            }

        }, 100);

        activateGeofences();
    }

    // Add Markers to Map

    private void addMarkers() {

        Cursor cursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, POI_TABLE, null, null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                POI poi = DataSource.poiFromCursor(cursor);
                MapsFragment.this.googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(poi.getLatitudeValue(), poi.getLongitudeValue()))
                        .title(poi.getLocationName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

                MapsFragment.this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        showResultDialog();
                        return true;
                    }
                });

                addGeofence(poi);

                if (POITable.getColumnHasVisited(cursor) == 0) {
                    geofenceList.remove(poi);
                }
            }
            while(cursor.moveToNext());
        }

        cursor.close();
    }

    public void addNewResultMarkers() {

        Cursor cursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, FTS_TABLE, null, null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                POI poi = DataSource.poiFromCursor(cursor);
                MapsFragment.this.googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(poi.getLatitudeValue(), poi.getLongitudeValue()))
                        .title(poi.getLocationName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .snippet(poi.getAddress() + " ("
                                + poi.getCity() + ","
                                + poi.getState() + ")"));

                MapsFragment.this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        showResultDialog();
                        return true;
                    }
                });
            }
            while(cursor.moveToNext());
        }

        cursor.close();
    }

    private void removeAllMarkers() {

        Cursor cursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, FTS_TABLE, null, null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                MapsFragment.this.googleMap.clear();
            }
            while(cursor.moveToNext());
        }

        removeAllGeofences();
        cursor.close();
    }

    public void removeCurSrchMarkers() {
        removeAllMarkers();
        addMarkers();
    }

    // Activate geofences

    private void activateGeofences() {
        Log.v(TAG, "activateGeofences() called");

        if(getGeofencingRequest() == null) {
            return;
        }

        LocationServices.GeofencingApi.addGeofences(googleApiClient, getGeofencingRequest(),
                getGeofencePendingIntent()).setResultCallback(this);

    }

    // Geofence Methods

    private GeofencingRequest getGeofencingRequest() {
        Log.v(TAG, "getGeofencingRequest() called");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);

        // If the list is empty, make this null

        if(geofenceList.isEmpty()) {
            return null;
        }

        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Log.v(TAG, "getGeofencePendingIntent() called");
        // Reuse the PendingIntent if we already have it.

        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(getActivity(), GeofenceTransitionsIntentService.class);

        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    // Method to add a geofence to a location

    private void addGeofence(POI poi) {

        Log.v(TAG, "addGeofence() called");

        if(poi == null) {
            return;
        }

        double poiLatitude = poi.getLatitudeValue();
        double poiLongitude = poi.getLongitudeValue();
        int geofenceRadius = 400; //defaulted to 1/4 mi for now. Will add feature later to adjust

        // Add geofence to the list defined earlier for request to work

        geofenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(poi.getRowId()))
                .setCircularRegion(poiLatitude, poiLongitude, geofenceRadius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(10000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .build());

        // Add the circular fence around each point of interest

        if(googleMap == null) {
            Log.v(TAG, "googleMap variable null. Unable to add geofence circle");
            return;
        }
    }

    // Method to remove geofence

    private void removeAllGeofences() {
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, getGeofencePendingIntent())
                .setResultCallback(this);
    }

    private void showResultDialog() {
        ResultDialog resultDialog = ResultDialog.newInstance(R.string.result_dialog);
        resultDialog.show(getFragmentManager(), "result_dialog");
    }

    /**
     *
     * GoogleApiClient.ConnectionCallbacks Implemented Methods
     *
     */

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "onConnected() called");

        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.v(TAG, "Your current location: (" + latitude + "," + longitude + ")");

            if(bundle == null) {
                Log.v(TAG, "Bundle is null. (Location is not null)");
                startUpMap();
            }

            // return to map at current location on screen

        }
        else {
            Log.v(TAG, "Location is null. Unable to get location");

            if(bundle == null) {
                Log.v(TAG, "Bundle is null. (Location is null)");
                startUpMap();
            }

            // return to map at current location on screen

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     *
     * GoogleApiClient.OnConnectionFailedListener Implemented Methods
     *
     */

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     *
     * ResultCallback Implemented Methods
     *
     */

    @Override
    public void onResult(Result result) {

    }

}

