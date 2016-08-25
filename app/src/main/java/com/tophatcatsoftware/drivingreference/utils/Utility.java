package com.tophatcatsoftware.drivingreference.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.format.Time;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreferencelib.DrivingValues;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Copyright (C) 2016 Joey Turczak
 */
public class Utility {

    /**
     * Returns the icon id for the given Manual type.
     */
    public static int getResourceIconId(String type) {

        int id = -1;

        DrivingValues.Type drivingValuesType = DrivingValues.Type.valueOf(type);

        switch (drivingValuesType) {
            case Driver:
                id = R.drawable.ic_directions_car_black_24dp;
                break;
            case Motorcycle:
                id = R.drawable.ic_motorcycle_black_24dp;
                break;
            case Commercial:
                id = R.drawable.ic_local_shipping_black_24dp;
                break;
            case Road_Test:
                id = R.drawable.ic_traffic_black_24dp;
                break;
            case Farm:
                id = R.drawable.ic_local_florist_black_24dp;
                break;
            case School:
                id = R.drawable.ic_directions_bus_black_24dp;
                break;
            case Senior:
                id = R.drawable.ic_traffic_black_24dp;
                break;
            case Supplemental:
                id = R.drawable.ic_accessible_black_24dp;
                break;
            case Teen:
                id = R.drawable.ic_traffic_black_24dp;
                break;
            case Trailer:
                id = R.drawable.ic_local_shipping_black_24dp;
                break;
            default:
                id = R.drawable.ic_traffic_black_24dp;
                break;
        }

        return id;
    }

    /**
     * Returns the index of the currently selected location.
     */
    public static int getSpinnerValue(Context context, String location) {
        location = location.replace("_", " ");
        String[] strings = context.getResources().getStringArray(R.array.locations);
        for(int i = 0; i < strings.length; i++) {
            if(location.equals(strings[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a random number between the min and max values
     */
    public static int getRandomNumber(int min, int max, boolean maxInclusive) {
        if(maxInclusive) {
            max++;
        }
        return new Random().nextInt(max - min) + min;
    }

    /**
     * Checks if there is Internet access
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();

    }

    /**
     * Checks if database any entries.
     * Returns true if any data is found.
     */
    public static boolean isDbEmpty(Context context, Uri uri) {

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if(cursor != null) {
            if(cursor.getCount() > 0) {
                cursor.close();
                return false;
            }
        }

        return true;
    }

    public static String loadJsonFromAsset(Context context, String filename) {
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static String getDateString(long time) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("MM-dd-yy");
        return formatter.format(date);
    }
}
