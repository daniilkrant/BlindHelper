package ua.protech.protech.g2s;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ScaningService extends Service {
    private static final int REQUEST_ENABLE_BT = 2;
    private ArrayList<BlindBeacon> wifiBeacons = new ArrayList<>();
    private ArrayList<BlindBeacon> btBeacons = new ArrayList<>();
    private int last_size = 0;
    private Vibrator v;
    private SharedPreferences sharedPreferences;
    private Runnable scanningThread;
    private BluetoothAdapter bluetoothAdapter;

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
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

        scanningThread  = () -> {
            getNearbyWiFiBeacons();
            getNearbyBtBeacons();
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                scanningThread.run();
            }
        },0, Data.getScanPeriod());

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

    public void getNearbyBtBeacons() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getName() != null &&
                            device.getName().startsWith(Data.AP_SSID_PATTERN)) {
                        Log.e("@@@", "found");
                        String deviceName = device.getName();
                        BlindBeacon blindBeacon = new BlindBeacon(false);
                        blindBeacon.uuid = device.getAddress();
                        blindBeacon.name = deviceName + " Bluetooth";
                        blindBeacon.setSsid(deviceName);
                        blindBeacon.setBt(true);
                        btBeacons.add(blindBeacon);


                        Data.setBtBeaconsList(btBeacons);
                        checkForNewBeacons();
                    }
                }
            }
        };

        registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();

//        Set<BluetoothDevice> knownDevices = bluetoothAdapter.getBondedDevices();
//
//        if (knownDevices.size() > 0) {
//            btBeacons.clear();
//            for (BluetoothDevice device : knownDevices) {
//                String deviceName = device.getName();
//
//                if (deviceName.startsWith(Data.AP_SSID_PATTERN)) {
//                    BlindBeacon blindBeacon = new BlindBeacon(false);
//                    blindBeacon.uuid = device.getAddress();
//                    blindBeacon.name = deviceName + " Bluetooth";
//                    blindBeacon.setSsid(deviceName);
//                    blindBeacon.setBt(true);
//                    btBeacons.add(blindBeacon);
//                }
//            }
//            Data.setBtBeaconsList(btBeacons);
//            checkForNewBeacons();
    }

    public void getNearbyWiFiBeacons(){
        BlindBeacon temp_beacon;
        HashMap<String,String> BSSID_list = WiFiRoutine.getInstance().getPointsRegex();
        wifiBeacons.clear();
        for (Map.Entry<String, String> entry : BSSID_list.entrySet()) {
            temp_beacon = Data.getBeaconInfoByMac(entry.getKey());
            temp_beacon.setSsid(entry.getValue());
            wifiBeacons.add(temp_beacon);
        }

        Data.setWifiBeaconsList(wifiBeacons);
        checkForNewBeacons();
    }

    private void checkForNewBeacons() {
        EventBus.getDefault().postSticky(new ServiceMessages("Update"));
        int current_size = Data.getAggregatedBeaconsList().size();

        if (current_size > last_size) {
            if (sharedPreferences.getBoolean((Data.IS_VIBRO), true)) {
                v.vibrate(1500);
            }
            if (sharedPreferences.getBoolean((Data.IS_AUDIO), true)) {
                TTS.getInstance().speakWords(getString(R.string.found_new_beacon) + (Data.getAggregatedBeaconsList().size() - last_size));
            }
        }

        if (current_size != last_size && current_size != 0) {
            if (sharedPreferences.getBoolean((Data.IS_AUDIO), true)) {
                String message = getString(R.string.beacon_find);
                TTS.getInstance().speakWords(message);
                for (int i = 0; i < wifiBeacons.size(); i++) {
                    TTS.getInstance().speakWords(wifiBeacons.get(i).getName());
                    TTS.getInstance().silence();
                }
                for (int i = 0; i < btBeacons.size(); i++) {
                    TTS.getInstance().speakWords(btBeacons.get(i).getName());
                    TTS.getInstance().silence();
                }
            }
        }

        last_size = Data.getAggregatedBeaconsList().size();
    }

    public ArrayList<BlindBeacon> getBeaconsList() {
        return wifiBeacons;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
