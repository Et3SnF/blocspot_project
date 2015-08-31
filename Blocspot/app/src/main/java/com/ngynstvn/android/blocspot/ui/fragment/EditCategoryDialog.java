package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.R;

import java.lang.ref.WeakReference;

public class EditCategoryDialog extends DialogFragment {

    public static interface EditCatDialogDelegate {
        public void onItemEdited(EditCategoryDialog editCategoryDialog, String value);
    }

    private WeakReference<EditCatDialogDelegate> editCatDialogDelegate;

    public void setEditDialogDelegate(EditCatDialogDelegate editCatDialogDelegate) {
        this.editCatDialogDelegate = new WeakReference<EditCatDialogDelegate>(editCatDialogDelegate);
    }

    public EditCatDialogDelegate getEditCatDialogDelegate() {

        if(editCatDialogDelegate == null) {
            return null;
        }

        return editCatDialogDelegate.get();
    }

    private static String TAG = "Test (" + EditCategoryDialog.class.getSimpleName() + "): ";

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private EditText editText;



    // Important instance method with saved state

    public static EditCategoryDialog newInstance(int title) {

        EditCategoryDialog editCategoryDialog = new EditCategoryDialog();

        Bundle bundle = new Bundle();
        bundle.putInt("title", title);

        editCategoryDialog.setArguments(bundle);

        return editCategoryDialog;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        builder = new AlertDialog.Builder(getActivity(), R.style.MaterialAlertDialogStyle);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "onCreateDialog() called");

        View view = getActivity().getLayoutInflater().inflate(R.layout.category_input, null);

        editText = (EditText) view.findViewById(R.id.et_category_input);

        builder.setTitle("Edit Category")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Positive Button clicked");
                        // Leave this blank. It is being handled somewhere else!
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Negative Button clicked");
                        showCategoryDialog();
                    }
                });

        alertDialog = builder.create();

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

        // Overriding + button behavior

        final AlertDialog alertDialog = (AlertDialog) getDialog();

        if(alertDialog != null) {

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });

            editText.setInputType(InputType.TYPE_CLASS_TEXT);

            Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.v(TAG, "Positive Button Clicked");

                    boolean closeDialog = false;

                    String value = editText.getText().toString();

                    // Modify this to be database stuff later

                    if (value.equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Invalid entry. Please try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Delegation pattern here

                    if(value != null) {

                        if(getEditCatDialogDelegate() == null) {
                            Log.v(TAG, "editCatDialogDelegate is null");
                            return;
                        }

                        getEditCatDialogDelegate().onItemEdited(EditCategoryDialog.this, value);

                        closeDialog = true;
                    }

                    if (closeDialog) {
                        dismiss();
                        showCategoryDialog();
                    }
                }
            });
        }
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

    // ------------------------- //

    void showCategoryDialog(){
        CatDialogFragment catDialogFragment = CatDialogFragment.newInstance(R.string.fbc_dialog_title);
        catDialogFragment.show(getFragmentManager(), "category_dialog");
    }
}
