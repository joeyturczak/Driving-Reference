package com.tophatcatsoftware.drivingreference.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright (C) 2016 Joey Turczak
 */
public class Question implements Parcelable {

    String id;
    String location;
    String type;
    String language;
    String statement;
    String correctAnswer;
    String incorrectAnswerA;
    String incorrectAnswerB;
    String incorrectAnswerC;

    public Question(String id, String location, String type, String language, String statement,
                    String correctAnswer, String incorrectAnswerA, String incorrectAnswerB, String incorrectAnswerC) {
        this.id = id;
        this.location = location;
        this.type = type;
        this.language = language;
        this.statement = statement;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswerA = incorrectAnswerA;
        this.incorrectAnswerB = incorrectAnswerB;
        this.incorrectAnswerC = incorrectAnswerC;
    }

    protected Question(Parcel in) {
        id = in.readString();
        location = in.readString();
        type = in.readString();
        language = in.readString();
        statement = in.readString();
        correctAnswer = in.readString();
        incorrectAnswerA = in.readString();
        incorrectAnswerB = in.readString();
        incorrectAnswerC = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(location);
        dest.writeString(type);
        dest.writeString(language);
        dest.writeString(statement);
        dest.writeString(correctAnswer);
        dest.writeString(incorrectAnswerA);
        dest.writeString(incorrectAnswerB);
        dest.writeString(incorrectAnswerC);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getIncorrectAnswerA() {
        return incorrectAnswerA;
    }

    public void setIncorrectAnswerA(String incorrectAnswerA) {
        this.incorrectAnswerA = incorrectAnswerA;
    }

    public String getIncorrectAnswerB() {
        return incorrectAnswerB;
    }

    public void setIncorrectAnswerB(String incorrectAnswerB) {
        this.incorrectAnswerB = incorrectAnswerB;
    }

    public String getIncorrectAnswerC() {
        return incorrectAnswerC;
    }

    public void setIncorrectAnswerC(String incorrectAnswerC) {
        this.incorrectAnswerC = incorrectAnswerC;
    }
}
