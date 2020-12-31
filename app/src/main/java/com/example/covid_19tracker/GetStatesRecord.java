package com.example.covid_19tracker;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GetStatesRecord extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>
        implements GetJSONData.OnDownloadComplete {
    private static final String TAG = "GetStatesRecord";
    private ArrayList<HashMap<String, String>> mStatesDataList;

    interface OnDataAvailable {
        void onDataAvailable(ArrayList<HashMap<String, String>> statesDataList, DownloadStatus status);
    }

    private OnDataAvailable mCallback;

    GetStatesRecord(OnDataAvailable callback) {
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> statesDataList) {
        Log.d(TAG, "onPostExecute: Starts");
        if (mCallback != null) {
            mCallback.onDataAvailable(statesDataList, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: Ends");
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Starts");
        GetJSONData getJsonData = new GetJSONData(this);
        getJsonData.runOnSameThread(strings[0]);
        Log.d(TAG, "doInBackground: Ends");
        return mStatesDataList;
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "OnDownloadComplete: Starts");
        if (status == DownloadStatus.OK) {
            mStatesDataList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONObject jsonData = jsonObject.getJSONObject("data");
                JSONArray jsonArray = jsonData.getJSONArray("statewise");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    HashMap<String, String> stateData = new HashMap<>();
                    stateData.put("name", object.getString("state"));
                    stateData.put("affected", object.getString("confirmed"));
                    stateData.put("recovered", object.getString("recovered"));
                    stateData.put("deaths", object.getString("deaths"));
                    stateData.put("active", object.getString("active"));
                    Log.d(TAG, "OnDownloadComplete: " + object.getString("state"));
                    mStatesDataList.add(stateData);
                }

            } catch (JSONException e) {
                Log.e(TAG, "OnDownloadComplete: Error while parsing Json:" + e.getMessage());
                e.printStackTrace();
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
            Log.d(TAG, "OnDownloadComplete: Ends");
        }
    }
}
