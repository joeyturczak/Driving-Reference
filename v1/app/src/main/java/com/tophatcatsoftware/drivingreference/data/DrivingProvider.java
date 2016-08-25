package com.tophatcatsoftware.drivingreference.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Copyright (C) 2016 Joey Turczak
 */
public class DrivingProvider extends ContentProvider {

    static final int MANUALS = 100;
    static final int QUESTIONS = 200;
    static final int TESTS = 300;

    private DrivingDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DrivingContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DrivingContract.PATH_MANUALS, MANUALS);
        matcher.addURI(authority, DrivingContract.PATH_QUESTIONS, QUESTIONS);
        matcher.addURI(authority, DrivingContract.PATH_TESTS, TESTS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DrivingDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MANUALS:
                return DrivingContract.DrivingManualEntry.CONTENT_TYPE;
            case QUESTIONS:
                return DrivingContract.QuestionEntry.CONTENT_TYPE;
            case TESTS:
                return DrivingContract.TestEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);

        Cursor cursor;
        switch (match) {
            case MANUALS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        DrivingContract.DrivingManualEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case QUESTIONS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        DrivingContract.QuestionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TESTS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        DrivingContract.TestEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MANUALS: {
                long _id = db.insert(DrivingContract.DrivingManualEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DrivingContract.DrivingManualEntry.buildManualUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case QUESTIONS: {
                long _id = db.insert(DrivingContract.QuestionEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DrivingContract.QuestionEntry.buildQuestionUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TESTS: {
                long _id = db.insert(DrivingContract.TestEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DrivingContract.TestEntry.buildTestUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) {
            selection = "1";
        }
        switch (match) {
            case MANUALS:
                rowsDeleted = db.delete(DrivingContract.DrivingManualEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case QUESTIONS:
                rowsDeleted = db.delete(DrivingContract.QuestionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TESTS:
                rowsDeleted = db.delete(DrivingContract.TestEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MANUALS:
                rowsUpdated = db.update(DrivingContract.DrivingManualEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case QUESTIONS:
                rowsUpdated = db.update(DrivingContract.QuestionEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TESTS:
                rowsUpdated = db.update(DrivingContract.TestEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            case MANUALS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DrivingContract.DrivingManualEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case QUESTIONS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DrivingContract.QuestionEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case TESTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DrivingContract.TestEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            default:
                return super.bulkInsert(uri, values);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }
}
