package ua.protech.protech.g2s;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.security.RunAs;

public class ScaningService extends Service {
    private ArrayList<BlindBeacon> beaconArrayList = new ArrayList<>();
    private int last_size = 0;
    private Vibrator v;
    private SharedPreferences sharedPreferences;
    private Runnable scanningThread;

    private PendingIntent pendingIntent;

    public ScaningService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(740, notification);

        scanningThread  = new Runnable() {
            @Override
            public void run() {
                getNearbyBeacons();
                Log.e("@@@@", "size: " + Integer.toString(beaconArrayList.size()));
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

    @TargetApi(26)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Система маяковой навигации G2s")
                .setContentText("Поиск маяков ...")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(741, notification);
    }

    public void getNearbyBeacons(){
        beaconArrayList.clear();
        BlindBeacon temp_beacon;
        HashMap<String,String> BSSID_list = WiFiRoutine.getInstance().getPointsRegex();

        if (BSSID_list != null) {
            beaconArrayList.clear();
            for (Map.Entry<String, String> entry : BSSID_list.entrySet()) {
                temp_beacon = Data.getBeaconInfo(entry.getKey());
                temp_beacon.setSsid(entry.getValue());
                beaconArrayList.add(temp_beacon);
            }
        }

        Data.setBeaconsAfterScan(beaconArrayList);
        checkForNewBeacons();
    }

    private void checkForNewBeacons() {
        int current_size = beaconArrayList.size();
        if (current_size != last_size)
            EventBus.getDefault().postSticky(new ServiceMessages("Update"));

        if (current_size > last_size) {
            if (sharedPreferences.getBoolean((Data.IS_VIBRO), true)) {
                v.vibrate(1500);
            }
            if (sharedPreferences.getBoolean((Data.IS_AUDIO), true)) {
                TTS.getInstance().speakWords(getString(R.string.found_new_beacon) + Integer.toString(beaconArrayList.size() - last_size));
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
