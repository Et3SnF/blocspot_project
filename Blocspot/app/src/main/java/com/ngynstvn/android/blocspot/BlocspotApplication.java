package com.ngynstvn.android.blocspot;

import android.app.Application;
import android.util.Log;

import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.ui.fragment.MapsFragment;

public class BlocspotApplication extends Application {

    private static final String TAG = "Test (" + BlocspotApplication.class.getSimpleName() + ")";

    private static BlocspotApplication sharedInstance;
    private DataSource dataSource;
    private MapsFragment mapsFragment;

    public static BlocspotApplication getSharedInstance() {
        return sharedInstance;
    }

    public static DataSource getSharedDataSource() {
        Log.v(TAG, "getSharedDataSource() called");
        return BlocspotApplication.getSharedInstance().getDataSource();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate() called");
        super.onCreate();
        sharedInstance = this;
        dataSource = new DataSource(this);
    }
}
