package com.ngynstvn.android.blocspot.ui.adapter;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.ngynstvn.android.blocspot.BlocspotApplication;
import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.POI;
import com.ngynstvn.android.blocspot.ui.helper.ItemTouchHelperCallback;
import com.squareup.picasso.Picasso;

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

    public interface PlaceAdapterDelegate {
        void onItemClicked(PlaceAdapter placeAdapter, POI poi);
        void onItemAssigned(PlaceAdapter placeAdapter, int rowId);
        void onNoteClicked(PlaceAdapter placeAdapter, int rowId);
        void onVisitClicked(PlaceAdapter placeAdapter, int rowId, boolean isChecked);
        void onVisiteSiteClicked(PlaceAdapter placeAdapter, POI poi);
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

        Button visitSite;
        Button noteButton;
        Button assignCatButton;
        Button deletePOIButton;

        ImageView poiRating;

        // Constructor

        public PlaceAdapterViewHolder(View itemView) {

            super(itemView);
            Log.v(TAG, "PlaceAdapterViewHolder() instantiated");
            visitCheckbox = (CheckBox) itemView.findViewById(R.id.cb_has_visited);
            poiName = (TextView) itemView.findViewById(R.id.tv_poi_name);
            poiDescription = (TextView) itemView.findViewById(R.id.tv_poi_description);

            visitSite = (Button) itemView.findViewById(R.id.btn_poi_visit_site);
            noteButton = (Button) itemView.findViewById(R.id.btn_poi_note);
            assignCatButton = (Button) itemView.findViewById(R.id.btn_assign_category);
            deletePOIButton = (Button) itemView.findViewById(R.id.btn_poi_delete);

            poiRating = (ImageView) itemView.findViewById(R.id.iv_poi_rating);

            // Listeners

            noteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(getAdapterDelegate() != null) {
                        getAdapterDelegate().onNoteClicked(PlaceAdapter.this, (int) PlaceAdapter.
                                this.getItemId(getAdapterPosition()));
                    }
                    notifyItemChanged(getAdapterPosition());
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
                }
            });

            deletePOIButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemDismiss((int) PlaceAdapter.this.getItemId(getAdapterPosition()));
                }
            });

            visitSite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterDelegate() != null) {
                        getAdapterDelegate().onVisiteSiteClicked(PlaceAdapter.this, poi);
                    }
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterDelegate() != null) {
                        getAdapterDelegate().onItemClicked(PlaceAdapter.this, poi);
                    }
                }
            });

        }

        void updateViewHolder(POI poi) {
            Log.v(TAG, "updateViewHolder() called");
            this.poi = poi; // this is very important as far as which item is chosen!
            poi = DataSource.poiFromCursor(getCursor());
            poiName.setText(poi.getLocationName());

            if(poi.getDescription().length() == 0) {
                poiDescription.setText("Add a note to this point of interest.");
                poiDescription.setTextColor(ColorStateList.valueOf(Color.GRAY));
                poiDescription.setTypeface(null, Typeface.ITALIC);
            }
            else {
                poiDescription.setText(poi.getDescription());
                poiDescription.setTextColor(ColorStateList.valueOf(Color.BLACK));
                poiDescription.setTypeface(null, Typeface.NORMAL);
            }

            if(poi.getCategoryColor() == 0) {
                if(Build.VERSION.SDK_INT < 21) {
                    // For devices not on Lollipop
                    visitCheckbox.setBackgroundColor(Color.BLACK);
                }
                else {
                    // Lollipop compatibility
                    visitCheckbox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                }
            }
            else {
                if(Build.VERSION.SDK_INT < 21) {
                    visitCheckbox.setBackgroundColor(poi.getCategoryColor());
                }
                else {
                    visitCheckbox.setButtonTintList(ColorStateList.valueOf(poi.getCategoryColor()));
                }
            }

            visitCheckbox.setChecked(poi.isHasVisited());

            if(poiRating != null) {
                Picasso.with(BlocspotApplication.getSharedInstance()).load(poi.getRatingImgURL()).into(poiRating);
            }

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