package com.example.covid_19tracker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class StatesDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = "StatesDataAdapter";
//    private static final int TYPE_HEADER = 0;
//    private static final int TYPE_ITEM = 1;
    private ArrayList<HashMap<String, String>> mStatesDataList;
    private ArrayList<HashMap<String, String>> mStatesDataListForSearch;

    StatesDataAdapter(ArrayList<HashMap<String, String>> statesDataList) {
        Log.d(TAG, "StatesDataAdapter: Initialised");
        this.mStatesDataList = statesDataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: a new view requested");
//        if (viewType == TYPE_HEADER) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_for_rv, parent, false);
//            return new headerViewHolder(v);
//        } else {   //(viewType==TYPE_ITEM)
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.state_data_feeder, parent, false);
        return new viewHolder(v);
    //}
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Data gets binded to an view");
        if (holder instanceof viewHolder) {
            viewHolder Holder = (viewHolder) holder;
            if (mStatesDataList != null && mStatesDataList.size() != 0) {
                HashMap<String, String> hashMap = mStatesDataList.get(position);//-1 due to header
                Holder.stateName.setText(hashMap.get("name"));
                Holder.stateRecovered.setText(hashMap.get("recovered"));
                Holder.stateDeaths.setText(hashMap.get("deaths"));
                Holder.stateAffected.setText(hashMap.get("affected"));
                Holder.stateActive.setText(hashMap.get("active"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return ((mStatesDataList != null && mStatesDataList.size() != 0) ? mStatesDataList.size() : 0);//+1 for header
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0) {
//            return TYPE_HEADER;
//        }
//        return TYPE_ITEM;
//    }

    void loadNewData(ArrayList<HashMap<String, String>> statesDataList) {
        Log.d(TAG, "loadNewData: New data gets load");
        if(statesDataList!=null) {
            mStatesDataList = statesDataList;
            mStatesDataListForSearch = new ArrayList<>(mStatesDataList);
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mStatesDataListForSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (HashMap<String, String> item : mStatesDataListForSearch) {
                    if (item.get("name").toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (mStatesDataList != null && results != null && results.values != null) {
                mStatesDataList.clear();
                mStatesDataList.addAll((ArrayList) results.values);
                notifyDataSetChanged();
            }
        }
    };
//
//    static class headerViewHolder extends RecyclerView.ViewHolder {
//        private static final String TAG = "stateDataHeaderViewHold";
//        TextView name, affected, deaths, recovered, active;
//
//        headerViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            Log.d(TAG, "headerViewHolder: created");
//            name = itemView.findViewById(R.id.header_name);
//            affected = itemView.findViewById(R.id.header_affected);
//            deaths = itemView.findViewById(R.id.header_deaths);
//            recovered = itemView.findViewById(R.id.header_recovered);
//            active = itemView.findViewById(R.id.header_active);
//        }
//    }

    static class viewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "viewHolder";
        TextView stateName;
        TextView stateAffected;
        TextView stateDeaths;
        TextView stateActive;
        TextView stateRecovered;

        viewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "viewHolder: Starts");
            stateName = itemView.findViewById(R.id.stateName);
            stateActive = itemView.findViewById(R.id.state_active);
            stateAffected = itemView.findViewById(R.id.state_affected);
            stateDeaths = itemView.findViewById(R.id.state_deaths);
            stateRecovered = itemView.findViewById(R.id.state_recovered);
        }
    }
}
