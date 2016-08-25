package com.tophatcatsoftware.drivingreference.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joeyturczak on 4/24/16.
 */
public class Test implements Parcelable {

    public static final String CHECKED = "true";
    public static final String NOT_CHECKED = "false";
    public static final int NOT_COMPLETED = 0;
    public static final int COMPLETED = 1;


    public static final String DELIMITER = "|";
    public static final String DELIMITER_CHARACTER = "[|]";
    public static final int QUESTION_MAX = 20;

    long date;
    String location;
    String type;
    String language;
    String questionIds;
    String answers;
    String checked;
    int score;
    int completed;

    public Test(long date, String location, String type, String language, String questionIds, String answers, String checked, int score, int completed) {
        this.date = date;
        this.location = location;
        this.type = type;
        this.language = language;
        this.questionIds = questionIds;
        this.answers = answers;
        this.checked = checked;
        this.score = score;
        this.completed = completed;
    }


    protected Test(Parcel in) {
        date = in.readLong();
        location = in.readString();
        type = in.readString();
        language = in.readString();
        questionIds = in.readString();
        answers = in.readString();
        checked = in.readString();
        score = in.readInt();
        completed = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date);
        dest.writeString(location);
        dest.writeString(type);
        dest.writeString(language);
        dest.writeString(questionIds);
        dest.writeString(answers);
        dest.writeString(checked);
        dest.writeInt(score);
        dest.writeInt(completed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(String questionIds) {
        this.questionIds = questionIds;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }
}
