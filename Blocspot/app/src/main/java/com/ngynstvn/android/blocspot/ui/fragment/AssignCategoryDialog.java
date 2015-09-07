package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.model.Category;
import com.ngynstvn.android.blocspot.ui.adapter.AssignCategoryAdapter;

public class AssignCategoryDialog extends DialogFragment implements AssignCategoryAdapter.AssignCategoryAdapterDelegate {

    private static final String TAG = "Test: (" + AssignCategoryDialog.class.getSimpleName() + "): ";
    private static final String CATEGORY_TABLE = "category_table";

    private AlertDialog.Builder builder;
    private RecyclerView recyclerView;
    private AssignCategoryAdapter assignCategoryAdapter;

    private Category category;
    private SQLiteDatabase database = BlocspotApplication.getSharedDataSource()
            .getDatabaseOpenHelper().getWritableDatabase();
    private Cursor cursor;

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
        cursor = database.query(true, CATEGORY_TABLE, null, null, null, null, null, "category_color", null);
        assignCategoryAdapter = new AssignCategoryAdapter(BlocspotApplication.getSharedInstance(), cursor);
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
        
        dismiss();

    }
}
