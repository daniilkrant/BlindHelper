package ua.protech.protech.blindhelper;

import android.app.KeyguardManager;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import sm.euzee.github.com.servicemanager.ServiceManager;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.LOCATION_HARDWARE;

public class MainActivity extends AppCompatActivity {
    private ImageButton radar, fav, search, settings;
    private PermissionUtil permissionUtil;
    private ViewPager pager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Data.SETTINGS_FILE_SHARED_PREF, Context.MODE_PRIVATE);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Data.getSerialized_beacons(getApplicationContext()) == null){
            Toast.makeText(getApplicationContext(), getString(R.string.db_not_found), Toast.LENGTH_LONG).show();
        }

        radar = (ImageButton) findViewById(R.id.navigation_radar);
        radar.setEnabled(true);
        search = (ImageButton) findViewById(R.id.navigation_search);
        fav = (ImageButton) findViewById(R.id.navigation_fav);
        settings = (ImageButton) findViewById(R.id.navigation_settings);

        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(0);

        radar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка выбора вкладки радар");
                }
                return false;
            }
        });
        fav.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка выбора вкладки избранное");
                }
                return false;
            }
        });
        search.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка выбора вкладки поиск");
                }
                return false;
            }
        });
        settings.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка выбора кладки настройки");
                }
                return false;
            }
        });
        radar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(0);
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords(getString(R.string.main_radar_enabled));
                }
                search.announceForAccessibility(getString(R.string.main_radar_enabled));
            }
        });
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(1);
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords(getString(R.string.main_fav_enabled));
                }
                settings.announceForAccessibility(getString(R.string.main_fav_enabled));
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(2);
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords(getString(R.string.main_search_enabled));
                }
                search.announceForAccessibility(getString(R.string.main_search_enabled));
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(3);
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords(getString(R.string.main_settings_enabled));
                }
                radar.announceForAccessibility(getString(R.string.main_settings_enabled));
            }
        });

        permissionUtil = PermissionUtil.getInstance();
        checkForPermissions();

        Intent intent = new Intent(getApplicationContext(), ScaningService.class);
        ServiceManager.runService(getApplicationContext(), intent);

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: {
                    return RadarFragment.newInstance();
                }
                case 1: {
                    return FavouriteFragment.newInstance();
                }
                case 2: {
                    return DBFragment.newInstance();

                }
                case 3: {
                    return SettingsFragment.newInstance();
                }
                default: {
                    return RadarFragment.newInstance();
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private boolean checkForPermissions() {
        return isPermissionGranted(ACCESS_COARSE_LOCATION, R.string.access_coarse_location_rationale, Permissions.ACCESS_COARSE_LOCATION_RESULT_CODE);
    }

    public boolean isPermissionGranted(String permission, int rationaleResId, int requestCode) {
        if (permissionUtil.permissionNotGranted(this, permission)) {
            if (permissionUtil.shouldShowPermissionRationale(this, permission)) {

            } else {
                permissionUtil.requestPermissions(this, new String[]{permission}, requestCode);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Permissions.ACCESS_COARSE_LOCATION_RESULT_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(Data.TAG, "Access course location permission granted");
                    // Continue WiseFy logic here
                } else {
                    Log.e(Data.TAG, "Access course location permission denied");
                    // TODO - Display permission error here
                }
                break;
            default:
                Log.wtf(Data.TAG, "Weird permission requested, not handled");
                // TODO - Display permission error here
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
