package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.model.database.table.CategoryTable;
import com.ngynstvn.android.blocspot.ui.Utils;

public class AddCategoryDialog extends DialogFragment {

    private static final String TAG = "Test (" + AddCategoryDialog.class.getSimpleName() + "): ";

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private EditText editText;
    private TextView addCatInstr;
    private TextView addCatCounter;

    // Special one for character tracking

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Nothing to put here
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            addCatCounter.setText(String.valueOf(s.length()) + " / 25");
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Nothing to put here
        }
    };

    // Important instance method with saved state

    public static AddCategoryDialog newInstance(int title) {

        AddCategoryDialog addCategoryDialog = new AddCategoryDialog();

        Bundle bundle = new Bundle();
        bundle.putInt("title", title);

        addCategoryDialog.setArguments(bundle);

        return addCategoryDialog;

    }

    // ----- Lifecycle Methods ------ //

    @Override
    public void onAttach(Activity activity) {
        Log.v(TAG, "onAttach() called");
        super.onAttach(activity);
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

        View view = getActivity().getLayoutInflater().inflate(R.layout.edit_category, null);

        editText = (EditText) view.findViewById(R.id.et_category_input);
        addCatInstr = (TextView) view.findViewById(R.id.tv_edit_category_instruction);
        addCatCounter = (TextView) view.findViewById(R.id.tv_editcat_edittext_counter);

        builder.setTitle("Add Category")
                .setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
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

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Overriding + button behavior

        final AlertDialog alertDialog = (AlertDialog) getDialog();

        if(alertDialog != null) {

            addCatInstr.setText("Add category below:");

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });

            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.addTextChangedListener(textWatcher);

            Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.v(TAG, "Positive Button Clicked");

                    boolean closeDialog = false;

                    String value = editText.getText().toString();

                    boolean isAlreadyInDB = BlocspotApplication.getSharedDataSource()
                            .checkIfItemIsInCatDB("category_table", "category", value);

                    if(value.equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Invalid entry. Please try again.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(isAlreadyInDB) {
                        Toast.makeText(BlocspotApplication.getSharedInstance(), value + " is already " +
                                "a category", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new CategoryTable.Builder()
                            .setCategoryName(value)
                            .setCategoryColor(Utils.generateRandomColor(android.R.color.white))
                            .setHasChecked(0)
                            .insert(BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                                    .getWritableDatabase());

                    closeDialog = true;

                    if(closeDialog) {
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

        // Called probably whenever I have another dialog and that I want to call to do something
        // to update information here
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

