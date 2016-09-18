package com.tophatcatsoftware.drivingreference.models;

import io.realm.RealmObject;

/**
 * Created by joeyturczak on 9/17/16.
 */

public class RealmQuestion extends RealmObject {

    private String id;
    private String location;
    private String type;
    private String language;
    private String statement;
    private String correctAnswer;
    private String incorrectAnswerA;
    private String incorrectAnswerB;
    private String incorrectAnswerC;

    public RealmQuestion() {

    }

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
