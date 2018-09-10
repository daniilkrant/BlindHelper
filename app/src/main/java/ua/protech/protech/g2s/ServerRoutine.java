package ua.protech.protech.g2s;

import android.util.Log;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerRoutine {

        static ArrayList<BlindBeacon> getBeaconsFromDB(){
        ArrayList<BlindBeacon> blindBeacons = new ArrayList<>();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(Data.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();
        BlindAPI client = retrofit.create(BlindAPI.class);
        Call<ArrayList<BlindBeacon>> call = client.getBeaconsList("all", "true");
        try {
            Response<ArrayList<BlindBeacon>> response =  call.execute();
            blindBeacons = response.body();
        } catch (IOException e) {
            blindBeacons = null;
            e.printStackTrace();
        }

        return blindBeacons;
    }

    static void pingBeacon(){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl("http://192.168.4.1/" + "5000" + "/" +  "330" + "/" + "354" +
                                 "/" + "3" + "/" + "1000" + "/" + "5" + "/")
                        .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();
        BlindAPI client = retrofit.create(BlindAPI.class);
        Log.e("@@@Builder", retrofit.baseUrl().toString());

        Call<Integer> call = client.pingBeacon();
        try {
            Response response =  call.execute();
            Log.e("@@@Response:", response.message());
        } catch (SocketException e) {
            Log.e("@@@NetworkException:", "NetworkException");
            pingBeacon();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
