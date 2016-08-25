package com.tophatcatsoftware.drivingreference.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.tophatcatsoftware.drivingreferencelib.DrivingValues.Language;
import com.tophatcatsoftware.drivingreferencelib.DrivingValues.Location;
import com.tophatcatsoftware.drivingreferencelib.DrivingValues.Type;

/**
 * Copyright (C) 2016 Joey Turczak
 */
@Entity
public class BackendQuestion {

    @Id private long id;
    private Location location;
    private Type type;
    private Language language;
    private String statement;
    private String correctAnswer;
    private String incorrectAnswerA;
    private String incorrectAnswerB;
    private String incorrectAnswerC;
    @Index
    private long lastUpdated;

    public BackendQuestion() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
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

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
