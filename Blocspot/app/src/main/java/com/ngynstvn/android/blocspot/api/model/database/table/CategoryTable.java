package com.ngynstvn.android.blocspot.api.model.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CategoryTable extends Table {

    // ----- Class Variables ----- //

    private static final String TAG = "Test: (" + CategoryTable.class.getSimpleName() + "): ";

    private static final String NAME = "category_table";
    private static final String COLUMN_CATEGORY_NAME = "category";
    private static final String COLUMN_CATEGORY_COLOR = "category_color";

    // Getters

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCreateStatement() {
        Log.v(TAG, "getCreateStatement() called");

        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_CATEGORY_NAME + " TEXT, "
                + COLUMN_CATEGORY_COLOR + " INTEGER);";
    }

    // Get category table

    public static Cursor getAllCategories(SQLiteDatabase readonlyDatabase) {
        // select * from category_table order by category_color;
        return readonlyDatabase.rawQuery("SELECT * FROM " + NAME + " ORDER BY ?", new String[]{COLUMN_CATEGORY_COLOR});
    }


    // Builder class

    public static class Builder implements Table.Builder {

        ContentValues values = new ContentValues();

        public Builder setCategoryName(String categoryName) {
            values.put(COLUMN_CATEGORY_NAME, categoryName);
            return this;
        }

        public Builder setCategoryColor(int categoryColor) {
            values.put(COLUMN_CATEGORY_COLOR, categoryColor);
            return this;
        }

        // Insert statement

        @Override
        public long insert(SQLiteDatabase writableDB) {
            // insert into category_table (id, category, category_color) values (1, "pigeon", -151351234);
            return writableDB.insert(NAME, null, values);
        }

    }

    // Get column names

    public static String getCategoryName(Cursor cursor) {
        return getString(cursor, COLUMN_CATEGORY_NAME);
    }

    public static int getCategoryColor(Cursor cursor) {
        return getInteger(cursor, COLUMN_CATEGORY_COLOR);
    }

}
