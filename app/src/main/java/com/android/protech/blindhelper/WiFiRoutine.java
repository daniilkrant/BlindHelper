package com.android.protech.blindhelper;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.isupatches.wisefy.WiseFy;

import java.util.ArrayList;
import java.util.List;

class WiFiRoutine {

    private static final WiFiRoutine ourInstance = new WiFiRoutine();
    private Context c;
    private WifiManager wifiManager;
    private WiseFy mWiseFy = null;
    private ArrayList<String> pointsRegexList = new ArrayList<>();

    public static WiFiRoutine getInstance() {
        return ourInstance;
    }

    private WiFiRoutine() {

    }

    void initWifi(Context context){
        this.c = context;
        mWiseFy = new WiseFy.brains(c.getApplicationContext()).getSmarts();
    }

    ArrayList <String> getPointsRegex(){
        List<ScanResult> scans = mWiseFy.getNearbyAccessPoints(true);

        for (ScanResult s: scans){
            Log.d("ssid", s.SSID);
            if (s.SSID.startsWith(Data.AP_SSID_PATTERN)){
                pointsRegexList.add(s.SSID);
            }
        }
        return pointsRegexList;
    }

    ArrayList<BlindBeacon> getNearbyBeacons(){
        ArrayList<BlindBeacon> blindBeacons= new ArrayList<>();
        pointsRegexList.clear();
        getPointsRegex();
        for (String s: pointsRegexList){
            blindBeacons.add(new BlindBeacon(s));
        }
        return blindBeacons;
    }

    void connect(String ssid){

        mWiseFy.addWPA2Network(ssid, Data.AP_PASS_PATTERN);
        mWiseFy.connectToNetwork(ssid, 3000);
    }

    void disconnect(String ssid){
        mWiseFy.disconnectFromCurrentNetwork();
        mWiseFy.removeNetwork(ssid);
    }
}
