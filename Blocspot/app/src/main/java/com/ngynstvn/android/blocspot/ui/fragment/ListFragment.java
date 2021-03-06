package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.ui.Utils;
import com.ngynstvn.android.blocspot.ui.adapter.PlaceAdapter;
import com.ngynstvn.android.blocspot.ui.helper.ItemTouchHelperCallback;

import java.lang.ref.WeakReference;

public class ListFragment extends Fragment implements PlaceAdapter.PlaceAdapterDelegate, DataSource.DataSourceDelegate {

    // Interface for future delegation

    public static interface ListFragDelegate {
        public void onListItemClicked(ListFragment listFragment, POI poi);
    }

    // ----- Setter and getter for ListFragDelegate -----//

    private WeakReference<ListFragDelegate> listFragDelegate;

    public void setListFragDelegate(ListFragDelegate listFragDelegate) {
        this.listFragDelegate = new WeakReference<ListFragDelegate>(listFragDelegate);
    }

    public ListFragDelegate getListFragDelegate() {

        if(listFragDelegate == null) {
            return null;
        }

        return listFragDelegate.get();
    }

    // newInstance() method for fragment

    public static ListFragment newInstance() {
        ListFragment listFragment = new ListFragment();
        Bundle bundle = new Bundle();
        return listFragment;
    }

    public static ListFragment newInstance(int value) {

        ListFragment listFragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("value", value);
        listFragment.setArguments(bundle);

        return listFragment;
    }

    // ----- Class variables ----- //

    private static final String TAG = "Test (" + ListFragment.class.getSimpleName() + ")";
    private static final String POI_TABLE = "poi_table";
    private static final String FILTER_POI_TABLE = "filter_poi_table";

    // ----- Member variables ------ //

    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper touchHelper;

        // Database Material

    private SQLiteDatabase database = BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper().getWritableDatabase();
    private Cursor cursor;

    // ----- Lifecycle Methods ----- //

    @Override
    public void onAttach(Activity activity) {
        Log.e(TAG, "onAttach() called");
        super.onAttach(activity);

        // THIS IS THE MOST IMPORTANT LINE FOR DELEGATION AND FRAGMENT --> ACTIVITY COMMUNICATION
        listFragDelegate = new WeakReference<ListFragDelegate>((ListFragDelegate) activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);

        if (BlocspotApplication.getSharedDataSource().isDBEmpty(FILTER_POI_TABLE)
                && Utils.getSPrefTrueCount() == 0) {
            cursor = database.query(true, POI_TABLE, null, null, null, null, null, null, null);
        } else {
            cursor = database.query(true, FILTER_POI_TABLE, null, null, null, null, null, null, null);
        }

        placeAdapter = new PlaceAdapter(BlocspotApplication.getSharedInstance(), cursor);
        placeAdapter.setPlaceAdapterDelegate(this);
        BlocspotApplication.getSharedDataSource().setDataSourceDelegate(this);
        callback = new ItemTouchHelperCallback(placeAdapter);
        touchHelper = new ItemTouchHelper(callback);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView() called");
        View inflate = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) inflate.findViewById(R.id.rv_poi_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(placeAdapter);
        touchHelper.attachToRecyclerView(recyclerView);
        return inflate;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated() called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();
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

    // ------------------------------- //

    /**
     *
     * PlaceAdapter.PlaceAdapterDelegate Implemented Methods
     *
     */

    @Override
    public void onItemClicked(PlaceAdapter placeAdapter, POI poi) {

        Log.v(TAG, "POI Item Clicked");

        if(getListFragDelegate() == null) {
            Log.v(TAG, "PROBLEM: getListFragmentDelegate() is null");
            return;
        }

        getListFragDelegate().onListItemClicked(this, poi);

    }

    @Override
    public void onItemAssigned(PlaceAdapter placeAdapter, int rowId) {
        showAssignCategoryDialog(rowId);
    }

    @Override
    public void onNoteClicked(PlaceAdapter placeAdapter, int rowId) {
        // show the add note popup dialog
        showEditNoteDialog(rowId);
    }

    @Override
    public void onVisitClicked(PlaceAdapter placeAdapter, final int rowId, boolean isChecked) {

        int dbValue = 0;

        Log.v(TAG, "Position from PlaceAdapter transferred " + rowId);

        if(isChecked) {
            dbValue = 1;
        }
        else {
            dbValue = 0;
        }

        final ContentValues values = new ContentValues();
        values.put("has_visited", dbValue);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                database.update("poi_table", values, "_id = " + rowId, null);
            }
        });
    }

    @Override
    public void onVisiteSiteClicked(PlaceAdapter placeAdapter, POI poi) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(poi.getPlaceURL())));
    }

// ----- Separate Methods ----- //

    private void showAssignCategoryDialog(int position) {
        AssignCategoryDialog assignCategoryDialog = AssignCategoryDialog.newInstance(position);
        assignCategoryDialog.show(getFragmentManager(), "assign_category");
    }

    private void showEditNoteDialog(int position) {
        EditNoteDialog editNoteDialog = EditNoteDialog.newInstance(position);
        editNoteDialog.show(getFragmentManager(), "edit_note");
    }

    @Override
    public void onQueryComplete(Cursor cursor) {
        placeAdapter.swapCursor(cursor);
    }

    public boolean isFragmentUIActive() {
        return !isDetached();
    }
}
