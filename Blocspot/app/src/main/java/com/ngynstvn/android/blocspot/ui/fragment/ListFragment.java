package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.ui.adapter.PlaceAdapter;
import com.ngynstvn.android.blocspot.ui.helper.ItemTouchHelperCallback;

import java.lang.ref.WeakReference;

public class ListFragment extends Fragment implements PlaceAdapter.PlaceAdapterDelegate {

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

    // ----- Class variables ----- //

    private static final String BUNDLE_LIST_MODE = ListFragment.class.getCanonicalName().concat(".LIST_MODE");
    private static final String TAG = "Test (" + ListFragment.class.getSimpleName() + ")";

    // ----- Member variables ------ //

    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper touchHelper;

    // Keep these here for now. Handle them later.

    public static ListFragment listFragmentForPOI(POI poi) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_LIST_MODE, poi.getRowId());
        ListFragment listFragment = new ListFragment();
        listFragment.setArguments(bundle);
        return listFragment;
    }

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
        placeAdapter = new PlaceAdapter();
        placeAdapter.setPlaceAdapterDelegate(this);
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
        Log.e(TAG, getClass().getSimpleName() + " onPause called");
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

        if(getListFragDelegate() == null) {
            Log.v(TAG, "PROBLEM: getListFragmentDelegate() is null");
            return;
        }

        getListFragDelegate().onListItemClicked(this, poi);

    }

    @Override
    public void onAssignCatButtonClicked(PlaceAdapter placeAdapter, int position) {
        showAssignCategoryDialog(position);
        // Work whatever necessary in the AssignCategoryDialog
    }

    @Override
    public void onAddNoteButtonClicked(PlaceAdapter placeAdapter, int position) {
        // show the add note popup dialog
    }

    // ----- Separate Methods ----- //

    private void showAssignCategoryDialog(int position) {
        AssignCategoryDialog assignCategoryDialog = AssignCategoryDialog.newInstance(position);
        assignCategoryDialog.show(getFragmentManager(), "assign_category");
    }

}
