package com.tophatcatsoftware.drivingreference.utils;

/**
 * Created by joeyturczak on 4/20/16.
 */

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import com.tophatcatsoftware.drivingreference.backend.drivingReferenceApi.DrivingReferenceApi;
import com.tophatcatsoftware.drivingreference.models.JsonManual;
import com.tophatcatsoftware.drivingreference.models.Manual;

import java.io.IOException;

/**
 * Copyright (C) 2016 Joey Turczak
 *
 * Downloads data from GCE backend.
 */
public class DebugEndpointsTask extends AsyncTask<Object, Void, Void> {
    public static final int INSERT_MANUAL = 0;
    public static final int MODIFY_MANUAL = 1;
    public static final int REMOVE_MANUAL = 2;

    private static DrivingReferenceApi myApiService = null;

    public DebugEndpointsTask() {
    }

    @Override
    protected Void doInBackground(Object... params) {

        if(myApiService == null) {  // Only do this once
            DrivingReferenceApi.Builder builder = new DrivingReferenceApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://driving-reference-1301.appspot.com/_ah/api/");
            // end options for devappserver

            myApiService = builder.build();
        }

        int request = (int)params[0];

        try {
            switch (request) {
                case INSERT_MANUAL: {
                    JsonManual jsonManual = (JsonManual) params[1];
                    myApiService.insertDrivingManual(jsonManual.getLocation(), jsonManual.getType(),
                            jsonManual.getLanguage(), jsonManual.getUrl(), jsonManual.getDisplayName()).execute();
                    break;
                }
                case MODIFY_MANUAL: {
                    JsonManual jsonManual = (JsonManual) params[1];
                    Long id = (Long) params[2];
                    myApiService.modifyDrivingManual(id).setUrl(jsonManual.getUrl()).setDisplayName(jsonManual.getDisplayName()).execute();
                    break;
                }
                case REMOVE_MANUAL: {
                    Manual manual = (Manual) params[1];
                    myApiService.removeDrivingManual(manual.getId()).execute();
                    break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
