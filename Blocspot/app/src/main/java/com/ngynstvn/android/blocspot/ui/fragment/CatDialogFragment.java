package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.ui.adapter.CategoryAdapter;

public class CatDialogFragment extends DialogFragment {

    // ----- Class Variables ----- //

    private static final String TAG = "Test (" + CatDialogFragment.class.getSimpleName() + "): ";


    // ----- Member Variables ----- //

    private int num;

        // RecyclerView variables
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;

        // ItemTouchHelper variables

    private ItemTouchHelper itemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleCallback;

    // New Instance of Category Fragment

    public static CatDialogFragment newInstance(int title) {

        CatDialogFragment catDialogFragment = new CatDialogFragment();

        // Supply num input as an argment
        Bundle bundle = new Bundle();
        bundle.putInt("title", title);

        catDialogFragment.setArguments(bundle);

        return catDialogFragment;
    }

    // ----- Lifecycle Methods ------ //


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryAdapter = new CategoryAdapter();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int title = getArguments().getInt("title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaterialAlertDialogStyle);

        // For custom layout dialog view, find a way to inflate it

        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Add some form of RecyclerView in here

        View view = inflater.inflate(R.layout.fragment_category_dialog, null, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_category_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(categoryAdapter);

        builder.setIcon(R.drawable.ic_filter_list_black_24dp)
        .setTitle(title)
        .setView(view)
        .setPositiveButton(R.string.alert_dialog_okay,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Positive Button Clicked");
                    }
                }
        )
        .setNegativeButton(R.string.alert_dialog_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Negative Button Clicked");
                    }
                }
        )
        .setNeutralButton("Add Category", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(TAG, "Add Button Clicked");
            }
        });

        return builder.create();
    }

    // --------------------------------- //

    private void activateSwipeToAction(RecyclerView recyclerView) {

        simpleCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder
                            viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                    }
                };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

}
