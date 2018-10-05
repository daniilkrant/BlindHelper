package ua.protech.protech.g2s;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.isupatches.wisefy.WiseFy;
import com.isupatches.wisefy.callbacks.GetNearbyAccessPointsCallbacks;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

class WiFiRoutine {

    private static final WiFiRoutine ourInstance = new WiFiRoutine();
    private Context c;
    private WiseFy mWiseFy = null;

    private HashMap<String, String> pointsRegexList = new HashMap<String, String>();

    public static WiFiRoutine getInstance() {
        return ourInstance;
    }

    private WiFiRoutine() {

    }

    void initWifi(Context context) {

        if (this.c == null) {
            this.c = context;
            mWiseFy = new WiseFy.Brains(c.getApplicationContext()).logging(true).getSmarts();
        }
    }

    HashMap<String, String> getPointsRegex() {
        final CountDownLatch latch = new CountDownLatch(1);
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("TAG", "error");
        }
        mWiseFy.getNearbyAccessPoints(true, new GetNearbyAccessPointsCallbacks() {
            @Override
            public void wisefyFailure(int i) {
                Log.e("TAG", Integer.toString(i));
            }

            @Override
            public void retrievedNearbyAccessPoints(List<ScanResult> nearbyAccessPoints) {
                pointsRegexList.clear();
                for (ScanResult s : nearbyAccessPoints) {
                    if (s.SSID.startsWith(Data.AP_SSID_PATTERN)) {
                        pointsRegexList.put(s.BSSID.toUpperCase().replace(":", ""), s.SSID);
                    }
                }
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return pointsRegexList;
    }

    boolean connect(String ssid) {
        mWiseFy.addWPA2Network(ssid, Data.AP_PASS_PATTERN);
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                SupplicantState supplicantState;
//                WifiManager wifiManager = (WifiManager) getSystem
//            }
//        };
//        r.run();
        return mWiseFy.connectToNetwork(ssid, 1000);
    }

    void disconnect(String ssid) {
        mWiseFy.removeNetwork(ssid);
        mWiseFy.disconnectFromCurrentNetwork();
    }

    String get_curr() {
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return mWiseFy.getCurrentNetwork().getSSID();
    }

    boolean disconnectCurrent(){
        mWiseFy.disconnectFromCurrentNetwork();
        return true;
    }

}
