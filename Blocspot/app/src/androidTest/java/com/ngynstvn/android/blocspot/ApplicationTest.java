package com.ngynstvn.android.blocspot;

import android.test.ApplicationTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocspot.ui.fragment.MapsFragment;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<BlocspotApplication> {

    DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(getContext());

    public ApplicationTest() {
        super(BlocspotApplication.class);
    }

    private POI poi;
    private DataSource dataSource;
    private MapsFragment mapsFragment;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setContext(new RenamingDelegatingContext(getContext(), "test_"));
        createApplication();

        poi = new POI(1);
        dataSource = new DataSource(getContext());
        mapsFragment = new MapsFragment();
    }

    // ----- Testing Method Area ----- //

    public void testIfDatabaseIsOpened() {
        getApplication().onCreate();
        assertNotNull(databaseOpenHelper);
    }

    public void testIfSharedInstanceExists() {
        getApplication().onCreate();
        assertNotNull(BlocspotApplication.getSharedInstance());
    }

    public void testIfSharedDataSourceExists() {
        getApplication().onCreate();
        assertNotNull(BlocspotApplication.getSharedDataSource());
    }

    // ----- For Google Map Fragment ----- //

    public void testIfServicesExists() {

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplication());

        /*
        ConnectionResult.SUCCESS = 0
        ConnectionResult.SERVICE_MISSING = 1
        ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED = 18
         */

        if(isAvailable == ConnectionResult.SUCCESS) {
            assertTrue(true);
        }
        else {
            assertTrue(false);
        }
    }

    public void testIfLatLngValueIsGood() {

        // Input a random address to see if it converts to LatLng coordinates

        POI poi = new POI(1);
        poi.setAddress("1600 Amphitheatre Parkway");
        poi.setCity("Mountain View");
        poi.setState("CA");

        poi.setLatLngValue();

        if(String.valueOf(poi.getLongitudeValue()) == null || String.valueOf(poi.getLatitudeValue()) == null) {
            assertTrue(false);
        }
        else {
            Log.v(getClass().getSimpleName(), "(" + poi.getLatitudeValue() + ", " + poi.getLongitudeValue() + ")");
            assertTrue(true);
        }

    }

    public void testIfMapsFragmentNotNull() {
        assertNotNull(mapsFragment);
    }

}