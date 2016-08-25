package com.tophatcatsoftware.drivingreference.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Copyright (C) 2016 Joey Turczak
 */
public class DrivingDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "drivingreference.db";

    public DrivingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MANUAL_TABLE = "CREATE TABLE " + DrivingContract.DrivingManualEntry.TABLE_NAME + " (" +
                DrivingContract.DrivingManualEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                DrivingContract.DrivingManualEntry.COLUMN_BACKEND_ID + " INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                DrivingContract.DrivingManualEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                DrivingContract.DrivingManualEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                DrivingContract.DrivingManualEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
                DrivingContract.DrivingManualEntry.COLUMN_URL + " TEXT NOT NULL, " +
                DrivingContract.DrivingManualEntry.COLUMN_DISPLAY_NAME + " TEXT NOT NULL, " +
                DrivingContract.DrivingManualEntry.COLUMN_LAST_UPDATED + " INTEGER NOT NULL, " +
                DrivingContract.DrivingManualEntry.COLUMN_DOWNLOADED + " INTEGER NOT NULL, " +
                DrivingContract.DrivingManualEntry.COLUMN_LAST_PAGE + " INTEGER NOT NULL" +
                " );";

        final String SQL_CREATE_QUESTION_TABLE = "CREATE TABLE " + DrivingContract.QuestionEntry.TABLE_NAME + " (" +
                DrivingContract.QuestionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                DrivingContract.QuestionEntry.COLUMN_ID + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                DrivingContract.QuestionEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                DrivingContract.QuestionEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                DrivingContract.QuestionEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
                DrivingContract.QuestionEntry.COLUMN_STATEMENT + " TEXT NOT NULL, " +
                DrivingContract.QuestionEntry.COLUMN_CORRECT_ANSWER + " TEXT NOT NULL, " +
                DrivingContract.QuestionEntry.COLUMN_ALT_ANSWER_A + " TEXT NOT NULL, " +
                DrivingContract.QuestionEntry.COLUMN_ALT_ANSWER_B + " TEXT NOT NULL, " +
                DrivingContract.QuestionEntry.COLUMN_ALT_ANSWER_C + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_TEST_TABLE = "CREATE TABLE " + DrivingContract.TestEntry.TABLE_NAME + " (" +
                DrivingContract.TestEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                DrivingContract.TestEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                DrivingContract.TestEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                DrivingContract.TestEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                DrivingContract.TestEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
                DrivingContract.TestEntry.COLUMN_QUESTION_IDS + " TEXT NOT NULL, " +
                DrivingContract.TestEntry.COLUMN_ANSWERS + " TEXT NOT NULL, " +
                DrivingContract.TestEntry.COLUMN_CHECKED + " TEXT NOT NULL, " +
                DrivingContract.TestEntry.COLUMN_SCORE + " INTEGER NOT NULL, " +
                DrivingContract.TestEntry.COLUMN_COMPLETED + " INTEGER NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_MANUAL_TABLE);
        db.execSQL(SQL_CREATE_QUESTION_TABLE);
        db.execSQL(SQL_CREATE_TEST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DrivingContract.DrivingManualEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DrivingContract.QuestionEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DrivingContract.TestEntry.TABLE_NAME);
        onCreate(db);
    }
}
