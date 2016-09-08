package com.tophatcatsoftware.drivingreference.models;

/**
 * Created by joeyturczak on 9/7/16.
 */
public class BaseManual {

    public static final int NOT_DOWNLOADED = 0;
    public static final int DOWNLOADED = 1;

    protected String location;
    protected String type;
    protected String language;
    protected String url;
    protected String displayName;
    protected int downloaded;
    protected int lastPage;

    public BaseManual() {

    }

    public BaseManual(String location, String type, String language, String url, String displayName, int downloaded, int lastPage) {
        this.location = location;
        this.type = type;
        this.language = language;
        this.url = url;
        this.displayName = displayName;
        this.downloaded = downloaded;
        this.lastPage = lastPage;
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

    public int getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }
}
