package com.yucan.sensoro.demo;

import android.app.Application;

import com.sensoro.beacon.kit.SensoroBeaconConnection;

/**
 * Created by LiuYucan on 15-6-25.
 */
public class ConnectionApp extends Application {

    public static final String EXTRA_BEACONS = "Beacon";

    private SensoroBeaconConnection connection;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setConnection(SensoroBeaconConnection connection) {
        this.connection = connection;
    }

    public SensoroBeaconConnection getConnection() {
        return connection;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
