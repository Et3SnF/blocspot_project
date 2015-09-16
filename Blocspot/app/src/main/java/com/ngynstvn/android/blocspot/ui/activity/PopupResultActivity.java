package com.ngynstvn.android.blocspot.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.ngynstvn.android.blocspot.R;

public class PopupResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_dialog_layout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (0.85*width), (int) (0.35*height));
    }
}
