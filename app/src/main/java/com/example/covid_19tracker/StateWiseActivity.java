package com.example.covid_19tracker;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class StateWiseActivity extends BaseActivity implements GetStatesRecord.OnDataAvailable {

    private static final String TAG = "StateWiseActivity";
    private StatesDataAdapter mStateDataAdapter;
    private ShimmerFrameLayout mShimmerFrameLayout;
    private boolean isDataSet = false;
    private RecyclerView recyclerView;
    private TextView affected_data, deaths_data, active_data, recovered_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_wise);
        if(internetChecker()==false){
            Toast.makeText(this,"No InternetConnection!!",Toast.LENGTH_LONG).show();
        }

        affected_data = findViewById(R.id.affected_data);
        deaths_data = findViewById(R.id.deaths_data);
        active_data = findViewById(R.id.active_data);
        recovered_data = findViewById(R.id.recovered_data);

        mShimmerFrameLayout = findViewById(R.id.shimmerViewContainer);

         recyclerView = findViewById(R.id.recyclerView);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            HashMap<String, String> hashMap = (HashMap<String, String>) intent.getExtras().get("DATA_TRANSFER");

            if (hashMap != null) {
                Log.d(TAG, "onCreate: Data gets setted");
                affected_data.setText(hashMap.get("Affected"));
                deaths_data.setText(hashMap.get("Deaths"));
                recovered_data.setText(hashMap.get("Recovered"));
                active_data.setText(hashMap.get("Active"));
                isDataSet = true;
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mStateDataAdapter = new StatesDataAdapter(new ArrayList<HashMap<String, String>>());
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mStateDataAdapter);


        Log.d(TAG, "onCreate: Ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mStateDataAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: Starts");
        super.onResume();
        mShimmerFrameLayout.startShimmer();

        SharedPreferences preferences = getSharedPreferences(PREV_RECORD, Context.MODE_PRIVATE);
        if (!isDataSet) {
            affected_data.setText(preferences.getString(PREV_AFFECTED, "N/A"));
            deaths_data.setText(preferences.getString(PREV_DEATHS, "N/A"));
            active_data.setText(preferences.getString(PREV_ACTIVE, "N/A"));
            recovered_data.setText(preferences.getString(PREV_RECOVERED, "N/A"));
        }

        GetStatesRecord getStateDetails = new GetStatesRecord(StateWiseActivity.this);
        getStateDetails.execute("https://api.rootnet.in/covid19-in/unofficial/covid19india.org/statewise");
        Log.d(TAG, "onResume: Ends");
    }

    @Override
    public void onDataAvailable(ArrayList<HashMap<String, String>> data, DownloadStatus status) {
        Log.d(TAG, "OnDataAvailable: Starts");
        mStateDataAdapter.loadNewData(data);
        if (status == DownloadStatus.OK && data!=null) {
            recyclerView.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mShimmerFrameLayout.stopShimmer();
                    mShimmerFrameLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            },1500);

        } else {
            Log.d(TAG, "OnDataAvailable: Data not available");
        }
        Log.d(TAG, "OnDataAvailable: Ends");
    }
}
