package com.example.owner.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.owner.sunshine.data.WeatherContract;

/**
* Created by Owner on 3/7/2015.
*/
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG=DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG=" #SunshineApp";
    private String forecastString;

    private ShareActionProvider shareActionProvider;
    private static final int DETAIL_LOADER=0;

    private static final String[] FORECAST_COLUMNS={
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    private static final int COL_WEATHER_ID=0;
    private static final int COL_WEATHER_DATE=1;
    private static final int COL_WEATHER_DESC=2;
    private static final int COL_WEATHER_MAX_TEMP=3;
    private static final int COL_WEATHER_MIN_TEMP=4;
    private static final int COL_WEATHER_HUMIDITY=5;
    private static final int COL_WEATHER_PRESSURE=6;
    private static final int COL_WEATHER_WIND_SPEED=7;
    private static final int COL_WEATHER_DEGREES=8;
    private static final int COL_WEATHER_CONDITION_ID=9;


    private ImageView imageView;
    private TextView friendlyDateView;
    private TextView dateView;
    private TextView descriptionView;
    private TextView highTempView;
    private TextView lowTempView;
    private TextView humidityView;
    private TextView windView;
    private TextView pressureView;


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Log.d(LOG_TAG, "Cursor Value: " + cursor.toString());
        if(cursor!=null && cursor.moveToFirst()) {
            int weatherId=cursor.getInt(COL_WEATHER_CONDITION_ID);
            //imageView.setImageResource(R.mipmap.ic_launcher);
            imageView.setImageResource(
                    Utility.getArtResourceForWeatherCondition(weatherId)
            );

            long date=cursor.getLong(COL_WEATHER_DATE);
            String friendlyDateString=Utility.getDayName(getActivity(), date);
            String dateText=Utility.getFormattedMonthDay(getActivity(), date);
            friendlyDateView.setText(friendlyDateString);
            dateView.setText(dateText);

            String description=cursor.getString(COL_WEATHER_DESC);
            descriptionView.setText(description);

            boolean isMetric=Utility.isMetric(getActivity());
            double high=cursor.getDouble(COL_WEATHER_MAX_TEMP);
            String highString=Utility.formatTemperature(getActivity(), high, isMetric);
            highTempView.setText(highString);

            double low=cursor.getDouble(COL_WEATHER_MIN_TEMP);
            String lowString=Utility.formatTemperature(getActivity(), low, isMetric);
            lowTempView.setText(lowString);

            float humidity=cursor.getFloat(COL_WEATHER_HUMIDITY);
            humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

            float windSpeed=cursor.getFloat(COL_WEATHER_WIND_SPEED);
            float windDirection=cursor.getFloat(COL_WEATHER_DEGREES);
            windView.setText(Utility.getFormattedWind(getActivity(), windSpeed, windDirection));

            float pressure=cursor.getFloat(COL_WEATHER_PRESSURE);
            pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

            forecastString=String.format("%s - %s - %s/%s", dateText, description, high, low);
            if(shareActionProvider!=null) {
                shareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent=getActivity().getIntent();
        if(intent == null || intent.getData() == null) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);
        MenuItem menuItem=menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider=
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is Null.");
        }

        if(forecastString!=null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Not including 'false' in this function call threw a null pointer exception.
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);

        imageView=(ImageView) rootView.findViewById(R.id.detail_image_icon);
        dateView=(TextView) rootView.findViewById(R.id.detail_date_textview);
        friendlyDateView=(TextView) rootView.findViewById(R.id.detail_day_textview);
        descriptionView=(TextView) rootView.findViewById(R.id.detail_forecast_textview);
        highTempView=(TextView) rootView.findViewById(R.id.detail_high_textview);
        lowTempView=(TextView) rootView.findViewById(R.id.detail_low_textview);
        humidityView=(TextView) rootView.findViewById(R.id.detail_humidity_textview);
        windView=(TextView) rootView.findViewById(R.id.detail_windspeed_textview);
        pressureView=(TextView) rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                forecastString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }
}
