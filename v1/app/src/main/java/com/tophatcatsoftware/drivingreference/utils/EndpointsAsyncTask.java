package com.tophatcatsoftware.drivingreference.utils;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.tophatcatsoftware.drivingreference.backend.drivingReferenceApi.DrivingReferenceApi;
import com.tophatcatsoftware.drivingreference.backend.drivingReferenceApi.model.DrivingManual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Joey Turczak
 *
 * Downloads data from GCE backend.
 */
public class EndpointsAsyncTask extends AsyncTask<Object, Void, List<DrivingManual>> {
    public static final int DOWNLOAD_ALL = 0;
    public static final int DOWNLOAD_AFTER_DATE = 1;

    private static DrivingReferenceApi myApiService = null;

    public EndpointsAsyncTask() {
    }

    @Override
    protected List<DrivingManual> doInBackground(Object... params) {

        if(myApiService == null) {  // Only do this once
            DrivingReferenceApi.Builder builder = new DrivingReferenceApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://driving-reference-1301.appspot.com/_ah/api/");
            // end options for devappserver

            myApiService = builder.build();
        }

        List<DrivingManual> drivingManuals = new ArrayList<>();

        int request = (int)params[0];

        try {
            switch (request) {
                case DOWNLOAD_ALL:
                    drivingManuals = myApiService.getDrivingManuals().execute().getItems();
                    break;
                case DOWNLOAD_AFTER_DATE:
                    Long lastUpdated = (Long) params[1];
                    drivingManuals = myApiService.getDrivingManualsAfterDate(lastUpdated).execute().getItems();
                    break;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return drivingManuals;
    }
}
