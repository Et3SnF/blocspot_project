package com.ngynstvn.android.blocspot.ui.adapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.PlaceResult;
import com.squareup.picasso.Picasso;

public class PopupAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String FTS_TABLE = "yelp_search_table";
    private static PlaceResult placeResult;

    private View view = null;
    private LayoutInflater inflater = null;

    private TextView resultName;
    private TextView resultAddress;
    private TextView resultCity;
    private TextView resultState;
    private TextView resultZip;
    private ImageView resultLogo;
    private ImageView resultRating;

    PopupAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        if(view == null) {
            view = inflater.inflate(R.layout.result_item, null);
        }

        resultName = (TextView) view.findViewById(R.id.tv_search_result_name);
        resultAddress = (TextView) view.findViewById(R.id.tv_search_result_address);
        resultCity = (TextView) view.findViewById(R.id.tv_search_result_city);
        resultState = (TextView) view.findViewById(R.id.tv_search_result_state);
        resultLogo = (ImageView) view.findViewById(R.id.iv_place_logo);
        resultRating = (ImageView) view.findViewById(R.id.iv_search_result_rating);

        Cursor cursor = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().rawQuery("Select place_url, rating_url, logo_url from " +
                        FTS_TABLE + ";", null);

        placeResult = DataSource.placeResultFromCursor(cursor);
        loadImages(placeResult);
        setTextViews(placeResult);
        return view;
    }

    private void loadImages(PlaceResult placeResult) {
        if(resultLogo != null) {
            Picasso.with(BlocspotApplication.getSharedInstance()).load(placeResult.getLogoURL()).into(resultLogo);
        }

        if(resultRating != null) {
            Picasso.with(BlocspotApplication.getSharedInstance()).load(placeResult.getRatingImgURL()).into(resultRating);
        }
    }

    private void setTextViews(PlaceResult placeResult) {
        resultName.setText(placeResult.getLocationName());
        resultAddress.setText(placeResult.getAddress());
        resultCity.setText(placeResult.getCity());
        resultState.setText(placeResult.getState());
    }
}
