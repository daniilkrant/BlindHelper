package com.android.protech.blindhelper;

import android.os.Parcel;
import android.os.Parcelable;

public class BlindBeacon implements Parcelable {
    protected BlindBeacon(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        description = in.readString();
        location = in.readString();
    }

    public static final Creator<BlindBeacon> CREATOR = new Creator<BlindBeacon>() {
        @Override
        public BlindBeacon createFromParcel(Parcel in) {
            return new BlindBeacon(in);
        }

        @Override
        public BlindBeacon[] newArray(int size) {
            return new BlindBeacon[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String uuid;
    String name;
    String description;
    String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public BlindBeacon(String uuid) {
        this.uuid = uuid;
        this.name = uuid; //TODO: Delete after requests adding
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(location);
    }
}
