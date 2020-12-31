package com.example.covid_19tracker;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GetCountriesDetails extends AsyncTask<String,Void, ArrayList<HashMap<String,String>>>
                                        implements GetJSONData.OnDownloadComplete {
    private static final String TAG = "GetCountriesDetails";
    private ArrayList<HashMap<String,String>> mCountriesDataList;

    interface OnDataAvailable{
        void OnDataAvailable(ArrayList<HashMap<String,String>> data,DownloadStatus status);
    }
    OnDataAvailable mCallback;

    public GetCountriesDetails(OnDataAvailable callback) {
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
        Log.d(TAG, "onPostExecute: Starts");
        if(mCallback!=null){
            mCallback.OnDataAvailable(mCountriesDataList,DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: Ends");
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: Starts");
        GetJSONData getJsonData=new GetJSONData(this);
        getJsonData.runOnSameThread(strings[0]);
        Log.d(TAG, "doInBackground: Ends");
        return mCountriesDataList;
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "OnDownloadComplete: Starts");
        if(status==DownloadStatus.OK){
            mCountriesDataList =new ArrayList<>();
            try{
                JSONArray jsonArray=new JSONArray(data);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("country",jsonObject.getString("country"));
                    hashMap.put("cases",jsonObject.getString("cases"));
                    hashMap.put("todayCases",jsonObject.getString("todayCases"));
                    hashMap.put("deaths",jsonObject.getString("deaths"));
                    hashMap.put("todayDeaths",jsonObject.getString("todayDeaths"));
                    hashMap.put("recovered",jsonObject.getString("recovered"));
                    hashMap.put("active",jsonObject.getString("active"));
                    Log.d(TAG, "OnDownloadComplete:"+hashMap.get("country"));
                    mCountriesDataList.add(hashMap);
                }
            }catch (JSONException e){
                Log.d(TAG, "OnDownloadComplete: Error while parsing JSON data with error "+e.getMessage());
                e.printStackTrace();
                status=DownloadStatus.FAILED_OR_EMPTY;
            }
        }
        Log.d(TAG, "OnDownloadComplete: Ends");
    }
}
