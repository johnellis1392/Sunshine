package com.example.owner.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    // Item view types
    private static final int VIEW_TYPE_TODAY=0;
    private static final int VIEW_TYPE_FUTURE_DAY=1;
    private static final int VIEW_TYPE_COUNT=2;


    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType=getItemViewType(cursor.getPosition());
        int layoutId=-1;

        if(viewType==VIEW_TYPE_TODAY) {
            // Query cursor for current item position, and get
            // the layout identifier for the xml object representing
            // which list view schema to use.
            layoutId=R.layout.list_item_forecast_today;
        } else {
            layoutId=R.layout.list_item_forecast;
        }

        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return position==0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    /*
                This is where we fill-in the views with the contents of the cursor.
             */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder=new ViewHolder(view);

        // Get icon id for cursor
        int weatherId=cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int viewType=getItemViewType(cursor.getPosition());
//        viewHolder.iconView.setImageResource(R.mipmap.ic_launcher);
//        viewHolder.iconView.setImageResource(
//                Utility.getIconResourceForWeatherCondition(weatherId)
//        );

        switch(viewType) {
            case VIEW_TYPE_TODAY:
                viewHolder.iconView.setImageResource(
                        Utility.getArtResourceForWeatherCondition(weatherId)
                );
                break;
            case VIEW_TYPE_FUTURE_DAY:
                viewHolder.iconView.setImageResource(
                        Utility.getIconResourceForWeatherCondition(weatherId)
                );
        }

        // Create friendly date
        long date=cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, date));

        // Get weather forecast
        String weatherForecast=cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(weatherForecast);

        // Get metric preference
        boolean isMetric=Utility.isMetric(context);

        // Get High Temperature
        double high=cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTemperatureView.setText(Utility.formatTemperature(context, high, isMetric));

        // Get Low Temperature
        double low=cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTemperatureView.setText(Utility.formatTemperature(context, low, isMetric));
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTemperatureView;
        public final TextView lowTemperatureView;

        public ViewHolder(View view) {
            iconView=(ImageView) view.findViewById(R.id.list_item_icon);
            dateView=(TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView=(TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTemperatureView=(TextView) view.findViewById(R.id.list_item_high_textview);
            lowTemperatureView=(TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}







