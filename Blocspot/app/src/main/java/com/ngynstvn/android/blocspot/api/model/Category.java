package com.ngynstvn.android.blocspot.api.model;

public class Category extends Model{

    // ----- Class Variables ----- //

    private static final String TAG = "Test: (" + Category.class.getSimpleName() + "): ";

    // ----- Member Variables ------ //

    private String categoryName = "";
    private int categoryColor = 0;
    private boolean isCatChecked = false;

    // Constructors

    public Category(long rowId) {
        super(rowId);
        this.categoryName = "";
        this.categoryColor = 0;
        isCatChecked = false;
    }

    public Category(long rowId, String categoryName, int categoryColor, boolean isCatChecked) {
        super(rowId);
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.isCatChecked = isCatChecked;
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

    public void setIsCatChecked(boolean isCatChecked) {
        this.isCatChecked = isCatChecked;
    }

    public boolean getIsCatChecked() {
        return isCatChecked;
    }

}
