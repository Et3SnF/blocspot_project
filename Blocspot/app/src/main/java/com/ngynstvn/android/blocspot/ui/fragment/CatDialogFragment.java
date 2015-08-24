package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.ngynstvn.android.blocspot.R;

public class CatDialogFragment extends DialogFragment {

    private static final String TAG = "Test (" + CatDialogFragment.class.getSimpleName() + "): ";

    // ----- Class Variables ----- //


    // ----- Member Variables ----- //

    private int num;

    // New Instance of Category Fragment

    public static CatDialogFragment newInstance(int title) {

        CatDialogFragment catDialogFragment = new CatDialogFragment();

        // Supply num input as an argment
        Bundle bundle = new Bundle();
        bundle.putInt("title", title);

        catDialogFragment.setArguments(bundle);

        return catDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity(), R.style.MaterialAlertDialogStyle)
                .setIcon(R.drawable.ic_filter_list_black_24dp)
                .setTitle(title)
                .setMessage("This is just a test to display info on the dialog")
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
                        }).create(); // create() is part of new AlertDialog.Builder(...)
    }
}
