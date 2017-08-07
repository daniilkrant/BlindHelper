package com.android.protech.blindhelper;

import android.os.Parcel;
import android.os.Parcelable;

public class BlindBeacon implements Parcelable{
    String uuid;
    double distance;
    String name;
    boolean isAlive;


    protected BlindBeacon(Parcel in) {
        uuid = in.readString();
        distance = in.readFloat();
        name = in.readString();
        isAlive = in.readByte() != 0;
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

    public BlindBeacon(String uuid, double distance, String name) {
        this.uuid = uuid;
        this.distance = distance;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeDouble(distance);
        parcel.writeString(name);
        parcel.writeByte((byte) (isAlive ? 1 : 0));
    }
}
