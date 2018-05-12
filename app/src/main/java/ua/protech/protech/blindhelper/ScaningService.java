package ua.protech.protech.blindhelper;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import javax.annotation.security.RunAs;

public class ScaningService extends Service {
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
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Система маяковой навигации G2s")
                .setContentText("Поиск маяков ...");
        Notification notification;
        notification = builder.build();
        startForeground(740, notification);
        Runnable r = new Runnable() {
            @Override
            public void run() {

            }
        };
        r.run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
