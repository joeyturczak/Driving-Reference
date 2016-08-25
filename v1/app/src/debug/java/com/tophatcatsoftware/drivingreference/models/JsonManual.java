package com.tophatcatsoftware.drivingreference.models;

/**
 * Created by joeyturczak on 4/19/16.
 */
public class JsonManual {

    private String location;
    private String type;
    private String language;
    private String url;
    private String displayName;

    public JsonManual(String location, String type, String language, String url, String displayName) {
        this.location = location;
        this.type = type;
        this.language = language;
        this.url = url;
        this.displayName = displayName;
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
}
