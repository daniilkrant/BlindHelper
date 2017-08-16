package com.android.protech.blindhelper;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlindBeacon implements Parcelable {

    @SerializedName("HEX(MAC)")
    @Expose
    String uuid;
    @SerializedName("Description")
    @Expose
    String description;
    @SerializedName("Name")
    @Expose
    String name;
    @SerializedName("Lng")
    @Expose
    private String lng;
    @SerializedName("Lat")
    @Expose
    private String lat;
    @SerializedName("Address")
    @Expose
    private String addr;
    private String ssid;


    protected BlindBeacon(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        description = in.readString();
        lng = in.readString();
        lat = in.readString();
        addr = in.readString();
        ssid = in.readString();
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

    public BlindBeacon(){

    }
    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

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

    public String getLocation() {
        return lat+","+lng;
    }

    public void setLng(String location) {
        this.lng = location;
    }

    public void setLat(String location) {
        this.lat = location;
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
        parcel.writeString(lng);
        parcel.writeString(lat);
        parcel.writeString(addr);
        parcel.writeString(ssid);
    }
}