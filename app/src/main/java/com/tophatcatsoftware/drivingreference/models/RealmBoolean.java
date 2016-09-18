package com.tophatcatsoftware.drivingreference.models;

import io.realm.RealmObject;

/**
 * Created by joeyturczak on 9/17/16.
 */

public class RealmBoolean extends RealmObject{

    private boolean bool;

    public RealmBoolean() {

    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }
}
