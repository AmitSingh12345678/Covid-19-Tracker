package com.example.covid_19tracker;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class CountriesDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = "CountriesDataAdapter";
    //    public static final int TYPE_HEADER = 0;
//    public static final int TYPE_ITEM = 1;
    private ArrayList<HashMap<String, String>> mCountryDataList;
    private ArrayList<HashMap<String, String>> mCountryDataListForSearch;

    public CountriesDataAdapter(ArrayList<HashMap<String, String>> countryDataList) {
        mCountryDataList = countryDataList;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0) {
//            return TYPE_HEADER;
//        }
//        return TYPE_ITEM;
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == TYPE_HEADER) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_for_rv, parent, false);
//            return new headerViewHolder(v);
//        } else {    //(viewType == TYPE_ITEM)
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_data_feeder, parent, false);
        return new viewHolder(v);
        //}
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof viewHolder) {
            viewHolder Holder = (viewHolder) holder;
            if (mCountryDataList != null && mCountryDataList.size() != 0) {
                HashMap<String, String> hashMap = mCountryDataList.get(position);

                Holder.country_name.setText(hashMap.get("country"));
                Holder.country_affected.setText(hashMap.get("cases"));
                Holder.country_today_cases.setText(hashMap.get("todayCases"));
                Holder.country_deaths.setText(hashMap.get("deaths"));
                Holder.country_today_deaths.setText(hashMap.get("todayDeaths"));

                if (hashMap.get("active").equals("null")) {
                    Holder.country_active.setText("N/A");
                } else {
                    Holder.country_active.setText(hashMap.get("active"));
                }

                if (hashMap.get("recovered").equals("null")) {
                    Holder.country_recovered.setText("N/A");
                } else {
                    Holder.country_recovered.setText(hashMap.get("recovered"));
                }
//                String cases = hashMap.get("cases");
//                String todayCases = "\t+" + hashMap.get("todayCases");
//                setDifferentColor(Holder.country_affected, cases, todayCases);
//
//                String deaths = hashMap.get("deaths");
//                String todayDeaths = "\t+" + hashMap.get("todayDeaths");
//                setDifferentColor(Holder.country_deaths, deaths, todayDeaths);
            }

        }
    }

//    void setDifferentColor(TextView textView, String First, String Second) {
//        textView.setText(First);
//        Spannable second = new SpannableString(Second);
//        second.setSpan(new ForegroundColorSpan(Color.parseColor("#FF1100")), 0, second.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//      //  second.setSpan(new RelativeSizeSpan(0.75f), 0, second.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        textView.append(second);
//    }

    @Override
    public int getItemCount() {
        return ((mCountryDataList != null && mCountryDataList.size() != 0) ? mCountryDataList.size() : 0);
    }

    void loadNewData(ArrayList<HashMap<String, String>> countryData) {
        Log.d(TAG, "loadNewData: New data gets loaded");
        if (countryData != null) {
            mCountryDataList = countryData;
            mCountryDataListForSearch = new ArrayList<>(mCountryDataList);
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private Filter mFilter = new Filter() {//this filtering is performed in background thread.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mCountryDataListForSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (HashMap<String, String> hashMap : mCountryDataListForSearch) {
                    if (hashMap.get("country").toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(hashMap);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (mCountryDataList != null && results != null && results.values != null) {//here,we add results.values!=null,because while data is loading,if we press search button,as result.values==null,hence type conversion leads to null pointer exception
                mCountryDataList.clear();
                mCountryDataList.addAll((Collection<? extends HashMap<String, String>>) results.values);
                notifyDataSetChanged();
            }
        }
    };

    static class viewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "viewHolder";
        TextView country_name, country_affected, country_active, country_deaths,
                country_recovered, country_today_cases, country_today_deaths;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: Initialised");
            country_active = itemView.findViewById(R.id.country_active);
            country_affected = itemView.findViewById(R.id.country_affected);
            country_name = itemView.findViewById(R.id.country_name);
            country_deaths = itemView.findViewById(R.id.country_deaths);
            country_recovered = itemView.findViewById(R.id.country_recovered);
            country_today_cases = itemView.findViewById(R.id.country_today_cases);
            country_today_deaths = itemView.findViewById(R.id.country_today_deaths);
        }
    }
//
//    static class headerViewHolder extends RecyclerView.ViewHolder {
//        private static final String TAG = "headerViewHolder";
//        TextView name, affected, deaths, recovered, active;
//
//        public headerViewHolder(@NonNull View itemView) {
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
}
