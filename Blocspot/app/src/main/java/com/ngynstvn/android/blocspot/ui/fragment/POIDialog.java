package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.squareup.picasso.Picasso;

public class POIDialog extends DialogFragment {

    private static final String TAG = "Test (" + POIDialog.class.getSimpleName() + "): ";
    private static final String POI_TABLE = "poi_table";

    private SQLiteDatabase database = BlocspotApplication.getSharedDataSource()
            .getDatabaseOpenHelper().getWritableDatabase();
    private Cursor cursor;

    private Button cancelButton;
    private Button visitButton;
    private ImageView poiLogo;
    private ImageView poiRating;
    private TextView poiName;
    private TextView poiAddress;
    private TextView poiCity;
    private TextView poiState;
    private TextView poiCategory;

    private POI poi;

    public static POIDialog newInstance(POI poi) {

        POIDialog poiDialog = new POIDialog();

        // Supply num input as an argument --> for retrieving stuff later

        Bundle bundle = new Bundle();
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

        poiDialog.setArguments(bundle);

        return poiDialog;

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

        savedInstanceState = getArguments(); // the most important line

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

            Log.v(TAG, "row_id: " + row_id + " | " + location_name + " | " + category_name +
                    " | " + category_color + " | " + address + " | " + city + " | " + state
                    + " | " + latitude + " | " + longitude + " | " + description + " | "
                    + place_url + " | " + rating_url + " | " + logo_url + " | " + hasVisited);

        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.v(TAG, "onCreateDialog() called");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.MaterialAlertDialogStyle);

        View view = getActivity().getLayoutInflater().inflate(R.layout.poi_dialog_layout, null);

        // Buttons

        cancelButton = (Button) view.findViewById(R.id.btn_result_cancel);
        visitButton = (Button) view.findViewById(R.id.btn_result_visit_site);

        // Content Items

        poiLogo = (ImageView) view.findViewById(R.id.iv_poi_logo);
        poiRating = (ImageView) view.findViewById(R.id.iv_poi_rating);
        poiName = (TextView) view.findViewById(R.id.tv_poi_name);
        poiAddress = (TextView) view.findViewById(R.id.tv_poi_address);
        poiCity = (TextView) view.findViewById(R.id.tv_poi_city);
        poiState = (TextView) view.findViewById(R.id.tv_poi_state);
        poiCategory = (TextView) view.findViewById(R.id.tv_poi_category_name);

        setTextViews(poi);
        loadImages(poi);

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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(poi.getPlaceURL()));
                startActivity(intent);
            }
        });

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
        if(poiLogo != null) {
            Picasso.with(BlocspotApplication.getSharedInstance()).load(poi.getLogoURL()).into(poiLogo);
        }

        if(poiRating != null) {
            Picasso.with(BlocspotApplication.getSharedInstance()).load(poi.getRatingImgURL()).into(poiRating);
        }
    }

    private void setTextViews(POI poi) {
        poiName.setText(poi.getLocationName());
        poiAddress.setText(poi.getAddress());
        poiCity.setText(poi.getCity());
        poiState.setText(poi.getState());

        if(poi.getCategoryName().equals("")) {
            poiCategory.setText("N/A");
            poiCategory.setTextColor(Color.DKGRAY);
        }
        else {
            poiCategory.setText(poi.getCategoryName());
            poiCategory.setTextColor(poi.getCategoryColor());
        }
    }

}