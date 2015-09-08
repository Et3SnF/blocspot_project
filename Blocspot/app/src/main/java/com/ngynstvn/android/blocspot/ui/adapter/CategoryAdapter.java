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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.Category;
import com.ngynstvn.android.blocspot.ui.helper.ItemTouchHelperCallback;

import java.lang.ref.WeakReference;
import java.util.Collections;

public class CategoryAdapter extends CursorRecyclerViewAdapter<CategoryAdapter.CategoryAdapterViewHolder>
        implements ItemTouchHelperCallback.ItemTouchHelperAdapter {

    private static final String TAG = "Test (" + CategoryAdapter.class.getSimpleName() + "): ";
    private static final String CAT_TABLE = "category_table";

    // ----- Constructor ----- //

    public CategoryAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        Log.v(TAG, "CategoryAdapter object instantiated");
    }

    // ----- CategoryAdapter Methods ----- //

    @Override
    public CategoryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryAdapterViewHolder viewHolder, Cursor cursor) {
        Category category = DataSource.catFromCursor(cursor);
        viewHolder.updateViewHolder(category);
    }

    // ----- Delegation Interface Material ----- //

    public static interface CategoryAdapterDelegate {
        public void onEditButtonClicked(CategoryAdapter categoryAdapter, int position);
    }

    private WeakReference<CategoryAdapterDelegate> categoryAdapterDelegate;

    public CategoryAdapterDelegate getCategoryAdapterDelegate() {

        if(categoryAdapterDelegate == null) {
            return null;
        }

        return categoryAdapterDelegate.get();
    }

    public void setCategoryAdapterDelegate(CategoryAdapterDelegate categoryAdapterDelegate) {
        this.categoryAdapterDelegate = new WeakReference<CategoryAdapterDelegate>(categoryAdapterDelegate);
    }

    /**
     *
     * ItemTouchHelperAdapter Methods
     *
     */

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        if(fromPosition < toPosition) {
            for(int i = fromPosition; i < toPosition; i++) {
                // allows item to move down
                Collections.swap(BlocspotApplication.getSharedDataSource().getCategoryArrayList(), i, i + 1);
            }
        }
        else {
            for(int i = fromPosition; i > toPosition; i--) {
                // allows item to move up
                Collections.swap(BlocspotApplication.getSharedDataSource().getCategoryArrayList(), i, i -1);
            }
        }

        notifyItemMoved(fromPosition, toPosition); // important for adapter to be aware of this
        return true;

    }

    @Override
    public void onItemDismiss(int position) {
        BlocspotApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getWritableDatabase().delete(CAT_TABLE, "id = " + position, null);
        notifyItemRemoved(position);
    }

    // CategoryAdapterViewHolder inner class

    class CategoryAdapterViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperCallback.ItemTouchHelperViewHolder {

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

            // Filter Category listener material

            filterCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Do something here that manipulates the database. Will require delegation
                    // of some sort.
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(categoryAdapterDelegate != null) {
                        getCategoryAdapterDelegate().onEditButtonClicked(CategoryAdapter.this, getAdapterPosition());
                        notifyDataSetChanged();
                    }

                    swipeLayout.close();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemDismiss(getAdapterPosition());
                    swipeLayout.close();
                }
            });

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
