package com.android.protech.blindhelper;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BeaconDetailActivity extends AppCompatActivity {
    TextView beacon_description, beacon_title, beacon_location;
    Button beacon_route;
    String bssid;
    BlindBeacon blindBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_detail);
        Intent intent = getIntent();
        bssid = intent.getStringExtra(Data.PASS_BSSID);

        beacon_description = (TextView) findViewById(R.id.beacon_description);
        beacon_title = (TextView) findViewById(R.id.beacon_title);
        beacon_location = (TextView) findViewById(R.id.beacon_location);
        beacon_route = (Button) findViewById(R.id.beacon_route);

        LoadData loadData = new LoadData();
        loadData.execute();

    }


    private class LoadData extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            blindBeacon = ServerRoutine.getBeaconInfo(bssid);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            beacon_title.setText(blindBeacon.getName());
            beacon_description.setText(blindBeacon.getDescription());
            beacon_location.setText(blindBeacon.getLocation());

            beacon_route.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http://maps.google.com/maps?daddr=" + blindBeacon.getLocation()+ "";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
        }
    }
}
