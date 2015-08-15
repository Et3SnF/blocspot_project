package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.ui.adapter.PlaceAdapter;

import java.lang.ref.WeakReference;

public class ListFragment extends Fragment implements PlaceAdapter.PlaceAdapterDelegate {

    // Interface for future delegation

    public static interface ListFragDelegate {
        public void onListItemClicked(ListFragment listFragment, POI poi);
    }

    // Class variables

    private static final String BUNDLE_LIST_MODE = ListFragment.class.getCanonicalName().concat(".LIST_MODE");
    private static final String TAG = "Test";

    // Member variables

    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;

        // For referencing

    private WeakReference<ListFragDelegate> listFragDelegate;

    // ----- Setter and getter for ListFragDelegate -----//

    public void setListFragDelegate(ListFragDelegate listFragDelegate) {
        this.listFragDelegate = new WeakReference<ListFragDelegate>(listFragDelegate);
    }

    public ListFragDelegate getListFragDelegate() {

        if(listFragDelegate == null) {
            return null;
        }

        return listFragDelegate.get();
    }

    // Keep these here for now. Handle them later.

    public static ListFragment listFragmentForPOI(POI poi) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_LIST_MODE, poi.getRowId());
        ListFragment listFragment = new ListFragment();
        listFragment.setArguments(bundle);
        return listFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.e(TAG, getClass().getSimpleName() + " onAttach called");
        super.onAttach(activity);

        // THIS IS THE MOST IMPORTANT LINE FOR DELEGATION AND FRAGMENT --> ACTIVITY COMMUNICATION
        listFragDelegate = new WeakReference<ListFragDelegate>((ListFragDelegate) activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeAdapter = new PlaceAdapter();
        placeAdapter.setPlaceAdapterDelegate(this);

        Log.e(TAG, getClass().getSimpleName() + " onCreate called");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, getClass().getSimpleName() + " onCreateView called");
        View inflate = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) inflate.findViewById(R.id.rv_poi_list);
        return inflate;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG, getClass().getSimpleName() + " onActivityCreated called");
        super.onActivityCreated(savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(placeAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, getClass().getSimpleName() + " onSaveInstanceState called");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.e(TAG, getClass().getSimpleName() + " onPause called");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.e(TAG, getClass().getSimpleName() + " onResume called");
        super.onResume();
    }

    @Override
    public void onItemClicked(PlaceAdapter placeAdapter, POI poi) {

        if(getListFragDelegate() == null) {
            Log.e(TAG, ListFragment.class.getSimpleName() + " PROBLEM: getListFragmentDelegate() is null");
            return;
        }

        getListFragDelegate().onListItemClicked(this, poi);

    }
}
