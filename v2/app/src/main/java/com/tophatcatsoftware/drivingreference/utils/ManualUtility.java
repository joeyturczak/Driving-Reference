package com.tophatcatsoftware.drivingreference.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tophatcatsoftware.drivingreference.data.DrivingContract;
import com.tophatcatsoftware.drivingreference.models.Manual;


/**
 * Copyright (C) 2016 Joey Turczak
 *
 * Helps update Driver Manual data in database
 */
public class ManualUtility {

    public static final int COLUMN_BACKEND_ID = 1;
    public static final int COLUMN_LOCATION = 2;
    public static final int COLUMN_TYPE = 3;
    public static final int COLUMN_LANGUAGE = 4;
    public static final int COLUMN_URL = 5;
    public static final int COLUMN_DISPLAY_NAME = 6;
    public static final int COLUMN_LAST_UPDATED = 7;
    public static final int COLUMN_DOWNLOADED = 8;
    public static final int COLUMN_LAST_PAGE = 9;

    /**
     * Updates downloaded state of a driver manual entry.
     */
    public static void setFileDownloaded(Context context, long id) {

        String where = DrivingContract.DrivingManualEntry.COLUMN_BACKEND_ID + " = " + id;

        ContentResolver contentResolver = context.getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_DOWNLOADED, true);

        contentResolver.update(DrivingContract.DrivingManualEntry.CONTENT_URI, contentValues,
                where, null);
    }

    /**
     * Updates the last page of a driver manual entry.
     */
    public static void setPageNumber(Context context, long id, int pageNumber) {

        String where = DrivingContract.DrivingManualEntry.COLUMN_BACKEND_ID + " = " + id;

        ContentResolver contentResolver = context.getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_LAST_PAGE, pageNumber);

        contentResolver.update(DrivingContract.DrivingManualEntry.CONTENT_URI, contentValues,
                where, null);
    }

    /**
     * Creates a Manual object from cursor data.
     */
    public static Manual getManualFromCursor(Cursor data) {

        long id = data.getLong(COLUMN_BACKEND_ID);
        String location = data.getString(COLUMN_LOCATION);
        String type = data.getString(COLUMN_TYPE);
        String language = data.getString(COLUMN_LANGUAGE);
        String url = data.getString(COLUMN_URL);
        String displayName = data.getString(COLUMN_DISPLAY_NAME);
        int downloaded = data.getInt(COLUMN_DOWNLOADED);
        int lastPage = data.getInt(COLUMN_LAST_PAGE);
        return new Manual(id, location, type, language, url, displayName, downloaded, lastPage);
    }
}
