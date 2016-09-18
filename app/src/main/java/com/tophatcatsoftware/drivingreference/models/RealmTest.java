package com.tophatcatsoftware.drivingreference.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by joeyturczak on 9/17/16.
 */

public class RealmTest extends RealmObject {

    private long date;
    private String location;
    private String type;
    private String language;
    private RealmList<RealmString> questionIds;
    private RealmList<RealmString> answers;
    private RealmList<RealmBoolean> checked;
    private int score;
    private boolean completed;

    public RealmTest() {

    }

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

    public RealmList<RealmString> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(RealmList<RealmString> questionIds) {
        this.questionIds = questionIds;
    }

    public RealmList<RealmString> getAnswers() {
        return answers;
    }

    public void setAnswers(RealmList<RealmString> answers) {
        this.answers = answers;
    }

    public RealmList<RealmBoolean> getChecked() {
        return checked;
    }

    public void setChecked(RealmList<RealmBoolean> checked) {
        this.checked = checked;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
