package com.tophatcatsoftware.drivingreference.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.tophatcatsoftware.drivingreference.BuildConfig;


/**
 * Copyright (C) 2016 Joey Turczak
 */
public class DrivingContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MANUALS = "manuals";
    public static final String PATH_QUESTIONS = "questions";
    public static final String PATH_TESTS = "tests";

    public static final class DrivingManualEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MANUALS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MANUALS;

        public static final String TABLE_NAME = "manuals";

        public static final String COLUMN_BACKEND_ID = "backend_id";

        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_DISPLAY_NAME = "display_name";
        public static final String COLUMN_LAST_UPDATED = "last_updated";
        public static final String COLUMN_DOWNLOADED = "downloaded";
        public static final String COLUMN_LAST_PAGE = "last_page";

        public static Uri buildManualUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class QuestionEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUESTIONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUESTIONS;

        public static final String TABLE_NAME = "questions";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_STATEMENT = "statement";
        public static final String COLUMN_CORRECT_ANSWER = "correct_answer";
        public static final String COLUMN_ALT_ANSWER_A = "alt_answer_a";
        public static final String COLUMN_ALT_ANSWER_B = "alt_answer_b";
        public static final String COLUMN_ALT_ANSWER_C = "alt_answer_c";

        public static Uri buildQuestionUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TestEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TESTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TESTS;

        public static final String TABLE_NAME = "tests";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_QUESTION_IDS = "question_ids";
        public static final String COLUMN_ANSWERS = "answers";
        public static final String COLUMN_CHECKED = "checked";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_COMPLETED = "completed";

        public static Uri buildTestUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
