package com.example.covid_19tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
 import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class CountryWiseActivity extends BaseActivity implements GetCountriesDetails.OnDataAvailable {
    private static final String TAG = "CountryWiseActivity";
    private TextView worldAffectedData,worldActiveData,worldRecoveredData,worldDeathsData;
    private CountriesDataAdapter mCountryDataAdapter;
    private ShimmerFrameLayout mShimmerFrameLayout;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_wise);
        if(internetChecker()==false){
            Toast.makeText(this,"No InternetConnection!!",Toast.LENGTH_SHORT).show();
        }
        worldActiveData=findViewById(R.id.world_active_data);
        worldAffectedData=findViewById(R.id.world_affected_Data);
        worldDeathsData=findViewById(R.id.world_deaths_data);
        worldRecoveredData=findViewById(R.id.world_recovered_data);
        mShimmerFrameLayout=findViewById(R.id.shimmerViewContainerCountry);
        mRecyclerView=findViewById(R.id.countryRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCountryDataAdapter=new CountriesDataAdapter(new ArrayList<HashMap<String, String>>());
//        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mCountryDataAdapter);
        Log.d(TAG, "onCreate: Ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem searchItem=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);//this will change  keyboard done button icon.

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mCountryDataAdapter.getFilter().filter(newText);
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
        GetCountriesDetails getCountriesDetails = new GetCountriesDetails(this);
        getCountriesDetails.execute("https://coronavirus-19-api.herokuapp.com/countries");
        Log.d(TAG, "onResume: Ends");
    }

    @Override
    public void OnDataAvailable(ArrayList<HashMap<String, String>> data, DownloadStatus status) {
        Log.d(TAG, "OnDataAvailable: Starts");

        if (status == DownloadStatus.OK && data!=null) {
             mCountryDataAdapter.loadNewData(data);
             HashMap<String,String> worldData=data.get(0);
             worldRecoveredData.setText(worldData.get("recovered"));
             worldDeathsData.setText(worldData.get("deaths"));
             worldAffectedData.setText(worldData.get("cases"));
             worldActiveData.setText(worldData.get("active"));

            mRecyclerView.setVisibility(View.GONE);
            hideWorldData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mShimmerFrameLayout.stopShimmer();
                    mShimmerFrameLayout.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    unHideWorldData();
                }
            },1500);
        }
        else{
            Log.d(TAG, "OnDataAvailable: NOT AVAILABLE");
        }
        Log.d(TAG, "OnDataAvailable: Ends");
    }
    private void hideWorldData(){
        worldActiveData.setVisibility(View.INVISIBLE);
        worldAffectedData.setVisibility(View.INVISIBLE);
        worldRecoveredData.setVisibility(View.INVISIBLE);
        worldDeathsData.setVisibility(View.INVISIBLE);
    }
    private void unHideWorldData(){
        worldActiveData.setVisibility(View.VISIBLE);
        worldAffectedData.setVisibility(View.VISIBLE);
        worldRecoveredData.setVisibility(View.VISIBLE);
        worldDeathsData.setVisibility(View.VISIBLE);
    }
}
