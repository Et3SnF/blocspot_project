package com.ngynstvn.android.blocspot.ui.adapter;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.model.Category;

import java.lang.ref.WeakReference;

public class AssignCategoryAdapter extends RecyclerView.Adapter<AssignCategoryAdapter.AssignCategoryAdapterViewHolder> {

    // ----- INTERFACE ----- //

    public static interface AssignCategoryAdapterDelegate {
        public void onCategoryAssignmentClicked(int position);
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

    public AssignCategoryAdapter() {
        Log.v(TAG, "CategoryAdapter object instantiated");
    }

    @Override
    public int getItemCount() {
        return BlocspotApplication.getSharedDataSource().getCategoryArrayList().size();
    }

    @Override
    public AssignCategoryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assign_category_item, parent, false);
        return new AssignCategoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AssignCategoryAdapterViewHolder holder, int position) {
        holder.updateViewHolder(BlocspotApplication.getSharedDataSource().getCategoryArrayList().get(position));
    }

    // CategoryAdapterViewHolder inner class

    class AssignCategoryAdapterViewHolder extends RecyclerView.ViewHolder {

        // ----- Member variables ----- //

        private TextView categoryColor;
        private TextView categoryName;

        private Category category;

        // Constructor

        public AssignCategoryAdapterViewHolder(View itemView) {

            // Constructor is for inflation and listeners, that's it! No updating info!!..idiot

            super(itemView);

            categoryColor = (TextView) itemView.findViewById(R.id.tv_category_color);
            categoryName = (TextView) itemView.findViewById(R.id.tv_category_name);

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(getAssignCategoryDelegate() == null) {
                        Toast.makeText(BlocspotApplication.getSharedInstance(), "Assign Category " +
                                "Item #" + (getAdapterPosition()+1) + " clicked", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    getAssignCategoryDelegate().onCategoryAssignmentClicked(getAdapterPosition());
                    // Do something about this....
                }
            });

        }

        // ----- Separate Methods ----- //

        void updateViewHolder(Category category) {

            this.category = category;

            // Set the name and color of the category

            categoryName.setText(category.getCategoryName());
            categoryColor.setBackgroundColor(category.getCategoryColor());

        }

    }



}
