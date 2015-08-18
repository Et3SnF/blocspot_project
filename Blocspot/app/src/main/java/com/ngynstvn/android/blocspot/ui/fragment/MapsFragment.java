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
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.POI;

public class MapsFragment extends MapFragment {

    // Class variables

    private static final String TAG = "Test";
    private static final String BUNDLE_MAP_MODE = MapsFragment.class.getCanonicalName().concat(".MAP_MODE");

    // Member variables

    private GoogleMap googleMap;
    private boolean mapReady = false;
    private LatLng position;
    private float zoom;

    private DataSource dataSource = BlocspotApplication.getSharedDataSource();

    // Critical method for saving instance state. For now null.

    public static MapsFragment returnMapMarkers() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_MAP_MODE, null);
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.setArguments(bundle);
        return mapsFragment;
    }

    // Lifecycle methods

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, getClass().getSimpleName() + " onAttach called");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new DataSource(getActivity());
        Log.e(TAG, getClass().getSimpleName() + " onCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, getClass().getSimpleName() + " onCreateView called");
        setUpMapAtStartup();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, getClass().getSimpleName() + " onActivityCreated called");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.getParcelable(BUNDLE_MAP_MODE);
        Log.e(TAG, getClass().getSimpleName() + " onSaveInstanceState called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, getClass().getSimpleName() + " onPause called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, getClass().getSimpleName() + " onResume called");
    }

    // Set up the map

    private void setUpMapAtStartup() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapsFragment.this.googleMap = googleMap;
                        mapReady = true;

                        MapsFragment.this.googleMap.setMyLocationEnabled(true);
                        MapsFragment.this.googleMap.getUiSettings().isCompassEnabled();
                        MapsFragment.this.googleMap.getUiSettings().setZoomControlsEnabled(true);

                        //Goes to center of LA

                        position = new LatLng(34.05, -118.25);
                        zoom = 14;

                        MapsFragment.this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));

                        MapsFragment.this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                        for (int i = 0; i < dataSource.poiArrayList.size(); i++) {

                            MapsFragment.this.googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(dataSource.poiArrayList.get(i).getLatitudeValue(),
                                                    dataSource.poiArrayList.get(i).getLongitudeValue()))
                                            .title(dataSource.poiArrayList.get(i).getLocationName())
                                            .snippet(dataSource.poiArrayList.get(i).getAddress())
                            );
                        }
                    }
                });
            }
        });
    }

    // Go to certain location on the map

    public void goToPOI(final POI poi) {

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
