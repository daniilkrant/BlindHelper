package com.android.protech.blindhelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BTRoutine {

    static private final String TAG = "BLConnection";

    static String connectedAdress = "";
    static String recievedData = "";
    static String sentData = "";

    private static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothDevice device;
    private static BluetoothSocket tmp = null;
    private static BluetoothSocket mmSocket = null;

    static boolean connect(String address){
        mBluetoothAdapter.enable();
        device = mBluetoothAdapter.getRemoteDevice(address);
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            Method m = device.getClass().getMethod("createRfcommSocket", int.class);
            tmp = (BluetoothSocket) m.invoke(device, 1);
        } catch (Exception e) {
            Log.e(TAG, "create() failed", e);
        }
        mmSocket = tmp;
        if (mmSocket!=null)
        Log.d(TAG, "Socket created");

        return true;
    }

    static void sendData(String data){
        try {
            mBluetoothAdapter.cancelDiscovery();
            mmSocket.connect();
            byte[] dataBytes = data.getBytes();
            OutputStream outputStream = mmSocket.getOutputStream();
            if  (mmSocket.isConnected())
                outputStream.write(dataBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static String recieveData(){
        return "RecievedData";
    }

    private static boolean checkResponse(String response){
        return false;
    }
}
