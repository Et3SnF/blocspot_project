package com.ngynstvn.android.blocspot.api.model.fts_table;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

import java.util.HashMap;

public class FTSVirtualTable {

    private static final String TAG = "Test: " + FTSVirtualTable.class.getSimpleName() + ": ";
    public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;

    private static final String FTS_VTABLE_NAME = "yelp_results";
    private static final int VTABLE_VERSION = 1;

    private final FTSOpenHelper ftsOpenHelper;
    private static final HashMap<String,String> columnMap = buildColumnMap();

    public FTSVirtualTable(Context context) {
        ftsOpenHelper = new FTSOpenHelper(context);
    }

    private static HashMap<String,String> buildColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(KEY_WORD, KEY_WORD);
        map.put(BaseColumns._ID, "rowid AS " +
                BaseColumns._ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }

    public Cursor getResult(String rowId, String[] columns) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[] {rowId};
        return query(selection, selectionArgs, columns);
    }

    public Cursor getMatches(String query, String[] columns) {
        String selection = KEY_WORD + " MATCH ?";
        String[] selectionArgs = new String[]{query + "*"};
        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VTABLE_NAME);
        builder.setProjectionMap(columnMap);

        Cursor cursor = builder.query(ftsOpenHelper.getReadableDatabase(), columns, selection,
                selectionArgs, null, null, null);

        if(cursor == null) {
            return null;
        }
        else if(!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        return cursor;
    }

    private static class FTSOpenHelper extends SQLiteOpenHelper {

        private final Context helperContext;
        private SQLiteDatabase database;

        FTSOpenHelper(Context context) {
            super(context, FTS_VTABLE_NAME, null, VTABLE_VERSION);
            helperContext = context;
        }

        private static final String FTS_TABLE_CREATE = "Create virtual table " + FTS_VTABLE_NAME +
                " using fts3 (" + KEY_WORD + ");";

        @Override
        public void onCreate(SQLiteDatabase db) {
            database = db;
            database.execSQL(FTS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
