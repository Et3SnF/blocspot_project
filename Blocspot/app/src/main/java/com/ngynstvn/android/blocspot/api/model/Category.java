package com.ngynstvn.android.blocspot.api.model;

import android.database.Cursor;

import com.ngynstvn.android.blocspot.api.model.database.table.CategoryTable;

public class Category extends Model{

    // ----- Class Variables ----- //

    private static final String TAG = "Test: (" + Category.class.getSimpleName() + "): ";

    // ----- Member Variables ------ //

    private String categoryName = "";
    private int categoryColor = 0;

    // Constructors

    public Category(long rowId) {
        super(rowId);
        this.categoryName = "";
        this.categoryColor = 0;
    }

    public Category(long rowId, String categoryName, int categoryColor) {
        super(rowId);
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
    }

    // Setters and Getters

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryColor(int categoryColor) {
        this.categoryColor = categoryColor;
    }

    public int getCategoryColor() {
        return categoryColor;
    }

    // Cursor method for retrieving data from database

    public static Category fromCursor(Cursor cursor) {
        return new Category(CategoryTable.getRowId(cursor), CategoryTable.getCategoryName(cursor),
                CategoryTable.getCategoryColor(cursor));
    }
}
