package com.tophatcatsoftware.drivingreference.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.tophatcatsoftware.drivingreference.R;
import com.tophatcatsoftware.drivingreference.data.DrivingContract;
import com.tophatcatsoftware.drivingreference.models.JsonManual;
import com.tophatcatsoftware.drivingreference.models.Manual;
import com.tophatcatsoftware.drivingreference.utils.CheckDownloadManualTask;
import com.tophatcatsoftware.drivingreference.utils.DebugEndpointsTask;
import com.tophatcatsoftware.drivingreference.utils.ManualUtility;
import com.tophatcatsoftware.drivingreference.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joeyturczak on 4/19/16.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        Preference preference = findPreference("checkManuals");
        preference.setOnPreferenceClickListener(this);

        preference = findPreference("updateManuals");
        preference.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        switch (key) {
            case "checkManuals":
                checkManuals();
                return true;
            case "updateManuals":
                updateManuals();
                return true;
            default:
                return false;
        }
    }

    private void checkManuals() {
        List<Manual> manuals = getAllDbManuals();

        for(Manual manual : manuals) {
            new CheckDownloadManualTask(getActivity(), manual.getUrl()).execute();
        }

        Log.d("CHECK", "FINISH");
    }

    private void updateManuals() {

        List<JsonManual> jsonManuals = getJsonManuals();

        List<Manual> dbManuals = getAllDbManuals();

        boolean exists;

        for(JsonManual jsonManual : jsonManuals) {
            // Check if this manual exists in db
            // If not then insert it online
            // If so then check to see if it's different
            // If different then modify
            exists = false;

            for(Manual manual : dbManuals) {
                if (manual.getLocation().equals(jsonManual.getLocation()) && manual.getLanguage().equals(jsonManual.getLanguage()) && manual.getType().equals(jsonManual.getType())) {
                    String url = jsonManual.getUrl();
                    String displayName = jsonManual.getDisplayName();

                    if(manual.getUrl().equals(url)) {
                        url = null;
                    }
                    if(manual.getDisplayName().equals(displayName)) {
                        displayName = null;
                    }

                    if(url != null || displayName != null) {
                        modifyManualOnBackend(jsonManual, manual.getId());
                    }

                    exists = true;
                    break;
                }
            }

            if(!exists) {
                addManualToBackend(jsonManual);
            }
        }

        // If dbManuals > jsonManuals then find the extra and delete it
        if(dbManuals.size() > jsonManuals.size()) {

            for(Manual manual : dbManuals) {
                exists = false;
                for(JsonManual jsonManual : jsonManuals) {
                    if(manual.getLocation().equals(jsonManual.getLocation()) && manual.getLanguage().equals(jsonManual.getLanguage()) && manual.getType().equals(jsonManual.getType())) {
                        exists = true;
                    }
                }

                if(!exists) {
                    deleteManualFromBackend(manual);
                }
            }
        }
    }

    private List<Manual> getAllDbManuals() {
        List<Manual> manuals = new ArrayList<>();

        String sortOrder = DrivingContract.DrivingManualEntry.COLUMN_TYPE + getString(R.string.sort_order_asc);

        ContentResolver contentResolver = getActivity().getContentResolver();

        Cursor cursor = contentResolver.query(
                DrivingContract.DrivingManualEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);

        if(cursor != null && cursor.moveToFirst()) {
            do {
                manuals.add(ManualUtility.getManualFromCursor(cursor));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return manuals;
    }

    private List<JsonManual> getJsonManuals() {
        List<JsonManual> manuals = new ArrayList<>();

        try {
            String jsonString = Utility.loadJsonFromAsset(getActivity(), "manuals.json");

            JSONObject jsonResponse = new JSONObject(jsonString);

            JSONArray jsonManuals = jsonResponse.getJSONArray("manuals");

            String location;
            String language;
            String type;
            String url;
            String displayName;

            JSONObject currentObject;
            Log.d("JSONLENGTH", String.valueOf(jsonManuals.length()));

            for(int i = 0; i < jsonManuals.length(); i++) {
                currentObject = jsonManuals.getJSONObject(i);

                location = currentObject.getString("location");
                language = currentObject.getString("language");
                type = currentObject.getString("type");
                url = currentObject.getString("url");
                displayName = currentObject.getString("displayName");
                manuals.add(new JsonManual(location, type, language, url, displayName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return manuals;
    }


    private void addManualToBackend(JsonManual jsonManual) {
        new DebugEndpointsTask().execute(DebugEndpointsTask.INSERT_MANUAL, jsonManual);
    }

    private void modifyManualOnBackend(JsonManual jsonManual, long id) {
        new DebugEndpointsTask().execute(DebugEndpointsTask.MODIFY_MANUAL, jsonManual, id);
    }

    private void deleteManualFromBackend(Manual manual) {
        new DebugEndpointsTask().execute(DebugEndpointsTask.REMOVE_MANUAL, manual);
    }
}

