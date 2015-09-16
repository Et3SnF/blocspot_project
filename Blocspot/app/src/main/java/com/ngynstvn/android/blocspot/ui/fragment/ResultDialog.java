package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ResultDialog extends DialogFragment {

    private static final String TAG = "Test (" + CatDialogFragment.class.getSimpleName() + "): ";
    private static final String FTS_TABLE = "yelp_search_table";

    private SQLiteDatabase database = BlocspotApplication.getSharedDataSource()
            .getDatabaseOpenHelper().getWritableDatabase();
    private Cursor cursor;

    private Button cancelButton;
    private Button favoriteButton;
    private Button visitButton;
    private ImageView resultLogo;
    private ImageView resultRating;
    private TextView resultName;
    private TextView resultAddress;
    private TextView resultCity;
    private TextView resultState;

    private POI poi;

    public static ResultDialog newInstance(POI poi) {

        ResultDialog resultDialog = new ResultDialog();

        // Supply num input as an argument --> for retrieving stuff later

        Bundle bundle = new Bundle();
        bundle.putSerializable("poi", poi);

        resultDialog.setArguments(bundle);

        return resultDialog;

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
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.v(TAG, "onCreateDialog() called");

        new AsyncTask<Void, Void, ArrayList<POI>>() {

            @Override
            protected void onPreExecute() {
                cursor = database.rawQuery("Select * from " + FTS_TABLE, null);
            }

            @Override
            protected ArrayList<POI> doInBackground(Void... params) {

                ArrayList<POI> poiArrayList = new ArrayList<>();

                if (cursor.moveToFirst()) {
                    do {
                        poi = DataSource.poiFromCursor(cursor);
                        poiArrayList.add(poi);
                        Log.v(TAG, "The place URL is: " + poi.getPlaceURL());
                    }
                    while (cursor.moveToNext());
                }

                return poiArrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<POI> poiArrayList) {
                for(POI poi : poiArrayList) {
                    loadImages(poi);
                    setTextViews(poi);
                }
            }

        }.execute();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.MaterialAlertDialogStyle);

        View view = getActivity().getLayoutInflater().inflate(R.layout.result_dialog_layout, null);

        // Buttons

        cancelButton = (Button) view.findViewById(R.id.btn_result_cancel);
        favoriteButton = (Button) view.findViewById(R.id.btn_result_favorite);
        visitButton = (Button) view.findViewById(R.id.btn_result_visit_site);

        // Content Items

        resultLogo = (ImageView) view.findViewById(R.id.iv_search_result_logo);
        resultRating = (ImageView) view.findViewById(R.id.iv_search_result_rating);
        resultName = (TextView) view.findViewById(R.id.tv_search_result_name);
        resultAddress = (TextView) view.findViewById(R.id.tv_search_result_address);
        resultCity = (TextView) view.findViewById(R.id.tv_search_result_city);
        resultState = (TextView) view.findViewById(R.id.tv_search_result_state);

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

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do the database operation here
            }
        });

        visitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(poi.getPlaceURL())));
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
        if(resultLogo != null) {
            Picasso.with(BlocspotApplication.getSharedInstance()).load(poi.getLogoURL()).into(resultLogo);
        }

        if(resultRating != null) {
            Picasso.with(BlocspotApplication.getSharedInstance()).load(poi.getRatingImgURL()).into(resultRating);
        }
    }

    private void setTextViews(POI poi) {
        resultName.setText(poi.getLocationName());
        resultAddress.setText(poi.getAddress());
        resultCity.setText(poi.getCity());
        resultState.setText(poi.getState());
    }

}
