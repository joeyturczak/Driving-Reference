package com.tophatcatsoftware.drivingreference.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.data.DrivingContract;
import com.tophatcatsoftware.drivingreference.models.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by joeyturczak on 4/24/16.
 */
public class TestUtility {

    public static final int COLUMN_DATE = 1;
    public static final int COLUMN_LOCATION = 2;
    public static final int COLUMN_TYPE = 3;
    public static final int COLUMN_LANGUAGE = 4;
    public static final int COLUMN_QUESTION_IDS = 5;
    public static final int COLUMN_ANSWERS = 6;
    public static final int COLUMN_CHECKED = 7;
    public static final int COLUMN_SCORE = 8;
    public static final int COLUMN_COMPLETED = 9;

    public static final int INCORRECT = 0;
    public static final int CORRECT = 1;

    /**
     * Creates a Test object from cursor data.
     */
    public static Test getTestFromCursor(Cursor data) {

        long date = data.getLong(COLUMN_DATE);
        String location = data.getString(COLUMN_LOCATION);
        String type = data.getString(COLUMN_TYPE);
        String language = data.getString(COLUMN_LANGUAGE);
        String questionIds = data.getString(COLUMN_QUESTION_IDS);
        String answers = data.getString(COLUMN_ANSWERS);
        String checked = data.getString(COLUMN_CHECKED);
        int score = data.getInt(COLUMN_SCORE);
        int completed = data.getInt(COLUMN_COMPLETED);
        return new Test(date, location, type, language, questionIds, answers, checked, score, completed);
    }

    public static String getReviewTestDisplayName(Test test) {

        String date = Utility.getDateString(test.getDate());
        String location = LocationUtility.getShortLocation(test.getLocation());
        String type = getShortType(test.getType());

        return date + " " + location + " " + type + " Test";
    }

    public static String getScoreText(Test test) {
        return test.getScore() + "/" + Test.QUESTION_MAX;
    }

    public static String getNewTestDisplayName(Test test) {

        String location = LocationUtility.getShortLocation(test.getLocation());
        String type = getShortType(test.getType());

        return location + " " + type + " Practice Test";
    }

    public static Test createNewTest(Context context, String location, String type, String language) {

        String selection = context.getString(R.string.manual_table_update_selection,
                DrivingContract.QuestionEntry.COLUMN_LOCATION,
                DrivingContract.QuestionEntry.COLUMN_TYPE,
                DrivingContract.QuestionEntry.COLUMN_LANGUAGE);

        String[] selectionArgs = {location, type, language};

        ContentResolver contentResolver = context.getContentResolver();

        List<String> questionIds = new ArrayList<>();

        Cursor cursor = contentResolver.query(
                DrivingContract.QuestionEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                questionIds.add(cursor.getString(QuestionUtility.COLUMN_ID));
            } while(cursor.moveToNext());
        }

        selectionArgs[0] = "General";

        cursor = contentResolver.query(
                DrivingContract.QuestionEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if(cursor != null && cursor.moveToFirst()) {
            do {
                questionIds.add(cursor.getString(QuestionUtility.COLUMN_ID));
            } while(cursor.moveToNext());

            cursor.close();
        }

        String testQuestions = "";
        String checked = "";

        for(int i = 0; i < 20; i++) {
            int random = Utility.getRandomNumber(0, questionIds.size(), false);

            testQuestions += questionIds.get(random) + Test.DELIMITER;
            questionIds.remove(random);

            checked += Test.NOT_CHECKED + Test.DELIMITER;
        }

        return new Test(0, location, type, language, testQuestions, "", checked, -1, Test.NOT_COMPLETED);
    }

    private static String getShortType(String type) {

        if(type.equals("Commercial")) {
            return "CDL";
        }

        return type;
    }

    public static boolean isChecked(String check) {

        return check.equals(Test.CHECKED);
    }

    public static boolean isCompleted(int completed) {

        return completed == Test.COMPLETED;
    }

    public static int isCompleted(boolean completed) {
        return completed ? Test.COMPLETED : Test.NOT_COMPLETED;
    }

    public static ArrayList<String> getListFromString(String string) {

        String[] array = string.split(Test.DELIMITER_CHARACTER);

        ArrayList<String> list = new ArrayList<>();

        Collections.addAll(list, array);

        return list;
    }

    public static Drawable getCheckDrawable(Context context, int correct) {
        Resources resources = context.getResources();

        Drawable drawable;

        switch (correct) {
            case CORRECT:
                drawable = resources.getDrawable(R.drawable.ic_check_black_24dp);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, resources.getColor(R.color.green_a400));
                break;
            case INCORRECT:
                drawable = resources.getDrawable(R.drawable.ic_close_black_24dp);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, resources.getColor(R.color.red_a400));
                break;
            default:
                drawable = null;
                break;
        }

        return drawable;
    }

    public static String getStringFromList(List<String> list) {

        String returnString = "";

        for(String string : list) {
            returnString += string + Test.DELIMITER;
        }

        return returnString;
    }

    public static void saveTestToDb(Context context, Test test) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DrivingContract.TestEntry.COLUMN_DATE, test.getDate());
        contentValues.put(DrivingContract.TestEntry.COLUMN_LOCATION, test.getLocation());
        contentValues.put(DrivingContract.TestEntry.COLUMN_TYPE, test.getType());
        contentValues.put(DrivingContract.TestEntry.COLUMN_LANGUAGE, test.getLanguage());
        contentValues.put(DrivingContract.TestEntry.COLUMN_QUESTION_IDS, test.getQuestionIds());
        contentValues.put(DrivingContract.TestEntry.COLUMN_ANSWERS, test.getAnswers());
        contentValues.put(DrivingContract.TestEntry.COLUMN_CHECKED, test.getChecked());
        contentValues.put(DrivingContract.TestEntry.COLUMN_SCORE, test.getScore());
        contentValues.put(DrivingContract.TestEntry.COLUMN_COMPLETED, test.getCompleted());

        contentResolver.insert(DrivingContract.TestEntry.CONTENT_URI, contentValues);
    }

    public static void removeTestFromDb(Context context, Test test) {
        ContentResolver contentResolver = context.getContentResolver();

        long date = test.getDate();

        String where = DrivingContract.TestEntry.COLUMN_DATE + "=?";

        int rows = contentResolver.delete(DrivingContract.TestEntry.CONTENT_URI, where, new String[] {Long.toString(date)});

        Log.d("Deleted test entries", String.valueOf(rows));
    }

    public static void setTestInProgress(Context context, boolean inProgress) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("InProgress", inProgress);
        editor.apply();
    }

    public static boolean getTestInProgress(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean("InProgress", false);
    }
}
