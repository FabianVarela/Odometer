package com.developer.fabian.odometer.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.ContextCompat

class OdometerService : Service() {

    companion object {
        private var lastPosition: Location? = null
        private var distance: Double = 0.toDouble()
    }

    private val binder = OdometerBinder()
    private var listener: LocationListener? = null
    private var manager: LocationManager? = null

    override fun onBind(intent: Intent): IBinder? {
        return this.binder
    }

    inner class OdometerBinder : Binder() {
        fun giveOdometer(): OdometerService {
            return this@OdometerService
        }
    }

    override fun onCreate() {
        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (lastPosition == null)
                    lastPosition = location

                distance += location.distanceTo(lastPosition).toDouble()
                lastPosition = location
            }

            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}

            override fun onProviderEnabled(s: String) {}

            override fun onProviderDisabled(s: String) {}
        }

        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            manager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, listener)
    }

    override fun onDestroy() {
        if (manager != null && listener != null) {
            manager!!.removeUpdates(listener)

            manager = null
            listener = null
        }
    }

    fun getDistance(): Double {
        return distance / 1609.344
    }
}
