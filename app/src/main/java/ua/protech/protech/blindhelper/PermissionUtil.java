package ua.protech.protech.blindhelper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class PermissionUtil {

    private static final PermissionUtil PERMISSION_UTIL = new PermissionUtil();

    private PermissionUtil() {

    }

    public static PermissionUtil getInstance() {
        return PERMISSION_UTIL;
    }

    public boolean permissionNotGranted(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public boolean shouldShowPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}