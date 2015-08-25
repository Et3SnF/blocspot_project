package com.ngynstvn.android.blocspot.api.model;

public class Category {

    // ----- Class Variables ----- //

    private static final String TAG = "Test: (" + Category.class.getSimpleName() + "): ";

    // ----- Member Variables ------ //

    private String categoryName = "";
    private int categoryColor = 0;

    // Constructors

    public Category() {
        this.categoryName = "";
        this.categoryColor = 0;
    }

    public Category(String categoryName, int categoryColor) {
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
