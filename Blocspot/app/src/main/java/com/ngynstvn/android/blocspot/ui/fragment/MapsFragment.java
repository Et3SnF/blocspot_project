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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.intent.GeofenceTransitionsIntentService;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.model.database.table.FilterPOITable;
import com.ngynstvn.android.blocspot.api.model.database.table.POITable;
import com.ngynstvn.android.blocspot.ui.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends MapFragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    // Interface for future delegation

    public interface MapFragDelegate {
        void onCoordinatesSaved(MapsFragment mapsFragment, double latitude, double longitude, float zoom);
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

    private static final String POI_TABLE = "poi_table";
    private static final String FTS_TABLE = "yelp_search_table";
    private static final String FILTER_POI_TABLE = "filter_poi_table";

    private static CameraPosition cameraPosition;
    private static int counter = 0;

    // Defaulted to center of Los Angeles, CA
    private static double latitude = 34.05;
    private static double longitude = -118.25;
    private static float zoom = 15.00f;

    // ----- Member variables ----- //

    private GoogleMap googleMap;
    private LatLng position;
    private Map<String, POI> markerMap = new HashMap<>();

    // GoogleApiClient variables

    private GoogleApiClient googleApiClient;
    private Location location;

    // Geofence Variables

    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;

    // Notification Variables

    private NotificationManager notificationManager;
    private int notificationId;
    private POI poi;

    // ----- Fragment New Instance Method ------ //

    public static MapsFragment newInstance() {
        MapsFragment mapsFragment = new MapsFragment();
        return mapsFragment;
    }

    public static MapsFragment newInstance(double latitude, double longitude, float zoom) {

        MapsFragment mapsFragment = new MapsFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        bundle.putFloat("zoom", zoom);

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

        savedInstanceState = getArguments();

        if(savedInstanceState != null) {
            latitude = savedInstanceState.getDouble("latitude");
            longitude = savedInstanceState.getDouble("longitude");
            zoom = savedInstanceState.getFloat("zoom");

            Log.v(TAG, "Restored position: (" + latitude + "," + longitude + "), zoom = " + zoom);
        }
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

        // Only used for orientation change!

        outState.putDouble("latitude", latitude);
        outState.putDouble("longitude", longitude);
        outState.putFloat("zoom", zoom);
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
        googleMap = getMap();
        cameraPosition = googleMap.getCameraPosition();

        if(getMapFragDelegate() != null) {
            LatLng currentPosition = cameraPosition.target;
            double latitude = currentPosition.latitude;
            double longitude = currentPosition.longitude;
            float zoom = cameraPosition.zoom;

            Log.v(TAG, "Saved camera position: (" + latitude + "," + longitude + "), zoom = " + zoom);

            getMapFragDelegate().onCoordinatesSaved(this, latitude, longitude, zoom);
        }
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

    public void loadMap(final double latitude, final double longitude) {
        Log.v(TAG, "loadMap() called");

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

                        // First time, animate the camera. Otherwise just show the position w/o animation

                        if (counter == 1) {
                            MapsFragment.this.googleMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(position, zoom), 1700, null);
                        } else {
                            MapsFragment.this.googleMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(position, zoom));
                        }

                        MapsFragment.this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    }

                });

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.newSPrefInstance(Utils.FILTER_LIST).getAll().size() == 0) {
                            addPOIMarkers();
                            Log.v(TAG, "Total Active Geofences: " + geofenceList.size() + "");
                            activateGeofences();
                        } else {
                            addFilteredPOIMarkers();
                            Log.v(TAG, "Total Active Geofences: " + geofenceList.size() + "");
                            activateGeofences();
                        }
                    }
                });

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                            @Override
                            public void onCameraChange(CameraPosition cameraPosition) {
                                MapsFragment.latitude = cameraPosition.target.latitude;
                                MapsFragment.longitude = cameraPosition.target.longitude;
                                MapsFragment.zoom = cameraPosition.zoom;

                                Log.v(TAG, "Current position: (" + MapsFragment.latitude
                                        + ", " + MapsFragment.longitude + ") | zoom =" + MapsFragment.zoom);
                            }
                        });
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

                        MapsFragment.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoom), 1500, null);
                    }
                });

                addPOIMarkers();
            }

        }, 100);

        activateGeofences();
    }

    // Add Markers to Map

    public void addPOIMarkers() {

        Log.v(TAG, "addPOIMarkers() called");

        Cursor cursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, POI_TABLE, null, null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                poi = DataSource.poiFromCursor(cursor);

                Marker marker;

                if (POITable.getColumnHasVisited(cursor) == 0) {
                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(poi.getLatitudeValue(), poi.getLongitudeValue()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    addGeofence(poi);
                    Log.v(TAG, "Geofence activated for: " + poi.getLocationName());
                }
                else {
                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(poi.getLatitudeValue(), poi.getLongitudeValue()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    geofenceList.remove(poi);
                    Log.v(TAG, "Geofence deactivated for: " + poi.getLocationName());
                }

                markerMap.put(marker.getId(), poi);

//                UIUtils.displayPOIInfo(TAG, poi);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        getMarkerDialog(marker);
                        return true;
                    }
                });

            }
            while(cursor.moveToNext());
        }

        cursor.close();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG, "Total Active Geofences: " + geofenceList.size() + "");
                activateGeofences();
            }
        });

    }

    public void addFilteredPOIMarkers() {
        Log.v(TAG, "addFilteredPOIMarkers() called");
        removeAllPOIMarkers();
        getFilteredMarkers();
    }

    public void getFilteredMarkers() {
        Log.v(TAG, "getFilteredMarkers() called");

        Cursor newCursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, FILTER_POI_TABLE, null, null, null, null, null, null, null);

        if(newCursor.moveToFirst()) {
            do {
                poi = DataSource.poiFromCursor(newCursor);
                Marker marker;

                if (FilterPOITable.getColumnHasVisited(newCursor) == 0) {
                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(poi.getLatitudeValue(), poi.getLongitudeValue()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    addGeofence(poi);
                    Log.v(TAG, "Geofence activated for: " + poi.getLocationName());
                }
                else {
                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(poi.getLatitudeValue(), poi.getLongitudeValue()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    geofenceList.remove(poi);
                    Log.v(TAG, "Geofence deactivated for: " + poi.getLocationName());
                }

                markerMap.put(marker.getId(), poi);

//                UIUtils.displayPOIInfo(TAG, poi);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        getMarkerDialog(marker);
                        return true;
                    }
                });
            }
            while(newCursor.moveToNext());
        }

        newCursor.close();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG, "Total Active Geofences: " + geofenceList.size() + "");
                activateGeofences();
            }
        });
    }

    public void addNewResultMarkers() {

        Cursor cursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, FTS_TABLE, null, null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                poi = DataSource.poiFromCursor(cursor);
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(poi.getLatitudeValue(), poi.getLongitudeValue()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                markerMap.put(marker.getId(), poi);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        getMarkerDialog(marker);
                        return true;
                    }
                });
            }
            while(cursor.moveToNext());
        }

        cursor.close();
    }

    private void removeAllSearchMarkers() {

        Cursor cursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, FTS_TABLE, null, null, null, null, null, null, null);

        // refresh cursor for filtering

        if(cursor.moveToFirst()) {
            do {
                googleMap.clear();
            }
            while(cursor.moveToNext());
        }

        removeAllGeofences();
        cursor.close();
    }

    public void removeAllPOIMarkers() {
        Log.v(TAG, "removeAllPOIMarkers() called");
        Cursor cursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, POI_TABLE, null, null, null, null, null, null, null);

        // refresh cursor for filtering

        if(cursor.moveToFirst()) {
            do {
                googleMap.clear();
            }
            while(cursor.moveToNext());
        }

        removeAllGeofences();
        cursor.close();
    }

    public void removeAllFilteredMarkers() {
        Log.v(TAG, "removeAllFilteredMarkers() called");
        Cursor cursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, FILTER_POI_TABLE, null, null, null, null, null, null, null);

        // refresh cursor for filtering

        if(cursor.moveToFirst()) {
            do {
                googleMap.clear();
            }
            while(cursor.moveToNext());
        }

        removeAllGeofences();
        cursor.close();
    }

    public void removeCurSrchMarkers() {
        removeAllSearchMarkers();
        addPOIMarkers();
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

        Intent intent = new Intent(BlocspotApplication.getSharedInstance(),
                GeofenceTransitionsIntentService.class);

        return PendingIntent.getService(BlocspotApplication.getSharedInstance(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    // Method to add a geofence to a location

    private void addGeofence(POI poi) {

//        Log.v(TAG, "addGeofence() called");

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
    }

    // Method to remove geofence

    private void removeAllGeofences() {
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, getGeofencePendingIntent())
                .setResultCallback(this);
    }

    private void getMarkerDialog(Marker marker) {
        for (String id : markerMap.keySet().toArray(new String[markerMap.size()])) {
            if (marker.getId().equalsIgnoreCase(id)) {
                Log.v(TAG, "Markers are equal");
                showMarkerDialog(markerMap.get(id));
            }
        }
    }

    private void showMarkerDialog(POI poi) {
        Log.v(TAG, "showMarkerDialog() called");
        MarkerDialog markerDialog = MarkerDialog.newInstance(poi);
        markerDialog.show(getFragmentManager(), "marker_dialog");
    }

    // Map related methods


    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static float getZoom() {
        return zoom;
    }

    /**
     *
     * GoogleApiClient.ConnectionCallbacks Implemented Methods
     *
     */

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "onConnected() called");

        counter++;

        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if(counter <= 1) {

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.v(TAG, "Your current location: (" + latitude + "," + longitude + ")");
                loadMap(latitude, longitude);
            }
            else {
                loadMap(latitude, longitude);
            }
        }
        else if(counter > 1) {
            loadMap(latitude, longitude);
            Log.v(TAG, "Your current location: (" + latitude + "," + longitude + ")");
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

