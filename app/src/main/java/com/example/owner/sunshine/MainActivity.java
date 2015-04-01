package com.example.owner.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG=MainActivity.class.getSimpleName();
    public static final String DETAIL_FRAGMENT_TAG="DF_TAG";

    private String mLocation;

    private boolean mTwoPane = false;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // This method is called immediately after onCreate()
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);

        //Log.d(LOG_TAG, "Application Created");

        if(findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;

            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Log.d(LOG_TAG, "Application Resumed");
        String location=Utility.getPreferredLocation(this);
        if(location!=null && location.equals(mLocation)) {
            ForecastFragment forecastFragment=(ForecastFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_forecast);
            if(forecastFragment!=null) {
                forecastFragment.onLocationChanged();
            }
            mLocation=location;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // This method is called immediately before onPause()
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d(LOG_TAG, "Application Paused");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d(LOG_TAG, "Application Started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d(LOG_TAG, "Application Stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//      Log.d(LOG_TAG, "Application Destroyed");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void openPreferredLocationInMap() {
        String location=Utility.getPreferredLocation(this);

        // Create Uri for postal location
        Uri geoLocation=Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        // Create intent for launching maps
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't Resolve Map Activity to " + location);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                Intent intent=new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_view_map_location:
                openPreferredLocationInMap();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
