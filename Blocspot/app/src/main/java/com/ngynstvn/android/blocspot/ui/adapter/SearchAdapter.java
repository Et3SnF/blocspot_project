package com.ngynstvn.android.blocspot.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ngynstvn.android.blocspot.R;
import com.ngynstvn.android.blocspot.api.DataSource;
import com.ngynstvn.android.blocspot.api.model.POI;

public class SearchAdapter extends CursorRecyclerViewAdapter<SearchAdapter.SearchAdapterViewHolder> {

    public SearchAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public SearchAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new SearchAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchAdapterViewHolder viewHolder, Cursor cursor) {
        viewHolder.update(DataSource.poiFromCursor(cursor));
    }

    class SearchAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public SearchAdapterViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_search_text);
        }

        void update(POI poi) {
            textView.setText(poi.getLocationName());
        }
    }

}
