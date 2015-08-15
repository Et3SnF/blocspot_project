package com.ngynstvn.android.blocspot.api.model.database.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class Table {

    // Static interface for Builder Class

    public static interface Builder{
        public long insert(SQLiteDatabase writableDB);
    }

    // Class variable

    protected static final String COLUMN_ID = "id";

    // Abstract methods..will create them myself

    public abstract String getName();
    public abstract String getCreateStatement();

    // Fetch row method

    public Cursor fetchRow(SQLiteDatabase readableDB, long rowId) {
        return readableDB.query(true, getName(), null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(rowId)}, null, null, null, null);
    }

    // getRowId method

    public static long getRowId(Cursor cursor) {
        return getLong(cursor, COLUMN_ID);
    }

    // get info based on data type

    protected static int getInteger(Cursor cursor, String column) {
        int columnIndex = cursor.getColumnIndex(column);

        if(columnIndex == -1) {
            return -1;
        }

        return cursor.getInt(columnIndex);
    }

    protected static double getDouble(Cursor cursor, String column) {
        int columnIndex = cursor.getColumnIndex(column);

        if(columnIndex == -1) {
            return -1.00D;
        }

        return cursor.getDouble(columnIndex);
    }

    public static long getLong(Cursor cursor, String column) {
        int columnIndex = cursor.getColumnIndex(column);

        if(columnIndex == -1) {
            return -1L;
        }

        return cursor.getLong(columnIndex);
    }

    protected static String getString(Cursor cursor, String column) {
        int columnIndex = cursor.getColumnIndex(column);

        if(columnIndex == -1) {
            return "";
        }

        return cursor.getString(columnIndex);
    }

    public void onUpgrade(SQLiteDatabase writableDatabase, int oldVersion, int newVersion) {
        // In case one table is upgrade over the other
    }
}

