package com.tophatcatsoftware.drivingreference.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.tophatcatsoftware.drivingreference.data.DrivingContract;
import com.tophatcatsoftware.drivingreference.models.Question;


/**
 * Created by joeyturczak on 4/25/16.
 */
public class QuestionUtility {

    public static final int COLUMN_ID = 1;
    public static final int COLUMN_LOCATION = 2;
    public static final int COLUMN_TYPE = 3;
    public static final int COLUMN_LANGUAGE = 4;
    public static final int COLUMN_STATEMENT = 5;
    public static final int COLUMN_CORRECT_ANSWER = 6;
    public static final int COLUMN_ALT_ANSWER_A = 7;
    public static final int COLUMN_ALT_ANSWER_B = 8;
    public static final int COLUMN_ALT_ANSWER_C = 9;

    public static Question getQuestionFromDatabase(Context context, String id) {

        ContentResolver contentResolver = context.getContentResolver();

        String selection = DrivingContract.QuestionEntry.COLUMN_ID + " = '" + id + "'";

        Cursor cursor = contentResolver.query(DrivingContract.QuestionEntry.CONTENT_URI,
                null,
                selection,
                null,
                null);

        Question question = null;

        if(cursor != null && cursor.moveToFirst()) {
            question = getQuestionFromCursor(cursor);
            cursor.close();
        }

        return question;


    }

    public static Question getQuestionFromCursor(Cursor data) {

        String id = data.getString(COLUMN_ID);
        String location = data.getString(COLUMN_LOCATION);
        String type = data.getString(COLUMN_TYPE);
        String language = data.getString(COLUMN_LANGUAGE);
        String statement = data.getString(COLUMN_STATEMENT);
        String correctAnswer = data.getString(COLUMN_CORRECT_ANSWER);
        String incorrectAnswerA = data.getString(COLUMN_ALT_ANSWER_A);
        String incorrectAnswerB = data.getString(COLUMN_ALT_ANSWER_B);
        String incorrectAnswerC = data.getString(COLUMN_ALT_ANSWER_C);
        return new Question(id, location, type, language, statement, correctAnswer, incorrectAnswerA, incorrectAnswerB, incorrectAnswerC);
    }
}
