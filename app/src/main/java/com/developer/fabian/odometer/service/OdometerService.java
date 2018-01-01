package com.developer.fabian.odometer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;

public class OdometerService extends Service {

    private final IBinder binder = new OdometerBinder();
    private static Location lastPosition = null;
    private static double distance;

    public OdometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    public class OdometerBinder extends Binder {
        public OdometerService giveOdometer() {
            return OdometerService.this;
        }
    }

    @Override
    public void onCreate() {
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastPosition == null)
                    lastPosition = location;

                distance += location.distanceTo(lastPosition);
                lastPosition = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);
    }

    public double getDistance() {
        return distance;
    }
}
