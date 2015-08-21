package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.api.model.POI;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MapsFragment extends MapFragment {

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

    // ----- Member variables ----- //

        // DataSource's ArrayList access
    private ArrayList<POI> dSpoiArrayList = BlocspotApplication.getSharedDataSource().getPoiArrayList();

    private GoogleMap googleMap;
    private LatLng position;
    private float zoom;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView() called");
        setUpMapAtStartup();
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
    }

    // ----------------------------------------- //

    // Set up the map

    private void setUpMapAtStartup() {
        Log.v(TAG, "setUpMapAtStartup() called");

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

                        position = new LatLng(34.05, -118.25);
                        zoom = 14;

                        MapsFragment.this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));

                        MapsFragment.this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                        for (int i = 0; i < dSpoiArrayList.size(); i++) {

                            MapsFragment.this.googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(dSpoiArrayList.get(i).getLatitudeValue(),
                                                    dSpoiArrayList.get(i).getLongitudeValue()))
                                            .title(dSpoiArrayList.get(i).getLocationName())
                                            .snippet(dSpoiArrayList.get(i).getAddress())
                            );
                        }
                    }
                });
            }
        });
    }

    // Go to certain location on the map

    public void goToPOI(final POI poi) {

        Log.v(TAG, "goToPOI() called");

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
                        zoom = 18;

                        MapsFragment.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
                        MapsFragment.this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                });
            }
        }, 100);
    }

}
