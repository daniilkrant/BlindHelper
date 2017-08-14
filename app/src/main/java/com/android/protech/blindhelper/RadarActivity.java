package com.android.protech.blindhelper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RadarActivity extends AppCompatActivity {

    private static final String TAG = "BEACON_PROJECT";
    private ArrayList<BlindBeacon> beaconArrayList = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter pagerAdapter;
    private Vibrator v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager_radar);
        pagerAdapter = new FragmentAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager,true);

        WiFiRoutine.getInstance().initWifi(getApplicationContext());

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        beaconArrayList = WiFiRoutine.getInstance().getNearbyBeacons();
                        pagerAdapter.notifyDataSetChanged();
                    }
                });
            }
        },0,5000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconArrayList.clear();
    }

    private class FragmentAdaptor extends FragmentPagerAdapter {

        FragmentAdaptor(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return RadarFragment.newInstance(position, beaconArrayList);
        }

        @Override
        public int getCount() {
            if (beaconArrayList!=null){
                return beaconArrayList.size();
            }
            else return 0;
        }
    }

}
