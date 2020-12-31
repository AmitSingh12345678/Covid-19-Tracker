package com.example.covid_19tracker;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetIndiaRecord extends AsyncTask<String, Void, HashMap<String, String>> implements GetJSONData.OnDownloadComplete {
    private static final String TAG = "GetIndiaRecord";
    private HashMap<String, String> mDataHashmap;

    public interface OnDataAvailable {
        void onDataAvailable(HashMap<String, String> dataHashmap, DownloadStatus status);
    }

    private OnDataAvailable mCallback;

    GetIndiaRecord(OnDataAvailable Callback) {
        this.mCallback = Callback;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> dataHashmap) {
        Log.d(TAG, "onPostExecute: Starts");
        if (mCallback != null)
            mCallback.onDataAvailable(mDataHashmap, DownloadStatus.OK);
        Log.d(TAG, "onPostExecute: Ends");
    }

    @Override
    protected HashMap<String, String> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Starts");
        GetJSONData mGetJsonData = new GetJSONData(this);
        mGetJsonData.runOnSameThread(strings[0]);
        Log.d(TAG, "doInBackground: Ends");
        return mDataHashmap;
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "OnDownloadComplete: Starts");
        if (status == DownloadStatus.OK) {
            mDataHashmap = new HashMap<>();
            try {
                JSONObject jsonObject = new JSONObject(data);
//                JSONObject jsonData = jsonObject.getJSONObject("data");
//                JSONArray jsonTimeLine = jsonData.getJSONArray("timeline");
//                JSONObject jsonObject = jsonTimeLine.getJSONObject(0);
                mDataHashmap.put("Affected", jsonObject.getString("cases"));
                mDataHashmap.put("Recovered", jsonObject.getString("recovered"));
                mDataHashmap.put("Deaths", jsonObject.getString("deaths"));
                mDataHashmap.put("Active", jsonObject.getString("active"));
                mDataHashmap.put("New Cases", jsonObject.getString("todayCases"));
                mDataHashmap.put("Critical", jsonObject.getString("critical"));
                mDataHashmap.put("New Deaths", jsonObject.getString("todayDeaths"));

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "OnDownloadComplete: Error while processing Json data. " + e.getMessage());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        Log.d(TAG, "OnDownloadComplete: Ends");
    }
}
