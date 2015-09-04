package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.model.Category;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.ui.adapter.AssignCategoryAdapter;

import java.util.ArrayList;

public class AssignCategoryDialog extends DialogFragment implements AssignCategoryAdapter.AssignCategoryAdapterDelegate {

    private static final String TAG = "Test: (" + AssignCategoryDialog.class.getSimpleName() + "): ";

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private RecyclerView recyclerView;
    private AssignCategoryAdapter assignCategoryAdapter;

    private ArrayList<Category> categoryArrayList = BlocspotApplication.getSharedDataSource().getCategoryArrayList();
    private ArrayList<POI> poiArrayList = BlocspotApplication.getSharedDataSource().getPoiArrayList();

    public static AssignCategoryDialog newInstance(int position) {

        AssignCategoryDialog assignCategoryDialog = new AssignCategoryDialog();

        // Supply num input as an argument --> for retrieving stuff later

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);

        assignCategoryDialog.setArguments(bundle);

        return assignCategoryDialog;

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
        assignCategoryAdapter = new AssignCategoryAdapter();
        assignCategoryAdapter.setCategoryAdapterDelegate(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "onCreateDialog() called");

        View view = getActivity().getLayoutInflater().inflate(R.layout.assign_category_dialog_layout, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_assign_category_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(assignCategoryAdapter);

        builder = new AlertDialog.Builder(getActivity(), R.style.MaterialAlertDialogStyle)
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

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
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.v(TAG, "onCancel() called");
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

    // ------------------------------------------- //

    /**
     *
     * AssignCategoryAdapter.AssignCategoryAdapterDelegate implemented methods
     *
     */

    @Override
    public void onCategoryAssignmentClicked(int catItemPosition) {

        Log.v(TAG, "onCategoryAssignmentClicked() called");

        String categoryName = categoryArrayList.get(catItemPosition).getCategoryName();
        int categoryColor = categoryArrayList.get(catItemPosition).getCategoryColor();

        // Input validation

        if(categoryName.equalsIgnoreCase(poiArrayList.get(getArguments().getInt("position")).getCategoryName())) {
            Toast.makeText(BlocspotApplication.getSharedInstance(), "Point of Interest is already " +
                    "assigned this!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the category of POI item as categoryName

        poiArrayList.get(getArguments().getInt("position")).setCategoryName(categoryName);
        poiArrayList.get(getArguments().getInt("position")).setCategoryColor(categoryColor);

        Toast.makeText(BlocspotApplication.getSharedInstance(), "Point of interest was assigned to "
                + categoryName, Toast.LENGTH_SHORT).show();

        dismiss();

    }
}
