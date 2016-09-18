package com.tophatcatsoftware.drivingreference.models;

import io.realm.RealmObject;

/**
 * Created by joeyturczak on 9/17/16.
 */

public class RealmString extends RealmObject {

    private String string;

    RealmString() {

    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
