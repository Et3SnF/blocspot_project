package com.ngynstvn.android.blocspot.api.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ngynstvn.android.blocspot.api.model.database.table.Table;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    // For logging purposes

    private static final String TAG = "Test (" + DatabaseOpenHelper.class.getSimpleName() + "): ";

    // Class variables

    private static final String dbNAME = "blocspot_db";
    private static final int VERSION = 1;

    // Member variables

    private Table[] tables;

    // Constructor

    public DatabaseOpenHelper(Context context, Table... tables) {
        super(context, dbNAME, null, VERSION); //SQLiteOpenHelper(Context, CursorFactory, int)
        this.tables = tables;
        Log.v(TAG, "DatabaseOpenHelper class instantiation");
    }

    // Necessary create method

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "onCreate() called");
        for(int i = 0; i < tables.length; i++) {
            db.execSQL(tables[i].getCreateStatement());
        }
    }

    // upgrade the tables

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "onUpgrade() called");
        for(int i = 0; i < tables.length; i++) {
            tables[i].onUpgrade(db, oldVersion, newVersion);
        }
    }
}
