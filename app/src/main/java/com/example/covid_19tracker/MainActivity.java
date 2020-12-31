package com.example.covid_19tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;

public class MainActivity extends BaseActivity implements GetIndiaRecord.OnDataAvailable {
    private static final String TAG = "MainActivity";
    public static final String DATA_API_URL = "https://coronavirus-19-api.herokuapp.com/countries/india";
    private TextView mActiveCasesInIndia;
    private TextView mRecoveredCasesInIndia;
    private TextView mDeathsInIndia;
    private TextView mAffectedInIndia;
    private TextView mNewCasesInIndia;
    private TextView mNewDeathsInIndia;
    private TextView mCriticalCasesInIndia;
    private HashMap<String, String> mDataHashmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(internetChecker()==false){
            Toast.makeText(this,"No InternetConnection!!",Toast.LENGTH_LONG).show();
        }
        mActiveCasesInIndia = findViewById(R.id.activeCases);
        mRecoveredCasesInIndia = findViewById(R.id.recoveredCases);
        mDeathsInIndia = findViewById(R.id.deaths);
        mAffectedInIndia = findViewById(R.id.header_affected);
        mNewCasesInIndia = findViewById(R.id.newCases);
        mNewDeathsInIndia = findViewById(R.id.newDeaths);
        mCriticalCasesInIndia = findViewById(R.id.critical);
        MaterialCardView mStatesRecord = findViewById(R.id.stateCardView);
        MaterialCardView mCountriesRecord = findViewById(R.id.countryCardView);

        SwipeRefreshLayout swipeRefreshLayout=findViewById(R.id.swipe_refresh_layout);

        GetIndiaRecord getIndiaDetails = new GetIndiaRecord(this);
        getIndiaDetails.execute(DATA_API_URL);

        mStatesRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StateWiseActivity.class);
                if (mDataHashmap != null) {
                    intent.putExtra(DATA_TRANSFER, mDataHashmap);
                }
                startActivity(intent);
            }
        });
        mCountriesRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CountryWiseActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshActivity();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_refresh){
            refreshActivity();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadRecord();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveRecord();
    }

    private void saveRecord() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREV_RECORD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (mDataHashmap != null) {
            editor.putString(PREV_ACTIVE, mDataHashmap.get("Active"));
            editor.putString(PREV_AFFECTED, mDataHashmap.get("Affected"));
            editor.putString(PREV_RECOVERED, mDataHashmap.get("Recovered"));
            editor.putString(PREV_DEATHS, mDataHashmap.get("Deaths"));
            editor.putString(PREV_NEW_CASES, mDataHashmap.get("New Cases"));
            editor.putString(PREV_NEW_DEATHS, mDataHashmap.get("New Deaths"));
            editor.putString(PREV_CRITICAL, mDataHashmap.get("Critical"));
            editor.apply();
        }

    }

    private void loadRecord() {
        boolean loadData = false;
        String data = mAffectedInIndia.getText().toString();
        if (data.endsWith("\n")) {
            loadData = true;
        }
        if (loadData) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREV_RECORD, Context.MODE_PRIVATE);
            mActiveCasesInIndia.append(sharedPreferences.getString(PREV_ACTIVE, "N/A"));
            mAffectedInIndia.append(sharedPreferences.getString(PREV_AFFECTED, "N/A"));
            mCriticalCasesInIndia.append(sharedPreferences.getString(PREV_CRITICAL, "N/A"));
            mDeathsInIndia.append(sharedPreferences.getString(PREV_DEATHS, "N/A"));
            mNewCasesInIndia.append(sharedPreferences.getString(PREV_NEW_CASES, "N/A"));
            mNewDeathsInIndia.append(sharedPreferences.getString(PREV_NEW_DEATHS, "N/A"));
            mRecoveredCasesInIndia.append(sharedPreferences.getString(PREV_RECOVERED, "N/A"));
        }
    }

private void refreshActivity(){
    finish();
  //  overridePendingTransition(1, 1);
    startActivity(getIntent());
  //  overridePendingTransition(1, 1);
}
    @Override
    public void onDataAvailable(HashMap<String, String> dataHashmap, DownloadStatus status) {
        Log.d(TAG, "OnDataAvailable: Starts");
        mDataHashmap = dataHashmap;
        if (dataHashmap!=null) {
            Resources resources = getResources();
            mActiveCasesInIndia.setText(resources.getString(R.string.activeCasesText) + dataHashmap.get("Active"));
            mRecoveredCasesInIndia.setText(resources.getString(R.string.recoveredCasesText) + dataHashmap.get("Recovered"));
            mDeathsInIndia.setText(resources.getString(R.string.deathsText) + dataHashmap.get("Deaths"));
            mAffectedInIndia.setText(resources.getString(R.string.affectedText) + dataHashmap.get("Affected"));
            mNewCasesInIndia.setText(resources.getString(R.string.new_cases_text) + dataHashmap.get("New Cases"));
            mNewDeathsInIndia.setText(resources.getString(R.string.new_deaths_text) + dataHashmap.get("New Deaths"));
            mCriticalCasesInIndia.setText(resources.getString(R.string.critical_cases_text) + dataHashmap.get("Critical"));
            Log.d(TAG, "OnDataAvailable: Ends");
        }
    }
}
