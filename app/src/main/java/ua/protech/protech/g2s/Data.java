package ua.protech.protech.g2s;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Data {
    static final String AP_SSID_PATTERN = "BlindHelper";
    static final String AP_PASS_PATTERN = "12431243";
    static final String API_BASE_URL = "http://braille-device.com/iBeacon/";
    static final String PASS_MAC = "mac";
    static final String PASS_SSID = "ssid";
    static final String PASS_NAME = "name";
    static final String PASS_ISBT = "isbt";
    static final String NUMBER_OF_SIGNALS_ARRAY_POSITION = "number_of_signals_array_position";
    static final String NUMBER_OF_CYCLES_POSITION = "NUMBER_OF_CYCLES_POSITION";
    static final String NUMBER_OF_SIGNALS = "number_of_signals";
    static final String NUMBER_OF_CYCLES = "NUMBER_OF_CYCLES";
    static final String DEMO_SOUND = "DEMO_SOUND";
    static final String SETTINGS_FILE_SHARED_PREF = "settings";
    static final String TTS_ENGINE = "tts_engine";
    static final String IS_VIBRO = "is_vibro";
    static final String IS_AUTO_AUDIO = "is_auto_audio";
    static final String IS_AUDIO = "is_audio";
    static final String IS_GUIDE = "is_guide";
    static final String IS_WENT_FROM_RADAR = "went_from_radar";
    static final String TAG = "BLINDHELPER";
    static final String BEACONS_FILE_NAME = "BLINDHELPER";
    static final String[] cycles_list = {"1","2","3","5","10","15"};
    static final String[] sound_counter_list= {"1","2","3","4","5","7","10","15","20"};

    private static ArrayList<BlindBeacon> wifiBeaconsList = new ArrayList<>();
    private static ArrayList<BlindBeacon> btBeaconsList = new ArrayList<>();
    private static ArrayList<BlindBeacon> aggregatedBeaconsList = new ArrayList<>();

    public static ArrayList<BlindBeacon> getAggregatedBeaconsList() {
        aggregatedBeaconsList.clear();
        aggregatedBeaconsList.addAll(wifiBeaconsList);
        aggregatedBeaconsList.addAll(btBeaconsList);

        return aggregatedBeaconsList;
    }

    public static ArrayList<BlindBeacon> getWifiBeaconsList() {
        if (wifiBeaconsList == null) {
            return new ArrayList<BlindBeacon>();
        }
        return wifiBeaconsList;
    }

    public static void setWifiBeaconsList(ArrayList<BlindBeacon> wifiBeaconsList) {
        Data.wifiBeaconsList = wifiBeaconsList;
    }

    public static ArrayList<BlindBeacon> getBtBeaconsList() {
        return btBeaconsList;
    }

    public static void setBtBeaconsList(ArrayList<BlindBeacon> btBeaconsList) {
        if (Data.btBeaconsList != null) {
            Data.btBeaconsList.clear();
        }
        Data.btBeaconsList = btBeaconsList;
    }


    public static int getScanPeriod() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            return 31000;
        } else {
            return 15000;
        }
    }

    //Serialization

    private static ArrayList<BlindBeacon> serialized_beacons;

    public static ArrayList<BlindBeacon> getSerialized_beacons(Context context) {
        if (serialized_beacons == null)
            try {
                serialized_beacons = BlindBeacon.getList(context);
            } catch (FileNotFoundException e) {
                serialized_beacons = new ArrayList<>();
                e.printStackTrace();
            }
        return serialized_beacons;
    }

    public static void setSerialized_beacons(ArrayList<BlindBeacon> serialized_beacons) {
        Data.serialized_beacons = serialized_beacons;
    }

    @NonNull
    public static BlindBeacon getBeaconInfoByMac(String mac){
        if (serialized_beacons != null) {
            for (BlindBeacon b : serialized_beacons) {
                if (b.getUuid().equals(mac))
                    return b;
            }
        }
        return new BlindBeacon(false); //ideally unreachable, return mock
    }

    @NonNull
    public static BlindBeacon getBeaconInfoByName(String name){
        if (serialized_beacons != null) {
            for (BlindBeacon b : serialized_beacons) {
                if (b.getName().equals(name))
                    return b;
            }
        }
        return new BlindBeacon(false); //ideally unreachable, return mock
    }

    //Serialization

}
