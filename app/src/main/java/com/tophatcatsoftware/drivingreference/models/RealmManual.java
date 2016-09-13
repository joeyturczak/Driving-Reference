package com.tophatcatsoftware.drivingreference.models;

import io.realm.RealmObject;

/**
 * Created by joeyturczak on 9/13/16.
 */

public class RealmManual extends RealmObject {

    private String id;
    private String type;
    private String url;
    private String displayName;
    private boolean downloaded;
    private int lastPage;

    public RealmManual() {

    }

    public RealmManual(String id, FbManual manual) {
        this.id = id;
        this.type = manual.getType();
        this.url = manual.getUrl();
        this.displayName = manual.getDisplayName();
        this.downloaded = false;
        this.lastPage = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }
}
