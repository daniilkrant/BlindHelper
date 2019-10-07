package ua.protech.protech.g2s;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BeaconDetailActivity extends AppCompatActivity {
    private TextView beacon_description, beacon_title, beacon_location, beacon_time, beacon_phone;
    private Button beacon_route;
    private Button btn_add_to_fav;
    private String mac, ssid, name;
    private boolean isBt;
    private BlindBeacon blindBeacon;
    private SharedPreferences sharedPreferences;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detail);
        Intent intent = getIntent();
        mac = intent.getStringExtra(Data.PASS_MAC);
        ssid = intent.getStringExtra(Data.PASS_SSID);
        name = intent.getStringExtra(Data.PASS_NAME);
        isBt = intent.getBooleanExtra(Data.PASS_ISBT, false);

        Log.e("TAG", "arg" + mac + ssid + name + isBt);

        beacon_description = (TextView) findViewById(R.id.beacon_description);
        beacon_title = (TextView) findViewById(R.id.beacon_title);
        beacon_location = (TextView) findViewById(R.id.beacon_location);
        beacon_time = (TextView) findViewById(R.id.beacon_time);
        beacon_phone = (TextView) findViewById(R.id.beacon_phone);
        beacon_route = (Button) findViewById(R.id.btn_route_to_beac);
        btn_add_to_fav = (Button) findViewById(R.id.btn_add_to_fav);
        Button btn_activate_sound = (Button) findViewById(R.id.btn_speak_to_user_beac);
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);

        boolean isCalling = intent.getBooleanExtra(Data.IS_WENT_FROM_RADAR, false);
        btn_activate_sound.setEnabled(isCalling);

        sharedPreferences = getSharedPreferences(Data.SETTINGS_FILE_SHARED_PREF, Context.MODE_PRIVATE);

        WiFiRoutine.getInstance().initWifi(getApplicationContext());

        beacon_description.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Описание:" + beacon_description.getText());
                }
                return false;
            }
        });

        beacon_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Название:" + beacon_title.getText());
                }
                return false;
            }
        });

        beacon_location.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Расположение:" + beacon_location.getText());
                }
                return false;
            }
        });

        beacon_time.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Время работы:" + beacon_time.getText());
                }
                return false;
            }
        });

        beacon_phone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Номер телефона:" + beacon_phone.getText());
                }
                return false;
            }
        });

        btn_add_to_fav.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    if (!btn_add_to_fav.getText().equals(getString(R.string.delete_from_fav)))
                        TTS.getInstance().speakWords("Кнопка добавления маяка в избранное");
                    else
                        TTS.getInstance().speakWords("Кнопка удаления маяка из избранного");
                }
                return false;
            }
        });

        btn_activate_sound.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка озвучивания маяка");
                }
                return false;
            }
        });

        beacon_route.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка вызова навигационного приложения");
                }
                return false;
            }
        });

        btn_add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blindBeacon.isFav() == 1) {
                    blindBeacon.setFav(0);
                    btn_add_to_fav.announceForAccessibility(getString(R.string.deleted_from_fav));
                    btn_add_to_fav.setText(getString(R.string.add_to_fav));
                }
                else {
                    blindBeacon.setFav(1);
                    Log.d(Data.TAG, getString(R.string.added_to_fav));
                    btn_add_to_fav.announceForAccessibility(getString(R.string.added_to_fav));
                    btn_add_to_fav.setText(getString(R.string.delete_from_fav));
                }
                UpdList updList = new UpdList();
                updList.execute();
            }
        });

        btn_activate_sound.setOnClickListener(view -> {
            if (isBt) {
                ConnectAndDisconnectByBt connectAndDisconnectByBt  = new ConnectAndDisconnectByBt(mac);
                connectAndDisconnectByBt.execute();
            } else {
                ConnectAndDisconnectByWifi connectAndDisconnectByWifi = new ConnectAndDisconnectByWifi(ssid);
                connectAndDisconnectByWifi.execute();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData loadData = new LoadData();
        loadData.execute();
    }

    private class ConnectAndDisconnectByBt extends AsyncTask {
        String mac;
        BluetoothManager bluetoothManager = BluetoothManager.getInstance();
        private SimpleBluetoothDeviceInterface deviceInterface;

        ConnectAndDisconnectByBt(String mac_){
            this.mac = mac_;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Log.i(Data.TAG, mac);
            bluetoothManager.openSerialDevice(mac)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onConnected, this::onError);
            return null;
        }

        private void onError(Throwable error) {
            Log.e("@@@", String.valueOf(error));
            // Handle the error
        }

        private void onMessageSent(String message) {
            Log.e("@@@", "onMessageSent");
            bluetoothManager.close();
        }

        private void onMessageReceived(String message) {
            // We received a message! Handle it here.
        }

        private void onConnected(BluetoothSerialDevice connectedDevice) {
            // You are now connected to this device!
            // Here you may want to retain an instance to your device:
            Log.e("@@@", "Connected");
            deviceInterface = connectedDevice.toSimpleDeviceInterface();
            deviceInterface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);

            // Let's send a message:
            String msg = "GET //" + "220" + "/" + "200" + "/" + "300" +
                    "/" + Data.sound_counter_list[sharedPreferences.getInt(Data.NUMBER_OF_SIGNALS_ARRAY_POSITION, 3)] + "/" + "1000" + "/" +
                    Data.cycles_list[sharedPreferences.getInt(Data.NUMBER_OF_CYCLES_POSITION, 1)]+ "/&\n";
            deviceInterface.sendMessage(msg);
        }
    }

    private class ConnectAndDisconnectByWifi extends AsyncTask {
        String ssid;
        ConnectAndDisconnectByWifi(String ssid_){
            this.ssid = ssid_;

        }

        @Override
        protected Object doInBackground(Object[] params) {
            Log.i(Data.TAG, ssid);
            WiFiRoutine.getInstance().disconnectCurrent();

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WiFiRoutine.getInstance().connect(ssid);
                while (true) {
                    String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                    Log.e("@@@@IP:", "Waiting for IP " + ipAddress);
                    if (!ipAddress.equals("0.0.0.0") && !ipAddress.isEmpty()) {
                        Log.e("@@@Ping:", "pinging...");

                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                String url = "http://192.168.4.1//" + "220" + "/" + "200" + "/" + "300" +
                                        "/" + Data.sound_counter_list[sharedPreferences.getInt(Data.NUMBER_OF_SIGNALS_ARRAY_POSITION, 3)] + "/" + "1000" + "/" +
                                        Data.cycles_list[sharedPreferences.getInt(Data.NUMBER_OF_CYCLES_POSITION, 1)]+ "/&";
                                Log.e("@@@URL:", url);
                                webView.setWebViewClient(new WebViewClient() {
                                    public void onPageFinished(WebView view, String url) {
                                        webView.evaluateJavascript("(function(){return window.document.body.outerHTML})();",
                                                new ValueCallback<String>() {
                                                    @Override
                                                    public void onReceiveValue(String html) {
                                                        Log.e("@@@HTML:", html);
                                                    }
                                                });
                                        Log.i("@@@" + Data.TAG, "disConnecting");
                                        WiFiRoutine.getInstance().disconnect(ssid);
                                    }
                                });
                                webView.loadUrl(url);
                            }
                        });
                        break;
                    }
                }
            return null;
        }
    }

    private class LoadData extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            if (isBt) {
                blindBeacon = Data.getBeaconInfoByName(ssid);
            } else {
                blindBeacon = Data.getBeaconInfoByMac(mac); //MAC
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (blindBeacon != null) {
                beacon_title.setText(blindBeacon.getName());
                beacon_description.setText(blindBeacon.getDescription());
                beacon_location.setText(blindBeacon.getAddr());
                beacon_time.setText(blindBeacon.getWorking_time());
                beacon_phone.setText(blindBeacon.getPhone_numb());

                beacon_route.setOnClickListener(v -> {
                    String url = "http://maps.google.com/maps?daddr=" + blindBeacon.getLocation() + "";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                });

                if (sharedPreferences.getBoolean((Data.IS_AUTO_AUDIO), false)){
                    ConnectAndDisconnectByWifi connectAndDisconnectByWifi = new ConnectAndDisconnectByWifi(blindBeacon.getNet_name());
                    connectAndDisconnectByWifi.execute();
                }
                if (blindBeacon.isFav() == 1){
                    btn_add_to_fav.setText(getString(R.string.delete_from_fav));
                }
            }
        }
    }
    private class UpdList extends AsyncTask{
        ArrayList <BlindBeacon> blindBeaconArrayList;
        @Override
        protected Object doInBackground(Object[] objects) {
            blindBeaconArrayList = Data.getSerialized_beacons(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            for (int i = 0; i < blindBeaconArrayList.size(); i++) {
                if (blindBeaconArrayList.get(i).getUuid().equals(blindBeacon.getUuid())){
                    blindBeaconArrayList.get(i).setFav(blindBeacon.isFav());
                    Log.d(Data.TAG, "Done");
                }
            }

            BlindBeacon.saveList(getApplicationContext(), blindBeaconArrayList);
            BlindBeacon.UpdList(getApplicationContext());
            super.onPostExecute(o);
        }
    }
}
