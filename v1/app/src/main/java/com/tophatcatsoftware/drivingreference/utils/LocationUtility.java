package com.tophatcatsoftware.drivingreference.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreferencelib.DrivingValues;


/**
 * Copyright (C) 2016 Joey Turczak
 *
 * Helps retrieve and set the selected location to SharedPreferences
 */
public class LocationUtility {

    /**
     * Gets stored location from SharedPreferences
     */
    public static String getLocationConfig(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(context.getString(R.string.shared_preference_location_key), DrivingValues.Location.California.toString());
    }

    /**
     * Sets location value in SharedPreferences
     */
    public static void setLocationConfig(Context context, String location) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        location = location.replace(" ", "_");
        editor.putString(context.getString(R.string.shared_preference_location_key), location);
        editor.apply();
    }

    /**
     * Returns the image icon resource id for the given location
     */
    public static int getLocationImageResource(Context context, String location) {

        String resourceName = location;

        resourceName = resourceName.replace(" ", "_");

        resourceName = resourceName.toLowerCase();

        return context.getResources().getIdentifier(resourceName, context.getString(R.string.drawable_identifier), context.getString(R.string.package_name));
    }

    public static String getShortLocation(String location) {

        String shortLocation = "";

        switch (location) {
            case "Alabama":
                shortLocation = "AL";
                break;
            case "Alaska":
                shortLocation = "AK";
                break;
            case "Arizona":
                shortLocation = "AZ";
                break;
            case "Arkansas":
                shortLocation = "AR";
                break;
            case "California":
                shortLocation = "CA";
                break;
            case "Colorado":
                shortLocation = "CO";
                break;
            case "Connecticut":
                shortLocation = "CT";
                break;
            case "Delaware":
                shortLocation = "DE";
                break;
            case "District_of_Columbia":
                shortLocation = "DC";
                break;
            case "Florida":
                shortLocation = "FL";
                break;
            case "Georgia":
                shortLocation = "GA";
                break;
            case "Hawaii":
                shortLocation = "HI";
                break;
            case "Idaho":
                shortLocation = "ID";
                break;
            case "Illinois":
                shortLocation = "IL";
                break;
            case "Indiana":
                shortLocation = "IN";
                break;
            case "Iowa":
                shortLocation = "IA";
                break;
            case "Kansas":
                shortLocation = "KS";
                break;
            case "Kentucky":
                shortLocation = "KY";
                break;
            case "Louisiana":
                shortLocation = "LA";
                break;
            case "Maine":
                shortLocation = "ME";
                break;
            case "Maryland":
                shortLocation = "MD";
                break;
            case "Massachusetts":
                shortLocation = "MA";
                break;
            case "Michigan":
                shortLocation = "MI";
                break;
            case "Minnesota":
                shortLocation = "MN";
                break;
            case "Mississippi":
                shortLocation = "MS";
                break;
            case "Missouri":
                shortLocation = "MO";
                break;
            case "Montana":
                shortLocation = "MT";
                break;
            case "Nebraska":
                shortLocation = "NE";
                break;
            case "Nevada":
                shortLocation = "NV";
                break;
            case "New_Hampshire":
                shortLocation = "NH";
                break;
            case "New_Jersey":
                shortLocation = "NJ";
                break;
            case "New_Mexico":
                shortLocation = "NM";
                break;
            case "New_York":
                shortLocation = "NY";
                break;
            case "North_Carolina":
                shortLocation = "NC";
                break;
            case "North_Dakota":
                shortLocation = "ND";
                break;
            case "Ohio":
                shortLocation = "OH";
                break;
            case "Oklahoma":
                shortLocation = "OK";
                break;
            case "Oregon":
                shortLocation = "OR";
                break;
            case "Pennsylvania":
                shortLocation = "PA";
                break;
            case "Rhode_Island":
                shortLocation = "RI";
                break;
            case "South_Carolina":
                shortLocation = "SC";
                break;
            case "South_Dakota":
                shortLocation = "SD";
                break;
            case "Tennessee":
                shortLocation = "TN";
                break;
            case "Texas":
                shortLocation = "TX";
                break;
            case "Utah":
                shortLocation = "UT";
                break;
            case "Vermont":
                shortLocation = "VT";
                break;
            case "Virginia":
                shortLocation = "VA";
                break;
            case "Washington":
                shortLocation = "WA";
                break;
            case "West_Virginia":
                shortLocation = "WV";
                break;
            case "Wisconsin":
                shortLocation = "WI";
                break;
            case "Wyoming":
                shortLocation = "WY";
                break;
        }

        return shortLocation;
    }
}
