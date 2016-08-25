package com.tophatcatsoftware.drivingreference.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.backend.drivingReferenceApi.model.DrivingManual;
import com.tophatcatsoftware.drivingreference.data.DrivingContract;
import com.tophatcatsoftware.drivingreference.models.Manual;
import com.tophatcatsoftware.drivingreference.models.Question;
import com.tophatcatsoftware.drivingreference.utils.EndpointsAsyncTask;
import com.tophatcatsoftware.drivingreference.utils.ManualUtility;
import com.tophatcatsoftware.drivingreference.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

/**
 * Copyright (C) 2016 Joey Turczak
 *
 *
 * Downloads content from GCE backend
 */
public class UpdateDataIntentService extends IntentService {

    public static final String ACTION_UPDATE_MANUALS = "ACTION_UPDATE_MANUALS";
    public static final String ACTION_UPDATE_QUESTIONS = "ACTION_UPDATE_QUESTIONS";

    private ContentResolver mContentResolver;

    public UpdateDataIntentService() {
        super("UpdateDataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mContentResolver = getContentResolver();

        if(intent != null) {
            String action = intent.getAction();
            boolean isDbEmpty = intent.getBooleanExtra(getString(R.string.database_empty_intent_key), true);

            switch (action) {
                case ACTION_UPDATE_MANUALS:
                    if(isDbEmpty) {
                        downloadAllManuals();
                    } else {
                        downloadNewManuals();
                    }
                    break;
                case ACTION_UPDATE_QUESTIONS:
                    List<Question> questions = getJsonQuestions();

                    if(isDbEmpty) {
                        storeAllQuestions(questions);
                    } else {
                        checkAndUpdateQuestions(questions);
                    }
            }
        }
    }

    /**
     * Downloads all available manuals from backend
     */
    private void downloadAllManuals() {

        List<DrivingManual> drivingManuals = new ArrayList<>();

        try {
            drivingManuals = new EndpointsAsyncTask().execute(EndpointsAsyncTask.DOWNLOAD_ALL).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        saveDrivingManuals(drivingManuals);

    }

    /**
     * Downloads only manuals that do not exist in the database or any that are updated.
     */
    private void downloadNewManuals() {

        // Get the last time something was updated
        String selection = DrivingContract.DrivingManualEntry.COLUMN_LAST_UPDATED;

        String sortOrder = DrivingContract.DrivingManualEntry.COLUMN_LAST_UPDATED + getString(R.string.sort_order_desc);

        Cursor cursor = mContentResolver.query(DrivingContract.DrivingManualEntry.CONTENT_URI, null, selection, null, sortOrder);

        if(cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();

            long mostRecent = cursor.getLong(ManualUtility.COLUMN_LAST_UPDATED);

            cursor.close();

            List<DrivingManual> drivingManuals = new ArrayList<>();

            // Get any entries that are newer from the datastore
            try {
                drivingManuals = new EndpointsAsyncTask().execute(EndpointsAsyncTask.DOWNLOAD_AFTER_DATE, mostRecent).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (drivingManuals != null) {
                // Update any corresponding data in the database
                drivingManuals = updateOldEntries(drivingManuals);

                // Save the rest
                saveDrivingManuals(drivingManuals);
            }
        }
    }

    /**
     * Stores downloaded data in database.
     */
    private void saveDrivingManuals(List<DrivingManual> drivingManuals) {

        if(drivingManuals == null) {
            return;
        }

        Vector<ContentValues> cVVector = new Vector<ContentValues>();

        long id;
        String location;
        String type;
        String language;
        String url;
        String displayName;
        long lastUpdated;

        for(DrivingManual drivingManual : drivingManuals) {

            id = drivingManual.getId();
            location = drivingManual.getLocation();
            type = drivingManual.getType();
            language = drivingManual.getLanguage();
            url = drivingManual.getUrl();
            displayName = drivingManual.getDisplayName();
            lastUpdated = drivingManual.getLastUpdated();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_BACKEND_ID, id);
            contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_LOCATION, location);
            contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_TYPE, type);
            contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_LANGUAGE, language);
            contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_URL, url);
            contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_DISPLAY_NAME, displayName);
            contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_LAST_UPDATED, lastUpdated);
            contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_DOWNLOADED, false);
            contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_LAST_PAGE, 0);

            cVVector.add(contentValues);
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = cVVector.toArray(new ContentValues[cVVector.size()]);
            mContentResolver.bulkInsert(DrivingContract.DrivingManualEntry.CONTENT_URI, cvArray);
        }
    }

    /**
     * Updates database entries with new data.
     */
    private List<DrivingManual> updateOldEntries(List<DrivingManual> drivingManuals) {

        String selection = getString(R.string.manual_table_update_selection,
                DrivingContract.DrivingManualEntry.COLUMN_LOCATION,
                DrivingContract.DrivingManualEntry.COLUMN_TYPE,
                DrivingContract.DrivingManualEntry.COLUMN_LANGUAGE);

        for(DrivingManual drivingManual : drivingManuals) {

            String location = drivingManual.getLocation();
            String type = drivingManual.getType();
            String language = drivingManual.getLanguage();

            String[] selectionArgs = {location, type, language};

            Cursor cursor = mContentResolver.query(DrivingContract.DrivingManualEntry.CONTENT_URI, null, selection, selectionArgs, null);

            if(cursor != null && cursor.moveToFirst()) {
                // Get database _ID
                int id = cursor.getInt(0);

                // Delete old file if it exists
                if(cursor.getInt(ManualUtility.COLUMN_DOWNLOADED) == Manual.DOWNLOADED) {
                    String fileName = cursor.getLong(ManualUtility.COLUMN_BACKEND_ID) + getString(R.string.pdf_file_extension);
                    File file = new File(getFilesDir(), fileName);
                    if(file.exists()) {
                        file.delete();
                    }

                }

                String select = DrivingContract.DrivingManualEntry._ID + " = ?";
                String[] selectArgs = {String.valueOf(id)};

                ContentValues contentValues = new ContentValues();
                contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_URL, drivingManual.getUrl());
                contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_DISPLAY_NAME, drivingManual.getDisplayName());
                contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_LAST_UPDATED, drivingManual.getLastUpdated());
                contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_BACKEND_ID, drivingManual.getId());

                contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_DOWNLOADED, false);
                contentValues.put(DrivingContract.DrivingManualEntry.COLUMN_LAST_PAGE, 0);

                mContentResolver.update(DrivingContract.DrivingManualEntry.CONTENT_URI, contentValues, select, selectArgs);

                drivingManuals.remove(drivingManual);

                cursor.close();
            }

        }

        return drivingManuals;
    }

    private void storeAllQuestions(List<Question> questions) {

        Vector<ContentValues> cVVector = new Vector<ContentValues>();

        String id;
        String location;
        String type;
        String language;
        String statement;
        String correctAnswer;
        String incorrectAnswerA;
        String incorrectAnswerB;
        String incorrectAnswerC;

        for(Question question : questions) {

            id = question.getId();
            location = question.getLocation();
            type = question.getType();
            language = question.getLanguage();
            statement = question.getStatement();
            correctAnswer = question.getCorrectAnswer();
            incorrectAnswerA = question.getIncorrectAnswerA();
            incorrectAnswerB = question.getIncorrectAnswerB();
            incorrectAnswerC = question.getIncorrectAnswerC();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DrivingContract.QuestionEntry.COLUMN_ID, id);
            contentValues.put(DrivingContract.QuestionEntry.COLUMN_LOCATION, location);
            contentValues.put(DrivingContract.QuestionEntry.COLUMN_TYPE, type);
            contentValues.put(DrivingContract.QuestionEntry.COLUMN_LANGUAGE, language);
            contentValues.put(DrivingContract.QuestionEntry.COLUMN_STATEMENT, statement);
            contentValues.put(DrivingContract.QuestionEntry.COLUMN_CORRECT_ANSWER, correctAnswer);
            contentValues.put(DrivingContract.QuestionEntry.COLUMN_ALT_ANSWER_A, incorrectAnswerA);
            contentValues.put(DrivingContract.QuestionEntry.COLUMN_ALT_ANSWER_B, incorrectAnswerB);
            contentValues.put(DrivingContract.QuestionEntry.COLUMN_ALT_ANSWER_C, incorrectAnswerC);

            cVVector.add(contentValues);
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = cVVector.toArray(new ContentValues[cVVector.size()]);
            mContentResolver.bulkInsert(DrivingContract.QuestionEntry.CONTENT_URI, cvArray);
        }
    }

    private void checkAndUpdateQuestions(List<Question> questions) {
        // Search for questions that match and remove them from the list
        // Send list of questions left over to storeAllQuestions()
        // Find any that exist in db but not in this list and delete them

        // Get all question ids from database
        List<String> dbIds = new ArrayList<>();

        Cursor cursor = mContentResolver.query(DrivingContract.QuestionEntry.CONTENT_URI, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
            do {
                dbIds.add(cursor.getString(1));
            } while (cursor.moveToNext());

            cursor.close();
        }

        List<Question> questionsToStore = new ArrayList<>();

        for(Question question : questions) {
            String currentId = question.getId();

            if(dbIds.contains(currentId)) {
                dbIds.remove(currentId);
            } else {
                questionsToStore.add(question);
            }
        }

        removeQuestionsFromDb(dbIds);
        storeAllQuestions(questionsToStore);
    }

    private void removeQuestionsFromDb(List<String> ids) {

        if(ids.isEmpty()) {
            return;
        }

        String where;

        for(String id : ids) {
            where = DrivingContract.QuestionEntry.COLUMN_ID + " = " + id;
            mContentResolver.delete(DrivingContract.QuestionEntry.CONTENT_URI, where, null);
        }
    }

    private List<Question> getJsonQuestions() {

        List<Question> questions = new ArrayList<>();

        try {
            String jsonString = Utility.loadJsonFromAsset(this, "questions.json");

            JSONObject jsonResponse = new JSONObject(jsonString);

            JSONArray jsonQuestions = jsonResponse.getJSONArray("questions");

            String id;
            String location;
            String language;
            String type;
            String statement;
            String correctAnswer;
            String incorrectAnswerA;
            String incorrectAnswerB;
            String incorrectAnswerC;

            JSONObject currentObject;

            for(int i = 0; i < jsonQuestions.length(); i++) {
                currentObject = jsonQuestions.getJSONObject(i);

                id = currentObject.getString("id");
                location = currentObject.getString("location");
                language = currentObject.getString("language");
                type = currentObject.getString("type");
                statement = currentObject.getString("statement");
                correctAnswer = currentObject.getString("correct_answer");
                incorrectAnswerA = currentObject.getString("incorrect_answer_a");
                incorrectAnswerB = currentObject.getString("incorrect_answer_b");
                incorrectAnswerC = currentObject.getString("incorrect_answer_c");
                questions.add(new Question(id, location, type, language, statement, correctAnswer, incorrectAnswerA, incorrectAnswerB, incorrectAnswerC));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return questions;
    }
}
