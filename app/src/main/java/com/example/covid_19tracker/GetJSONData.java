package com.example.covid_19tracker;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus {IDLE, NOT_INITIALISED, FAILED_OR_EMPTY, OK}

class GetJSONData {
    private static final String TAG = "GetJSONData";
    private DownloadStatus mDownloadStatus;

    interface OnDownloadComplete {
        void onDownloadComplete(String data, DownloadStatus status);
    }

    private OnDownloadComplete mCallback;

    GetJSONData(OnDownloadComplete callback) {
        mDownloadStatus = DownloadStatus.IDLE;
        mCallback = callback;
    }

    void runOnSameThread(String url) {
        Log.d(TAG, "runOnSameThread: starts");
        if (mCallback != null)
            mCallback.onDownloadComplete(downloadingData(url), mDownloadStatus);
        Log.d(TAG, "runOnSameThread: Ends");
    }

    private String downloadingData(String s) {
        Log.d(TAG, "downloadingData: Starts with url:" + s);
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        if (s == null) {
            mDownloadStatus = DownloadStatus.NOT_INITIALISED;
            return null;
        }
        try {
            URL url = new URL(s);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            Log.d(TAG, "downloadingData: Response code was:" + connection.getResponseCode());
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                result.append(line + "\n");
            }
            mDownloadStatus = DownloadStatus.OK;
            Log.d(TAG, "downloadingData: Ends with status:" + mDownloadStatus);
            return result.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadingData: Invalid URL" + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "downloadingData: IOException reading data" + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "downloadingData: Security Exception,needs permisson??" + e.getMessage());
        } finally {
            if (connection != null)
                connection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "downloadingData: Error while closing stream" + e.getMessage());
                }
            }
        }
        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }
}
