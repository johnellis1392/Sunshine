package com.example.owner.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.owner.sunshine.data.WeatherContract;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private ForecastAdapter mForecastAdapter;
    private static final String LOG_TAG=ForecastFragment.class.getSimpleName();

    private final int FORECAST_LOADER = 0;

    // [Table_Name].[Column_Name]
    // For specifying a single column of a table.
    private static final String[] FORECAST_COLUMNS= {
            WeatherContract.WeatherEntry.TABLE_NAME + "." +
                    WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    static final int COL_WEATHER_ID=0;
    static final int COL_WEATHER_DATE=1;
    static final int COL_WEATHER_DESC=2;
    static final int COL_WEATHER_MAX_TEMP=3;
    static final int COL_WEATHER_MIN_TEMP=4;
    static final int COL_LOCATION_SETTING=5;
    static final int COL_WEATHER_CONDITION_ID=6;
    static final int COL_COORD_LAT=7;
    static final int COL_COORD_LONG=8;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting=Utility.getPreferredLocation(getActivity());

        String sortOrder=WeatherContract.WeatherEntry.COLUMN_DATE + " ASC ";
        Uri weatherForLocationUri=WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting,
                System.currentTimeMillis()
        );

        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    public ForecastFragment() {
    }

    /**
     * Notes for Loaders:
     *
     * To Create a Loader ID:
     * - Use initLoader() on getLoaderManager()
     *
     * To Create Loader Callbacks:
     * - implements LoaderManager.LoaderCallbacks
     *
     * To Initialize Loader in LoaderManager:
     * - getLoaderManager().initLoader(0, null, this);
     * Specifies ID=0.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor=(Cursor) parent.getItemAtPosition(position);
                if(cursor!=null) {
                    String locationSetting=Utility.getPreferredLocation(getActivity());
                    Intent intent=new Intent(getActivity(),
                            DetailActivity.class)
                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting,
                                    cursor.getLong(COL_WEATHER_DATE)
                            ));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        Log.d(LOG_TAG, "Location: " + location);
        weatherTask.execute(location);
    }

    public void onLocationChanged() {
        updateWeather();
//        getActivity().getSupportLoaderManager()
//                .getLoader(FORECAST_LOADER).reset();
        getLoaderManager().restartLoader(FORECAST_LOADER,
                null, this);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        updateWeather();
//    }
}
