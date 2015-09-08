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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.ui.adapter.CategoryAdapter;
import com.ngynstvn.android.blocspot.ui.helper.ItemTouchHelperCallback;

public class CatDialogFragment extends DialogFragment implements CategoryAdapter.CategoryAdapterDelegate {

    // ----- Class Variables ----- //

    private static final String TAG = "Test (" + CatDialogFragment.class.getSimpleName() + "): ";
    private static final String CATEGORY_TABLE = "category_table";

    // ----- Member Variables ----- //

    private Button btnAddCategory;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper touchHelper;

    private SQLiteDatabase database = BlocspotApplication.getSharedDataSource()
            .getDatabaseOpenHelper().getWritableDatabase();
    private Cursor cursor;

    // Important single instantiation of this class

    public static CatDialogFragment newInstance(int title) {

        CatDialogFragment catDialogFragment = new CatDialogFragment();

        // Supply num input as an argument --> for retrieving stuff later

        Bundle bundle = new Bundle();
        bundle.putInt("title", title);

        catDialogFragment.setArguments(bundle);

        return catDialogFragment;

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
        categoryAdapter = new CategoryAdapter(BlocspotApplication.getSharedInstance(), cursor);
        categoryAdapter.setCategoryAdapterDelegate(this);
        callback = new ItemTouchHelperCallback(categoryAdapter);
        touchHelper = new ItemTouchHelper(callback);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.v(TAG, "onCreateDialog() called");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.MaterialAlertDialogStyle);

        View view = getActivity().getLayoutInflater().inflate(R.layout.category_dialog_layout, null);

        // Inflate anything specifically in this lifecycle method

        btnAddCategory = (Button) view.findViewById(R.id.btn_add_category);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_custom_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(categoryAdapter);
        touchHelper.attachToRecyclerView(recyclerView);

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Positive Button Clicked");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Negative Button Clicked");
                    }
                })
                .setNeutralButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Reset Button Clicked");
                        Toast.makeText(getActivity(), "Reset Button Clicked", Toast.LENGTH_SHORT).show();
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
        // This is where the fragment stops at when I first load it.

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Add Category Button Clicked");
                dismiss();
                showAddCategoryDialog();
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

        // Called probably whenever I have another dialog and that I want to call to do something
        // to update information here
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

    /**
     *
     *  CategoryAdapter.CategoryAdapterDelegate implemented methods
     *
     */

    @Override
    public void onEditButtonClicked(CategoryAdapter categoryAdapter, int position) {
        dismiss();
        showEditCategoryDialog(position);
        categoryAdapter.notifyItemChanged(position);
    }

    // ---- Separate Methods ----- //

    void showAddCategoryDialog() {
        DialogFragment dialogFragment = AddCategoryDialog.newInstance(R.string.fbc_add_category_text);
        dialogFragment.show(getFragmentManager(), "add_category");
    }

    void showEditCategoryDialog(final int position) {
        EditCategoryDialog editCategoryDialog = EditCategoryDialog.newInstance(position);
        editCategoryDialog.show(getFragmentManager(), "edit_category");
    }

}

