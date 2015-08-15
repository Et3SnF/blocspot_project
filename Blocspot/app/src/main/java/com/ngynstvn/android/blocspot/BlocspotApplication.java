package com.ngynstvn.android.blocspot;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.maps.MapFragment;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.ui.fragment.MapsFragment;

public class BlocspotApplication extends Application {

    private static final String TAG = "Test";

    private static BlocspotApplication sharedInstance;
    private DataSource dataSource;
    private MapsFragment mapsFragment;

    public static BlocspotApplication getSharedInstance() {
        return sharedInstance;
    }

    public static DataSource getSharedDataSource() {
        return BlocspotApplication.getSharedInstance().getDataSource();
    }

    public static MapFragment getSharedMapsFragment() {
        return BlocspotApplication.getSharedInstance().getMapsFragment();
    }

    public MapFragment getMapsFragment() {
        return mapsFragment;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, getClass().getSimpleName() + " - " + "onCreate called");
        super.onCreate();
        sharedInstance = this;
        dataSource = new DataSource(this);
        mapsFragment = new MapsFragment();
        // Test
    }
}
