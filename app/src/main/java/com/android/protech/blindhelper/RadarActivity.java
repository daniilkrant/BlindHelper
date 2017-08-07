package com.android.protech.blindhelper;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class RadarActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = "BEACON_PROJECT";
    private ArrayList<BlindBeacon> beaconArrayList = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter pagerAdapter;
    private BeaconManager beaconManager;
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
        this.beaconManager = BeaconManager.getInstanceForApplication(this);
        this.beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        this.beaconManager.setBackgroundBetweenScanPeriod(6000);
        this.beaconManager.setBackgroundScanPeriod(6000);
        this.beaconManager.setForegroundBetweenScanPeriod(6000);
        this.beaconManager.setForegroundScanPeriod(6000);
        this.beaconManager.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        this.beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                     beaconArrayList.clear();
                    for (Beacon beacon : beacons) {
                        Log.d("TAG", Integer.toString(beaconArrayList.size()));
                        beaconArrayList.add(new BlindBeacon(beacon.getBluetoothAddress(),beacon.getDistance(), beacon.getBluetoothName()));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pagerAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });


        try {
            this.beaconManager.startRangingBeaconsInRegion(new Region("MyRegionId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
                Log.d("TAG", Integer.toString(beaconArrayList.size()));
                return beaconArrayList.size();
            }
            else return 0;
        }
    }

}
