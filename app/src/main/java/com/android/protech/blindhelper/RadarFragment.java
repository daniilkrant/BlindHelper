package com.android.protech.blindhelper;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RadarFragment extends Fragment {

    ArrayList<BlindBeacon> beaconArrayList;
    int pageNumber;
    TextView beacon_description, beacon_title;
    Button beacon_call;

    public RadarFragment() {

    }

    static RadarFragment newInstance(int page, ArrayList<BlindBeacon> beaconList){
        RadarFragment radarFragment = new RadarFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(Data.ARGUMENT_PAGE_NUMBER, page);
        arguments.putParcelableArrayList(Data.ARGUMENT_BEACON_LIST, beaconList);
        radarFragment.setArguments(arguments);
        return radarFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        beaconArrayList = getArguments().getParcelableArrayList(Data.ARGUMENT_BEACON_LIST);
        pageNumber = getArguments().getInt(Data.ARGUMENT_PAGE_NUMBER);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radar, container, false);
        beacon_description = (TextView) view.findViewById(R.id.beacon_description);
        beacon_title = (TextView) view.findViewById(R.id.beacon_title);
        beacon_call = (Button) view.findViewById(R.id.beacon_call);

        beacon_title.setText(beaconArrayList.get(pageNumber).getName());
        beacon_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WiFiRoutine.getInstance().connect(beaconArrayList.get(pageNumber).getUuid());
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                WiFiRoutine.getInstance().disconnect(beaconArrayList.get(pageNumber).getUuid());
            }
        });
        return view;
    }



}
