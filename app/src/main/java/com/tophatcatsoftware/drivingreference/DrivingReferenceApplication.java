package com.tophatcatsoftware.drivingreference;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.tophatcatsoftware.drivingreference.data.DrivingContract;
import com.tophatcatsoftware.drivingreference.services.UpdateDataIntentService;
import com.tophatcatsoftware.drivingreference.utils.Utility;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Copyright (C) 2016 Joey Turczak
 */
public class DrivingReferenceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        addQuestionsToDb();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        RealmConfiguration config = new RealmConfiguration.Builder(this).name("driving-reference.realm")
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private void addQuestionsToDb() {
        boolean isDbEmpty = Utility.isDbEmpty(this, DrivingContract.QuestionEntry.CONTENT_URI);

        Intent intent = new Intent(this, UpdateDataIntentService.class);
        intent.setAction(UpdateDataIntentService.ACTION_UPDATE_QUESTIONS);
        intent.putExtra(getString(R.string.database_empty_intent_key), isDbEmpty);
        startService(intent);
    }
}
