package com.tophatcatsoftware.drivingreference.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright (C) 2016 Joey Turczak
 *
 *
 * Stores driver manual information in a Parcelable implementation
 */
public class Manual extends BaseManual implements Parcelable {

    public Manual(String location, String type, String language, String url, String displayName, int downloaded, int lastPage) {
        super(location, type, language, url, displayName, downloaded, lastPage);
    }

    protected Manual(Parcel in) {
        location = in.readString();
        type = in.readString();
        language = in.readString();
        url = in.readString();
        displayName = in.readString();
        downloaded = in.readInt();
        lastPage = in.readInt();
    }

    public static final Creator<Manual> CREATOR = new Creator<Manual>() {
        @Override
        public Manual createFromParcel(Parcel in) {
            return new Manual(in);
        }

        @Override
        public Manual[] newArray(int size) {
            return new Manual[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeString(type);
        dest.writeString(language);
        dest.writeString(url);
        dest.writeString(displayName);
        dest.writeInt(downloaded);
        dest.writeInt(lastPage);
    }
}
