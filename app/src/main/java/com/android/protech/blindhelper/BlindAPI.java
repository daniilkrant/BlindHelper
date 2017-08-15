package com.android.protech.blindhelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BlindAPI {

    @GET("select.php?")
    Call<ArrayList<BlindBeacon>> getBeaconInfo(@Query("mac") String mac);

    @GET("select.php?")
    Call<ArrayList<BlindBeacon>> getBeaconsList(@Query("mac") String mac);
}
