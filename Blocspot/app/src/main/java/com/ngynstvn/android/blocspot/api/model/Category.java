package com.ngynstvn.android.blocspot.api.model;

public class Category extends Model {

    // ----- Class Variables ----- //

    private static final String TAG = "Test: (" + Category.class.getSimpleName() + "): ";

    // ----- Member Variables ------ //

    private String categoryName = "";
    private int categoryColor = 0;

    // Constructors

    public Category(long rowId) {
        super(rowId);
        categoryName = "";
        categoryColor = 0;
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
}
