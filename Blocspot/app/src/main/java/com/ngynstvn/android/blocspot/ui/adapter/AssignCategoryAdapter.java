package com.ngynstvn.android.blocspot.ui.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Outline;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.Category;

import java.lang.ref.WeakReference;

public class AssignCategoryAdapter extends CursorRecyclerViewAdapter<AssignCategoryAdapter.AssignCategoryAdapterViewHolder> {

    // ----- INTERFACE ----- //

    public static interface AssignCategoryAdapterDelegate {
        public void onCategoryAssignmentClicked(int catItemPosition);
    }

    private WeakReference<AssignCategoryAdapterDelegate> assignCategoryDelegate;

    public AssignCategoryAdapterDelegate getAssignCategoryDelegate() {

        if(assignCategoryDelegate == null) {
            return null;
        }

        return assignCategoryDelegate.get();
    }

    public void setCategoryAdapterDelegate(AssignCategoryAdapterDelegate assignCategoryDelegate) {
        this.assignCategoryDelegate = new WeakReference<AssignCategoryAdapterDelegate>(assignCategoryDelegate);
    }

    private static final String TAG = "Test (" + AssignCategoryAdapter.class.getSimpleName() + "): ";

    public AssignCategoryAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        Log.v(TAG, "CategoryAdapter object instantiated");
    }

    @Override
    public AssignCategoryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assign_category_item, parent, false);
        return new AssignCategoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AssignCategoryAdapterViewHolder viewHolder, Cursor cursor) {
        Category category = DataSource.catFromCursor(cursor);
        viewHolder.updateViewHolder(category);
    }

    // CategoryAdapterViewHolder inner class

    class AssignCategoryAdapterViewHolder extends RecyclerView.ViewHolder {

        // ----- Member variables ----- //

        private TextView categoryColor;
        private TextView categoryName;
        private View dividerLine;

        private Category category;

        // Constructor

        public AssignCategoryAdapterViewHolder(View itemView) {

            // Constructor is for inflation and listeners, that's it! No updating info!!..idiot

            super(itemView);

            categoryColor = (TextView) itemView.findViewById(R.id.tv_category_color);
            categoryName = (TextView) itemView.findViewById(R.id.tv_category_name);
            dividerLine = itemView.findViewById(R.id.v_assign_category_divider);

            // Need this check to check API version otherwise a RunTimeException occurs

            if(Build.VERSION.SDK_INT >= 21) {

                categoryColor.setText("");

                categoryColor.setOutlineProvider(new ViewOutlineProvider() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0,0, view.getWidth(), view.getHeight());
                    }
                });

                categoryColor.setClipToOutline(true);
            }

            // Remove the divider line if it's the last item

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(getAssignCategoryDelegate() == null) {
                        return;
                    }

                    getAssignCategoryDelegate().onCategoryAssignmentClicked(getAdapterPosition());
                    // Whoever delegates this will do the necessary actions
                }
            });

        }

        // ----- Separate Methods ----- //

        void updateViewHolder(Category category) {

            this.category = category;

            // Set the name and color of the category

            categoryName.setText(category.getCategoryName());
            categoryColor.setBackgroundColor(category.getCategoryColor());

//            if(getAdapterPosition() == BlocspotApplication.getSharedDataSource().getCategoryArrayList().size()-1) {
//                dividerLine.setVisibility(View.GONE);
//            }

        }

    }



}
