package com.example.sdp_app;

import android.app.Application;
import android.os.Handler;

public class ApplicationEx extends Application{
    private final int STATUS_CHECK_INTERVAL = 500;
    private MyBtEngine mBtEngine;

    //region $BT Device Name
    private String btDeviceName;

    public String getBtDeviceName() {
        return btDeviceName;
    }
    public void setBtDeviceName(String deviceName) {
        this.btDeviceName = deviceName;
    }
    //endregion

    //region $ BT Device MAC Address
    private String btDeviceMACAddress;

    public String getBtDeviceMACAddress() {
        return btDeviceMACAddress;
    }

    public void setBtDeviceMACAddress(String macAddress) {
        this.btDeviceMACAddress = macAddress;
    }
    //endregion

    @Override
    public void onCreate() {
        super.onCreate();
        mBtEngine = new MyBtEngine();
        handlerStatusCheck.postDelayed(new Runnable() {
            @Override
            public void run() {
                onBtStatusCheckTimer();
                handlerStatusCheck.postDelayed(this, STATUS_CHECK_INTERVAL);
            }
        }, STATUS_CHECK_INTERVAL);
    }
    private final Handler handlerStatusCheck = new Handler();
    private void onBtStatusCheckTimer(){
        if(mBtEngine.getState() == MyBtEngine.BT_STATE_NONE) {
            String mac = getBtDeviceMACAddress();
            mBtEngine.start(mac);
        }
    }
    boolean writeBt(byte [] buffer){ return mBtEngine.writeBt(buffer); }
}