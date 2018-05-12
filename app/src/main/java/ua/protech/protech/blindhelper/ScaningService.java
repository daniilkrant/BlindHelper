package ua.protech.protech.blindhelper;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.security.RunAs;

public class ScaningService extends Service {
    private ArrayList<BlindBeacon> beaconArrayList = new ArrayList<>();
    private int last_size = 0;
    private Vibrator v;
    private SharedPreferences sharedPreferences;
    private Runnable scanningThread;

    public ScaningService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("@@@", "Service: onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(Data.SETTINGS_FILE_SHARED_PREF, Context.MODE_PRIVATE);
        WiFiRoutine.getInstance().initWifi(getApplicationContext());
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        String tts_engine = sharedPreferences.getString(Data.TTS_ENGINE, "empty");
        if (!tts_engine.equals("empty"))
            TTS.getInstance().initTTS(getApplicationContext(), tts_engine);
        else
            TTS.getInstance().initTTS(getApplicationContext());



        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Система маяковой навигации G2s")
                .setContentText("Поиск маяков ...");
        Notification notification;
        notification = builder.build();
        startForeground(740, notification);
        scanningThread  = new Runnable() {
            @Override
            public void run() {
                scanForBeacons();
                checkForNewBeacons();
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                scanningThread.run();
            }
        },0,4000);

    }

    private void scanForBeacons() {
        ArrayList<BlindBeacon> ret = Data.getNearbyBeacons();
        if (ret != null) {
            beaconArrayList.clear();
            beaconArrayList.addAll(ret);
            Log.e("@@@", "" + beaconArrayList.size());
        }
    }

    private void checkForNewBeacons() {
        int current_size = beaconArrayList.size();
        if (current_size > last_size) {
            if (sharedPreferences.getBoolean((Data.IS_VIBRO), true)) {
                v.vibrate(1500);
                if (sharedPreferences.getBoolean((Data.IS_AUDIO), true)) {
                    TTS.getInstance().speakWords(getString(R.string.found_new_beacon) + Integer.toString(beaconArrayList.size() - last_size));
                }
            }
        }

        if (current_size != last_size && current_size != 0) {
            if (sharedPreferences.getBoolean((Data.IS_AUDIO), true)) {
                String message = getString(R.string.beacon_find);
                TTS.getInstance().speakWords(message);
                for (int i = 0; i < beaconArrayList.size(); i++) {
                    TTS.getInstance().speakWords(beaconArrayList.get(i).getName());
                    TTS.getInstance().silence();
                }
            }
        }

        last_size = beaconArrayList.size();
    }

    public ArrayList<BlindBeacon> getBeaconsList() {
        return beaconArrayList;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
