package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.ui.UIUtils;
import com.squareup.picasso.Picasso;

public class MarkerDialog extends DialogFragment {

    private static final String TAG = "Test (" + MarkerDialog.class.getSimpleName() + "): ";

    private Button favoriteButton;
    private TextView placeCategory;
    private Button cancelButton;
    private Button visitButton;
    private ImageView placeLogo;
    private ImageView placeRating;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placeCity;
    private TextView placeState;

    private POI poi;

    public static MarkerDialog newInstance(POI poi) {

        MarkerDialog markerDialog = new MarkerDialog();

        // Supply num input as an argument --> for retrieving stuff later

        Bundle bundle = new Bundle();

        bundle.putLong("row_id", poi.getRowId());
        bundle.putString("location_name", poi.getLocationName());
        bundle.putString("category_name", poi.getCategoryName());
        bundle.putInt("category_color", poi.getCategoryColor());
        bundle.putString("address", poi.getAddress());
        bundle.putString("city", poi.getCity());
        bundle.putString("state", poi.getState());
        bundle.putDouble("latitude", poi.getLatitudeValue());
        bundle.putDouble("longitude", poi.getLongitudeValue());
        bundle.putString("description", poi.getDescription());
        bundle.putString("place_url", poi.getPlaceURL());
        bundle.putString("rating_url", poi.getRatingImgURL());
        bundle.putString("logo_url", poi.getLogoURL());
        bundle.putBoolean("visited", poi.isHasVisited());

        markerDialog.setArguments(bundle);

        return markerDialog;

    }

    // ----- Lifecycle Methods ----- //

    @Override
    public void onAttach(Activity activity) {
        Log.v(TAG, "onAttach() called");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);

        savedInstanceState = getArguments();

        if(savedInstanceState != null) {

            long row_id = savedInstanceState.getLong("row_id");
            String location_name = savedInstanceState.getString("location_name");
            String category_name = savedInstanceState.getString("category_name");
            int category_color = savedInstanceState.getInt("category_color");
            String address = savedInstanceState.getString("address");
            String city = savedInstanceState.getString("city");
            String state = savedInstanceState.getString("state");
            double latitude = savedInstanceState.getDouble("latitude");
            double longitude = savedInstanceState.getDouble("longitude");
            String description = savedInstanceState.getString("description");
            String place_url = savedInstanceState.getString("place_url");
            String rating_url = savedInstanceState.getString("rating_url");
            String logo_url = savedInstanceState.getString("logo_url");
            boolean hasVisited = savedInstanceState.getBoolean("visited");

            poi = new POI(row_id, location_name, category_name, category_color, address, city, state,
                    latitude, longitude, description, place_url, rating_url, logo_url, hasVisited);

            UIUtils.displayPOIInfo(TAG, poi);

        }
        else {
            Log.v(TAG, "POI is null");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.v(TAG, "onCreateDialog() called");

        View view;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.MaterialAlertDialogStyle);

        if(poi.getRowId() == -1) {
            view = getActivity().getLayoutInflater().inflate(R.layout.result_dialog_layout, null);
            favoriteButton = (Button) view.findViewById(R.id.btn_result_favorite);

            cancelButton = (Button) view.findViewById(R.id.btn_result_cancel);
            visitButton = (Button) view.findViewById(R.id.btn_result_visit_site);
            placeLogo = (ImageView) view.findViewById(R.id.iv_search_result_logo);
            placeRating = (ImageView) view.findViewById(R.id.iv_search_result_rating);
            placeName = (TextView) view.findViewById(R.id.tv_search_result_name);
            placeAddress = (TextView) view.findViewById(R.id.tv_search_result_address);
            placeCity = (TextView) view.findViewById(R.id.tv_search_result_city);
            placeState = (TextView) view.findViewById(R.id.tv_search_result_state);
        }
        else {
            view = getActivity().getLayoutInflater().inflate(R.layout.poi_dialog_layout, null);
            placeCategory = (TextView) view.findViewById(R.id.tv_poi_category_name);

            cancelButton = (Button) view.findViewById(R.id.btn_poi_cancel);
            visitButton = (Button) view.findViewById(R.id.btn_poi_visit_site);
            placeLogo = (ImageView) view.findViewById(R.id.iv_poi_logo);
            placeRating = (ImageView) view.findViewById(R.id.iv_poi_rating);
            placeName = (TextView) view.findViewById(R.id.tv_poi_name);
            placeAddress = (TextView) view.findViewById(R.id.tv_poi_address);
            placeCity = (TextView) view.findViewById(R.id.tv_poi_city);
            placeState = (TextView) view.findViewById(R.id.tv_poi_state);
        }
        
        loadImages(poi);
        setTextViews(poi);

        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(TAG, "onActivityCreated() called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart() called");
        super.onStart();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        visitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(poi.getPlaceURL())));
            }
        });

        // For yelp results only

        if(poi.getRowId() == -1) {
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Favorite button has been clicked");

                    BlocspotApplication.getSharedDataSource().addNewPOI(poi);

                    Toast.makeText(BlocspotApplication.getSharedInstance(), poi.getLocationName() +
                            " has been added to your Points of Interest", Toast.LENGTH_SHORT).show();

                    dismiss();
                    Log.v(TAG, "Item has been inserted into the database");
                }
            });
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.v(TAG, "onCancel() called");
        // This is called only if I touched outside of the dialog to dismiss
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.v(TAG, "onDismiss() called");
        super.onDismiss(dialog);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.v(TAG, "onPause() called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.v(TAG, "onDestroyView() called");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Log.v(TAG, "onDetach() called");
        super.onDetach();
    }

    // ---------------- //

    private void loadImages(POI poi) {
        if(placeLogo != null) {
            Picasso.with(BlocspotApplication.getSharedInstance()).load(poi.getLogoURL()).into(placeLogo);
        }

        if(placeRating != null) {
            Picasso.with(BlocspotApplication.getSharedInstance()).load(poi.getRatingImgURL()).into(placeRating);
        }
    }

    private void setTextViews(POI poi) {

        placeName.setText(poi.getLocationName());
        placeAddress.setText(poi.getAddress());
        placeCity.setText(poi.getCity());
        placeState.setText(poi.getState());

        if(poi.getRowId() != -1) {
            if(poi.getCategoryName().length() == 0) {
                placeCategory.setText("N/A");
                placeCategory.setTextColor(Color.DKGRAY);
            }
            else {
                placeCategory.setText(poi.getCategoryName());
                placeCategory.setTextColor(poi.getCategoryColor());
            }
        }

    }

}
