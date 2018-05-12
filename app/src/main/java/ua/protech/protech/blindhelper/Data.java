package ua.protech.protech.blindhelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Data {
    static final String AP_SSID_PATTERN = "BlindHelper";
    static final String AP_PASS_PATTERN = "12431243";
    static final String API_BASE_URL = "http://braille-device.com/iBeacon/";
    static final String PASS_MAC = "mac";
    static final String PASS_SSID = "ssid";
    static final String NUMBER_OF_SIGNALS_ARRAY_POSITION = "number_of_signals_array_position";
    static final String NUMBER_OF_CYCLES_POSITION = "NUMBER_OF_CYCLES_POSITION";
    static final String NUMBER_OF_SIGNALS = "number_of_signals";
    static final String NUMBER_OF_CYCLES = "NUMBER_OF_CYCLES";
    static final String SETTINGS_FILE_SHARED_PREF = "settings";
    static final String TTS_ENGINE = "tts_engine";
    static final String IS_VIBRO = "is_vibro";
    static final String IS_AUTO_AUDIO = "is_auto_audio";
    static final String IS_AUDIO = "is_audio";
    static final String IS_WENT_FROM_RADAR = "went_from_radar";
    static final String TAG = "BLINDHELPER";
    static final String BEACONS_FILE_NAME = "BLINDHELPER";
    static final String[] cycles_list = {"1","3","5","7","10","15"};
    static final String[] sound_counter_list= {"1","2","3","4","5","6","10","15","20"};

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
    public static BlindBeacon getBeaconInfo(String mac){
        for (BlindBeacon b: serialized_beacons){
            if (b.getUuid().equals(mac))
            return b;
        }
        return new BlindBeacon(false); //ideally unreachable, return mock
    }

    public static ArrayList<BlindBeacon> getNearbyBeacons(){
        ArrayList<BlindBeacon> blindBeacons = new ArrayList<>();
        BlindBeacon temp_beacon;
        HashMap<String,String> BSSID_list = WiFiRoutine.getInstance().getPointsRegex();

        if (BSSID_list != null) {

            for (Map.Entry<String, String> entry : BSSID_list.entrySet()) {
                temp_beacon = Data.getBeaconInfo(entry.getKey());
                temp_beacon.setSsid(entry.getValue());
                blindBeacons.add(temp_beacon);
            }
        }

        return blindBeacons;
    }


    //Serialization

}
