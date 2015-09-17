package com.ngynstvn.android.blocspot.ui;

import android.util.Log;

import com.ngynstvn.android.blocspot.api.model.POI;

import java.util.Random;

public class UIUtils {

    public static int generateRandomColor(int baseColor) {

        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        int baseRed = (baseColor & 0xFF0000) >> 16;
        int baseGreen = (baseColor & 0x00FF00) >> 8;
        int baseBlue = baseColor & 0xFF;

        red = (red + baseRed)/2;
        green = (green + baseGreen)/2;
        blue = (blue + baseBlue)/2;

        return 0xFF000000 | (red << 16) | (green << 8) | blue;

    }

    public static void displayPOIInfo(String TAG, POI poi) {

        // Display information about the POI

        Log.v(TAG, "row_id: " + poi.getRowId() + " | " + poi.getLocationName() + " | " + poi.getCategoryName() +
                " | " + poi.getCategoryColor() + " | " + poi.getAddress() + " | " + poi.getCity()
                + " | " + poi.getState() + " | " + poi.getLatitudeValue() + " | "
                + poi.getLongitudeValue() + " | " + poi.getDescription() + " | "
                + poi.getPlaceURL() + " | " + poi.getRatingImgURL() + " | " + poi.getLogoURL()
                + " | " + poi.isHasVisited());

        // Check blank category names

//        if(poi.getCategoryName() == null) {
//            Log.v(TAG, "Category name is null");
//        }
//        else if(poi.getCategoryName() != null){
//            Log.v(TAG, "Category name is not null. Length: " + poi.getCategoryName().length());
//        }

    }

}
