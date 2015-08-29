package com.ngynstvn.android.blocspot.ui.adapter;

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
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.R;

import java.lang.ref.WeakReference;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceAdapterViewHolder>
        implements ItemTouchHelperAdapter {

    // ----- Delegation Interface and Accessors & Mutators ----- //

    public static interface PlaceAdapterDelegate {
        public void onItemClicked(PlaceAdapter placeAdapter, POI poi);
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

    // ----- Class Variables ----- //

    private static final String TAG = "Test (" + PlaceAdapter.class.getSimpleName() + ")";

    // ----- Member variables ----- //

    // ----- PlaceAdapter methods ----- //

    @Override
    public int getItemCount() {
        // Log.v(TAG, "getItemCount() called");
        return BlocspotApplication.getSharedDataSource().getPoiArrayList().size();
    }

    @Override
    public PlaceAdapter.PlaceAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log.v(TAG, "onCreateViewHolder() called");
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.poi_item, parent, false);
        return new PlaceAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(PlaceAdapter.PlaceAdapterViewHolder holder, int position) {
        // Log.v(TAG, "onBindViewHolder() called");
        holder.updateViewHolder(BlocspotApplication.getSharedDataSource().getPoiArrayList().get(position));
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

    class PlaceAdapterViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        CheckBox visitCheckbox;
        TextView poiDistance;
        TextView poiName;
        TextView poiDescription;

        POI poi;

        boolean isItemClickable = true;
        SwipeLayout swipeLayout;
        Button deleteButton;
        Button editButton;

        // Constructor

        public PlaceAdapterViewHolder(final View itemView) {

            super(itemView);
            Log.v(TAG, "PlaceAdapterViewHolder() instantiated");
            visitCheckbox = (CheckBox) itemView.findViewById(R.id.cb_has_visited);
            poiDistance = (TextView) itemView.findViewById(R.id.tv_dist_to_poi);
            poiName = (TextView) itemView.findViewById(R.id.tv_poi_name);
            poiDescription = (TextView) itemView.findViewById(R.id.tv_poi_description);

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

            // Listeners

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterDelegate() != null) {
                        getAdapterDelegate().onItemClicked(PlaceAdapter.this, poi);
                        Log.v(TAG, "getAdapterDelegate() is not null!");
                    }
                }
            });

            visitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.v(TAG, PlaceAdapter.class.getSimpleName() + " Visit Checkbox pressed");
                }
            });

        }

        void updateViewHolder(POI poi) {
//            Log.v(TAG, "updateViewHolder() called");
            this.poi = poi; // this is very important as far as which item is chosen!
            poiName.setText(poi.getLocationName());
            poiDescription.setText(poi.getDescription());
            poiDistance.setText(String.valueOf(poi.getDistanceToPOI()) + " mi");
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