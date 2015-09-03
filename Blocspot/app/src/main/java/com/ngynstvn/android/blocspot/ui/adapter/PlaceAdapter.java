package com.ngynstvn.android.blocspot.ui.adapter;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
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
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.ui.helper.ItemTouchHelperCallback;

import java.lang.ref.WeakReference;
import java.util.Collections;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceAdapterViewHolder>
        implements ItemTouchHelperCallback.ItemTouchHelperAdapter {

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
     * ItemTouchHelperCallback.ItemTouchHelperAdapter
     *
     */

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        if(fromPosition < toPosition) {
            for(int i = fromPosition; i < toPosition; i++) {
                // allows item to move down
                Collections.swap(BlocspotApplication.getSharedDataSource().getPoiArrayList(), i, i + 1);
            }
        }
        else {
            for(int i = fromPosition; i > toPosition; i--) {
                // allows item to move up
                Collections.swap(BlocspotApplication.getSharedDataSource().getPoiArrayList(), i, i -1);
            }
        }

        notifyItemMoved(fromPosition, toPosition); // important for adapter to be aware of this
        return true;

    }

    @Override
    public void onItemDismiss(int position) {
        BlocspotApplication.getSharedDataSource().getPoiArrayList().remove(position);
        notifyItemRemoved(position);
    }

    class PlaceAdapterViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperCallback.ItemTouchHelperViewHolder {

        CheckBox visitCheckbox;
        TextView poiDistance;
        TextView poiName;
        TextView poiDescription;

        POI poi;

        SwipeLayout swipeLayout;
        Button noteButton;
        Button assignCatButton;
        Button deletePOIButton;

        // Constructor

        public PlaceAdapterViewHolder(final View itemView) {

            super(itemView);
            Log.v(TAG, "PlaceAdapterViewHolder() instantiated");
            visitCheckbox = (CheckBox) itemView.findViewById(R.id.cb_has_visited);
            poiDistance = (TextView) itemView.findViewById(R.id.tv_dist_to_poi);
            poiName = (TextView) itemView.findViewById(R.id.tv_poi_name);
            poiDescription = (TextView) itemView.findViewById(R.id.tv_poi_description);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.sl_poi_item);
            noteButton = (Button) itemView.findViewById(R.id.btn_poi_note);
            assignCatButton = (Button) itemView.findViewById(R.id.btn_assign_category);
            deletePOIButton = (Button) itemView.findViewById(R.id.btn_poi_delete);

            // Listeners

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

            noteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeLayout.close();
                    // Do something here
                }
            });

            assignCatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeLayout.close();
                    // Do something here
                }
            });

            deletePOIButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemDismiss(getAdapterPosition());
                    swipeLayout.close();
                }
            });

            visitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.v(TAG, PlaceAdapter.class.getSimpleName() + " Visit Checkbox pressed");
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterDelegate() != null) {
                        getAdapterDelegate().onItemClicked(PlaceAdapter.this, poi);
                        Log.v(TAG, "getAdapterDelegate() is not null!");
                    }
                }
            });

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        void updateViewHolder(POI poi) {
//            Log.v(TAG, "updateViewHolder() called");
            this.poi = poi; // this is very important as far as which item is chosen!
            poiName.setText(poi.getLocationName());
            poiDescription.setText(poi.getDescription());
            poiDistance.setText(String.valueOf(poi.getDistanceToPOI()) + " mi");
            visitCheckbox.setButtonTintList(ColorStateList.valueOf(poi.getCategoryColor()));
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