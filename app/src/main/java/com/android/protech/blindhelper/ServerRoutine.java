package com.android.protech.blindhelper;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerRoutine {

    static ArrayList<BlindBeacon> getNearbyBeacons(){

        ArrayList<BlindBeacon> blindBeacons = new ArrayList<>();
        BlindBeacon temp_beacon;
        HashMap<String,String> BSSID_list = WiFiRoutine.getInstance().getPointsRegex();

        for (Map.Entry<String,String> entry : BSSID_list.entrySet()){
            temp_beacon = getBeaconInfo(entry.getKey());
            temp_beacon.setSsid(entry.getValue());
            blindBeacons.add(temp_beacon);
        }

        return blindBeacons;
    }

    static BlindBeacon getBeaconInfo(String bssid){
        ArrayList<BlindBeacon> beacon;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(Data.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();
        BlindAPI client = retrofit.create(BlindAPI.class);
        Call<ArrayList<BlindBeacon>> call = client.getBeaconInfo(bssid);
        try {
            Response<ArrayList<BlindBeacon>> response =  call.execute();
            beacon = response.body();
            Log.d("respons", response.message() + "//" + response.code());
        } catch (IOException e) {
            beacon = null;
            e.printStackTrace();
        }

        return beacon.get(0);
    }

    static ArrayList<BlindBeacon> getBeaconsFromDB(){
        ArrayList<BlindBeacon> blindBeacons = new ArrayList<>();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(Data.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();
        BlindAPI client = retrofit.create(BlindAPI.class);
        Call<ArrayList<BlindBeacon>> call = client.getBeaconsList("all");
        try {
            Response<ArrayList<BlindBeacon>> response =  call.execute();
            blindBeacons = response.body();
        } catch (IOException e) {
            blindBeacons = null;
            e.printStackTrace();
        }

        return blindBeacons;
    }
}
