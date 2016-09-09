package com.tophatcatsoftware.drivingreference.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joeyturczak on 9/7/16.
 */
public class ManualInfo implements Parcelable {

    public static final int NOT_DOWNLOADED = 0;
    public static final int DOWNLOADED = 1;

    protected String id;
    protected int downloaded;
    protected int lastPage;

    public ManualInfo() {

    }

    public ManualInfo(String id, int downloaded, int lastPage) {
        this.id = id;
        this.downloaded = downloaded;
        this.lastPage = lastPage;
    }


    protected ManualInfo(Parcel in) {
        id = in.readString();
        downloaded = in.readInt();
        lastPage = in.readInt();
    }

    public static final Creator<ManualInfo> CREATOR = new Creator<ManualInfo>() {
        @Override
        public ManualInfo createFromParcel(Parcel in) {
            return new ManualInfo(in);
        }

        @Override
        public ManualInfo[] newArray(int size) {
            return new ManualInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(downloaded);
        dest.writeInt(lastPage);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
