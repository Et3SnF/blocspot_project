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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.model.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryAdapterViewHolder>
        implements ItemTouchHelperAdapter {

    // ----- Class Variables ----- //

    private static final String TAG = "Test (" + CategoryAdapter.class.getSimpleName() + "): ";

    // ----- Member Variables ----- //

    // ----- Constructor ----- //

    public CategoryAdapter() {
        Log.v(TAG, "CategoryAdapter object instantiated");
    }

    // ----- CategoryAdapter Methods ----- //

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public CategoryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryAdapterViewHolder holder, int position) {

    }

    /**
     *
     * ItemTouchHelperAdapter Methods
     *
     */

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    // CategoryAdapterViewHolder inner class

    class CategoryAdapterViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        // ----- Member variables ----- //

        private TextView categoryColor;
        private TextView categoryName;
        private CheckBox filterCategory;

        private Category category;

        boolean isItemClickable = true;
        SwipeLayout swipeLayout;
        Button deleteButton;
        Button editButton;

        // Constructor

        public CategoryAdapterViewHolder(final View itemView) {

            // Constructor is for inflation and listeners, that's it! No updating info!!..idiot

            super(itemView);

            categoryColor = (TextView) itemView.findViewById(R.id.tv_category_color);
            categoryName = (TextView) itemView.findViewById(R.id.tv_category_name);
            filterCategory = (CheckBox) itemView.findViewById(R.id.cb_filter_category);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.sl_category_item);
            deleteButton = (Button) itemView.findViewById(R.id.btn_delete_category);
            editButton = (Button) itemView.findViewById(R.id.btn_edit_category);

            // SwipeLayout material

            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout swipeLayout) {
                    itemView.setClickable(!isItemClickable);
                    itemView.setLongClickable(!isItemClickable);
                }

                @Override
                public void onOpen(SwipeLayout swipeLayout) {
                    itemView.setClickable(!isItemClickable);
                    itemView.setLongClickable(!isItemClickable);
                }

                @Override
                public void onStartClose(SwipeLayout swipeLayout) {
                    itemView.setClickable(!isItemClickable);
                    itemView.setLongClickable(!isItemClickable);
                }

                @Override
                public void onClose(SwipeLayout swipeLayout) {
                    itemView.setLongClickable(isItemClickable);
                    itemView.setClickable(isItemClickable);
                }

                @Override
                public void onUpdate(SwipeLayout swipeLayout, int i, int i1) {

                }

                @Override
                public void onHandRelease(SwipeLayout swipeLayout, float v, float v1) {

                }
            });

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

            // Might implement swipe to delete here...

        }

        // ----- Separate Methods ----- //

        void updateViewHolder(Category category) {

            this.category = category;

            // Set the name and color of the category

            categoryName.setText(category.getCategoryName());
            categoryColor.setBackgroundColor(category.getCategoryColor());

        }


        /**
         *
         *
         * ItemTouchHelperViewHolder methods
         *
         */

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(0x1E000000);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0x00000000);
        }

    }

}
