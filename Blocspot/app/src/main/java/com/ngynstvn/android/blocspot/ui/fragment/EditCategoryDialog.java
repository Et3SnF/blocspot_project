package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;

public class EditCategoryDialog extends DialogFragment {

    private static final String TAG = "Test (" + EditCategoryDialog.class.getSimpleName() + "): ";

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private EditText editText;

    // Important single instantiation of this class

    public static EditCategoryDialog newInstance(int position) {

        EditCategoryDialog editCategoryDialog = new EditCategoryDialog();

        // Supply num input as an argument --> for retrieving stuff later

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);

        editCategoryDialog.setArguments(bundle);

        return editCategoryDialog;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArguments().getInt("position");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
        R.style.MaterialAlertDialogStyle);

        View view = getActivity().getLayoutInflater().inflate(R.layout.category_input, null);

        editText = (EditText) view.findViewById(R.id.et_category_input);

        builder.setTitle("Edit Category")
                .setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Leave this blank. It is being handled somewhere else!
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v("test", "Negative Button clicked");
                        dismiss();
                    }
                });

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog alertDialog = (AlertDialog) getDialog();

        if(alertDialog != null) {

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Overridden positive button clicked");
                    boolean closeDialog = false;

                    String value = editText.getText().toString();

                    if (value.equalsIgnoreCase("")) {
                        Toast.makeText(BlocspotApplication.getSharedInstance(), "Invalid entry. Please " +
                                "try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (value.equalsIgnoreCase(BlocspotApplication.getSharedDataSource()
                            .getCategoryArrayList().get(getArguments().getInt("position")).getCategoryName())) {
                        Toast.makeText(BlocspotApplication.getSharedInstance(), "Enter a different name " +
                                "or cancel", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    BlocspotApplication.getSharedDataSource().getCategoryArrayList()
                            .get(getArguments().getInt("position")).setCategoryName(value);
                    closeDialog = true;

                    if (closeDialog) {
                        alertDialog.dismiss();
                    }
                }
            });

        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Show the category dialog once this method hits

        DialogFragment dialogFragment = CatDialogFragment.newInstance(getArguments().getInt("title"));
        dialogFragment.show(getFragmentManager(), "category_dialog");

        // Call a fragment to recover the dialog
    }
}
