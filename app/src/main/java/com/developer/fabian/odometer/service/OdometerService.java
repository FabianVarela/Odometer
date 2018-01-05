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
    private LocationListener listener;
    private LocationManager manager;

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
        listener = new LocationListener() {
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

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);
    }

    @Override
    public void onDestroy() {
        if (manager != null && listener != null) {
            manager.removeUpdates(listener);

            manager = null;
            listener = null;
        }
    }

    public double getDistance() {
        return distance / 1609.344;
    }
}
