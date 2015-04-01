package com.example.owner.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Owner on 2/28/2015.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=2;
    static final String DATABASE_NAME="weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_LOCATION_TABLE="create table " +
                WeatherContract.LocationEntry.TABLE_NAME + " (" +
                WeatherContract.LocationEntry._ID +
                " integer primary key, " +
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +
                " text unique not null, " +
                WeatherContract.LocationEntry.COLUMN_CITY_NAME +
                " text unique not null, " +
                WeatherContract.LocationEntry.COLUMN_COORD_LAT +
                " real not null, " +
                WeatherContract.LocationEntry.COLUMN_COORD_LONG +
                " real not null " +
                " );";

        db.execSQL(SQL_CREATE_LOCATION_TABLE);

        final String SQL_CREATE_WEATHER_TABLE="create table " +
                WeatherContract.WeatherEntry.TABLE_NAME + "(" +
                WeatherContract.WeatherEntry._ID +
                " integer primary key autoincrement, " +
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                " integer not null, " +
                WeatherContract.WeatherEntry.COLUMN_DATE +
                " integer not null, " +
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC +
                " text not null, " +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID +
                " integer not null, " +

                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP +
                " real not null, " +
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP +
                " real not null, " +

                WeatherContract.WeatherEntry.COLUMN_HUMIDITY +
                " real not null, " +
                WeatherContract.WeatherEntry.COLUMN_PRESSURE +
                " real not null, " +
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED +
                " real not null, " +
                WeatherContract.WeatherEntry.COLUMN_DEGREES +
                " real not null, " +

                " foreign key (" +
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                ") references " +
                WeatherContract.LocationEntry.TABLE_NAME + " (" +
                WeatherContract.LocationEntry._ID + "), " +

                " unique (" + WeatherContract.WeatherEntry.COLUMN_DATE +
                ", " + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                ") on conflict replace);";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + WeatherContract.LocationEntry.TABLE_NAME);
        db.execSQL("drop table if exists " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
