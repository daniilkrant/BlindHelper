package com.android.protech.blindhelper;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.isupatches.wisefy.WiseFy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class WiFiRoutine {

    private static final WiFiRoutine ourInstance = new WiFiRoutine();
    private Context c;
    private WifiManager wifiManager;
    private WiseFy mWiseFy = null;
    private HashMap<String,String> pointsRegexList = new HashMap<String,String>();

    public static WiFiRoutine getInstance() {
        return ourInstance;
    }

    private WiFiRoutine() {

    }

    void initWifi(Context context){
        this.c = context;
        mWiseFy = new WiseFy.brains(c.getApplicationContext()).getSmarts();
    }

    HashMap<String,String> getPointsRegex(){
        List<ScanResult> scans = mWiseFy.getNearbyAccessPoints(true);
        pointsRegexList.clear();
        for (ScanResult s: scans){
            if (s.SSID.startsWith(Data.AP_SSID_PATTERN)){
                pointsRegexList.put("123456789AB",s.SSID);
//                pointsRegexList.put(s.BSSID,s.SSID);
            }
        }
        return pointsRegexList;
    }

    void connect(String ssid){

        mWiseFy.addWPA2Network(ssid, Data.AP_PASS_PATTERN);
        mWiseFy.connectToNetwork(ssid, 3000);
    }

    void disconnect(String ssid){
        mWiseFy.removeNetwork(ssid);
        mWiseFy.disconnectFromCurrentNetwork();
    }

    boolean isConnectedToWiFiNetwork(){
        return mWiseFy.isDeviceConnectedToWifiNetwork();
    }

    boolean isConnectedToMobileNetwork(){
        return mWiseFy.isDeviceConnectedToMobileNetwork();
    }
}
