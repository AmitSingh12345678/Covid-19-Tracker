package com.example.covid_19tracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public static final String DATA_TRANSFER = "DATA_TRANSFER";
    public static final String PREV_RECORD = "PREV_RECORD";
    public static final String PREV_ACTIVE = "PREV_ACTIVE";
    public static final String PREV_RECOVERED = "PREV_RECOVERED";
    public static final String PREV_DEATHS = "PREV_DEATHS";
    public static final String PREV_CRITICAL = "PREV_CRITICAL";
    public static final String PREV_NEW_CASES = "PREV_NEW_CASES";
    public static final String PREV_NEW_DEATHS = "PREV_NEW_DEATHS";
    public static final String PREV_AFFECTED = "PREV_AFFECTED";

    public boolean internetChecker() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
