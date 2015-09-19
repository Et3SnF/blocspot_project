package com.ngynstvn.android.blocspot.ui.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.ui.helper.ItemTouchHelperCallback;

import java.lang.ref.WeakReference;

public class PlaceAdapter extends CursorRecyclerViewAdapter<PlaceAdapter.PlaceAdapterViewHolder>
        implements ItemTouchHelperCallback.ItemTouchHelperAdapter {

    // Class variables

    private static final String TAG = "Test (" + PlaceAdapter.class.getSimpleName() + ")";

    // Member variables

    // Constructor

    public PlaceAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public PlaceAdapter.PlaceAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log.v(TAG, "onCreateViewHolder() called");
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.poi_item, parent, false);
        return new PlaceAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(PlaceAdapterViewHolder viewHolder, Cursor cursor) {
        POI poi = DataSource.poiFromCursor(cursor);
        viewHolder.updateViewHolder(poi);
    }

    // ----- Delegation Interface and Accessors & Mutators ----- //

    public static interface PlaceAdapterDelegate {
        public void onItemClicked(PlaceAdapter placeAdapter, POI poi);
        public void onItemAssigned(PlaceAdapter placeAdapter, int position);
        public void onNoteAdded(PlaceAdapter placeAdapter, int position);
        public void onVisitClicked(PlaceAdapter placeAdapter, int rowId, boolean isChecked);
    }

    // Setter and getter for delegate

    private WeakReference<PlaceAdapterDelegate> adapterDelegate;

    public void setPlaceAdapterDelegate(PlaceAdapterDelegate adapterDelegate) {
        this.adapterDelegate = new WeakReference<PlaceAdapterDelegate>(adapterDelegate);
    }

    public PlaceAdapterDelegate getAdapterDelegate() {

        if(adapterDelegate == null) {
            return null;
        }

        return adapterDelegate.get();
    }

    /**
     *
     * ItemTouchHelperCallback.ItemTouchHelperAdapter
     *
     */

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        if(fromPosition < toPosition) {

        }
        else {

        }

        notifyItemMoved(fromPosition, toPosition); // important for adapter to be aware of this
        return true;

    }

    @Override
    public void onItemDismiss(int position) {
        BlocspotApplication.getSharedDataSource().removePOIFromDB(position);
        notifyItemRemoved(position);
    }

    class PlaceAdapterViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperCallback.ItemTouchHelperViewHolder {

        CheckBox visitCheckbox;
        TextView poiName;
        TextView poiDescription;

        POI poi;

        private SwipeLayout swipeLayout;
        Button noteButton;
        Button assignCatButton;
        Button deletePOIButton;

        // Constructor

        public PlaceAdapterViewHolder(View itemView) {

            super(itemView);
            Log.v(TAG, "PlaceAdapterViewHolder() instantiated");
            visitCheckbox = (CheckBox) itemView.findViewById(R.id.cb_has_visited);
            poiName = (TextView) itemView.findViewById(R.id.tv_poi_name);
            poiDescription = (TextView) itemView.findViewById(R.id.tv_poi_description);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.sl_poi_item);
            noteButton = (Button) itemView.findViewById(R.id.btn_poi_note);
            assignCatButton = (Button) itemView.findViewById(R.id.btn_assign_category);
            deletePOIButton = (Button) itemView.findViewById(R.id.btn_poi_delete);

            // Listeners

            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.ll_poi_bottom_view));

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout swipeLayout) {

                }

                @Override
                public void onOpen(SwipeLayout swipeLayout) {

                }

                @Override
                public void onStartClose(SwipeLayout swipeLayout) {

                }

                @Override
                public void onClose(SwipeLayout swipeLayout) {

                }

                @Override
                public void onUpdate(SwipeLayout swipeLayout, int i, int i1) {

                }

                @Override
                public void onHandRelease(SwipeLayout swipeLayout, float v, float v1) {

                }
            });

            swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // MUST USE this over itemView.setOnClickListener()!

                    if (getAdapterDelegate() != null) {
                        getAdapterDelegate().onItemClicked(PlaceAdapter.this, poi);
                    }
                }
            });

            noteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(getAdapterDelegate() != null) {
                        getAdapterDelegate().onNoteAdded(PlaceAdapter.this, (int) PlaceAdapter.
                                this.getItemId(getAdapterPosition()));
                    }
                    notifyItemChanged(getAdapterPosition());

                    swipeLayout.close();
                }
            });

            assignCatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(getAdapterDelegate() != null) {
                        getAdapterDelegate().onItemAssigned(PlaceAdapter.this,
                                (int) PlaceAdapter.this.getItemId(getAdapterPosition()));
                    }
                    notifyItemChanged(getAdapterPosition());
                    swipeLayout.close(true, true);
                }
            });

            deletePOIButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemDismiss((int) PlaceAdapter.this.getItemId(getAdapterPosition()));
                }
            });

            visitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.v(TAG, PlaceAdapter.class.getSimpleName() + " Visit Checkbox pressed");

                    if (getAdapterDelegate() != null) {
                        getAdapterDelegate().onVisitClicked(PlaceAdapter.this,
                                (int) PlaceAdapter.this.getItemId(getAdapterPosition()), isChecked);
                        Log.v(TAG, "db rowId: " + (int) PlaceAdapter.this.getItemId(getAdapterPosition())
                                + " | " + "Adapter position: " + getAdapterPosition());
                    }
                }
            });

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        void updateViewHolder(POI poi) {
            Log.v(TAG, "updateViewHolder() called");
            this.poi = poi; // this is very important as far as which item is chosen!
            poi = DataSource.poiFromCursor(getCursor());
            poiName.setText(poi.getLocationName());

            if(poi.getDescription().length() == 0) {
                poi.setDescription("How about adding a little note for this location? :)");
                poiDescription.setTextColor(ColorStateList.valueOf(Color.GRAY));
                poiDescription.setTypeface(null, Typeface.ITALIC);
            }

            poiDescription.setText(poi.getDescription());

            if(poi.getCategoryColor() == 0) {
                visitCheckbox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
            }
            else {
                visitCheckbox.setButtonTintList(ColorStateList.valueOf(poi.getCategoryColor()));
            }

            visitCheckbox.setChecked(poi.isHasVisited());
        }

        /**
         *
         * ItemTouchHelperCallback.ItemTouchHelperViewHolder implemented methods
         *
         */

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }

    }
}