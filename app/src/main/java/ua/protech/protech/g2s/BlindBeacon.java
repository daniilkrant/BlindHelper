package ua.protech.protech.g2s;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class BlindBeacon implements Parcelable, Serializable {

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
    @SerializedName("Name_net")
    @Expose
    private String net_name;
    @SerializedName("Telephone")
    @Expose
    private String phone_numb;
    @SerializedName("Schedule")
    @Expose
    private String working_time;
    private String ssid;
    private static final long serialVersionUID = 4654897646L;
    private int isFav = 0;
    private boolean isBt = false;

    private BlindBeacon(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        description = in.readString();
        lng = in.readString();
        lat = in.readString();
        addr = in.readString();
        ssid = in.readString();
        working_time = in.readString();
        phone_numb = in.readString();
        isFav = in.readInt();
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

    public BlindBeacon(boolean n){
        if (!n){
            uuid = "";
            name = "";
            net_name = "";
            description = "";
            lng = "";
            lat = "";
            addr = "";
            ssid = "";
            working_time = "";
            phone_numb = "";
            isFav = 0;
        }
    }

    public boolean isBt() {
        return isBt;
    }

    public void setBt(boolean bt) {
        isBt = bt;
    }

    public int isFav() {
        return isFav;
    }

    public void setFav(int fav) {
        isFav = fav;
    }

    public String getNet_name() {
        return net_name;
    }

    public void setNet_name(String net_name) {
        this.net_name = net_name;
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

    public String getWorking_time() {
        return working_time;
    }

    public void setWorking_time(String working_time) {
        this.working_time = working_time;
    }

    public String getPhone_numb() {
        return phone_numb;
    }

    public void setPhone_numb(String phone_numb) {
        this.phone_numb = phone_numb;
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
        parcel.writeString(working_time);
        parcel.writeString(phone_numb);
        parcel.writeInt(isFav);
    }

    public static boolean saveList(Context context, ArrayList<BlindBeacon> blindBeacons) {
        try {
            FileOutputStream fos = context.openFileOutput(Data.BEACONS_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(blindBeacons);
            oos.close();
            Log.d(Data.TAG, "Saved");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Nullable
    public static ArrayList<BlindBeacon> getList(Context context) throws FileNotFoundException {
        FileInputStream fis = context.openFileInput(Data.BEACONS_FILE_NAME);
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(fis);
            Object readObject = is.readObject();
            is.close();
            if(readObject != null) {
                return (ArrayList<BlindBeacon>) readObject;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<BlindBeacon>();
    }

    public static void UpdList(Context context) {
        Data.setSerialized_beacons(null);
        try {
            FileInputStream fis = context.openFileInput(Data.BEACONS_FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object readObject = is.readObject();
            is.close();
            if(readObject != null) {
                Log.d(Data.TAG, "UPDTD");
                Data.setSerialized_beacons ((ArrayList<BlindBeacon>) readObject);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "BlindBeacon{" +
                "uuid='" + uuid + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", addr='" + addr + '\'' +
                ", net_name='" + net_name + '\'' +
                ", phone_numb='" + phone_numb + '\'' +
                ", working_time='" + working_time + '\'' +
                ", ssid='" + ssid + '\'' +
                ", isFav=" + isFav +
                '}';
    }
}