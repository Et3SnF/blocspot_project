package com.ngynstvn.android.blocspot.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;

public class EditNoteDialog extends DialogFragment {

    // ----- Class Variables ----- //

    private static final String TAG = "Test (" + EditNoteDialog.class.getSimpleName() + "): ";

    // ----- Member Variables ----- //

    private AlertDialog.Builder builder;
    private EditText editText;
    private TextView editNoteCounter;

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Nothing to put here
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            editNoteCounter.setText(String.valueOf(s.length()) + " / 250");
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Nothing to put here
        }
    };

    // Important single instantiation of this class

    public static EditNoteDialog newInstance(int rowId) {

        EditNoteDialog editNoteDialog = new EditNoteDialog();

        // Supply num input as an argument --> for retrieving stuff later

        Bundle bundle = new Bundle();
        bundle.putInt("rowId", rowId);

        editNoteDialog.setArguments(bundle);

        return editNoteDialog;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        builder = new AlertDialog.Builder(getActivity(),
        R.style.MaterialAlertDialogStyle);

        View view = getActivity().getLayoutInflater().inflate(R.layout.edit_note, null);

        editText = (EditText) view.findViewById(R.id.et_note_input);
        editNoteCounter = (TextView) view.findViewById(R.id.tv_edit_note_edittext_counter);

        builder.setTitle("Edit Note")
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
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        editText.addTextChangedListener(textWatcher);

        final AlertDialog alertDialog = (AlertDialog) getDialog();

        if(alertDialog != null) {

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Overridden positive button clicked");
                    boolean closeDialog = false;

                    String value = editText.getText().toString();

                    // Update the note for the item

                    final ContentValues values = new ContentValues();
                    values.put("description", value);

                    Log.v(TAG, "Value of rowId: " + getArguments().getInt("rowId"));

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                                    .getWritableDatabase().update("poi_table", values, "_id = "
                                    + (getArguments().getInt("rowId")), null);

                            Toast.makeText(BlocspotApplication.getSharedInstance(), "The note has " +
                                    "been successfully updated", Toast.LENGTH_SHORT).show();

                            getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocspot,
                                    ListFragment.newInstance(getArguments().getInt("rowId"))).commit();
                        }
                    });

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
    }
}
