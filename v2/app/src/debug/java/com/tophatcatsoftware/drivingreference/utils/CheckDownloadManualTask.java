package com.tophatcatsoftware.drivingreference.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.tophatcatsoftware.drivingreference.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Copyright (C) 2016 Joey Turczak
 *
 * Downloads a file from the given url and stores it with the given file name to the app's
 * file directory. Sends out a broadcast when the task is complete or if there is an error.
 */
public class CheckDownloadManualTask extends AsyncTask<Void, Integer, String> {

    public static final String RESULT_SUCCESS = "Success";
    public static final String RESULT_ERROR = "Error";

    private PowerManager.WakeLock mWakeLock;
    private Context mContext;
    private String mUrl;

    public CheckDownloadManualTask(Context context, String url) {
        mContext = context;
        mUrl = url;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return mContext.getString(R.string.download_server_error) + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            } else {
                return "Success";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return RESULT_ERROR;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(result, mUrl);
        mWakeLock.release();
    }
}
