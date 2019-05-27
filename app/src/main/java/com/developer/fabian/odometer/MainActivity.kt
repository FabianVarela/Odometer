package com.developer.fabian.odometer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import com.developer.fabian.odometer.service.OdometerService

class MainActivity : AppCompatActivity() {

    private var odometer: OdometerService? = null
    private var connected: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val odometerBinder = iBinder as OdometerService.OdometerBinder
            odometer = odometerBinder.giveOdometer()
            connected = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            connected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeDistance()
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, OdometerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()

        if (connected) {
            unbindService(connection)
            connected = false
        }
    }

    private fun changeDistance() {
        val distanceView = findViewById<TextView>(R.id.txtDistance)
        val handler = Handler()

        handler.post(object : Runnable {
            override fun run() {
                var distance = 0.0

                if (odometer != null)
                    distance = odometer!!.getDistance()

                distanceView.text = getString(R.string.distance_value, distance)
                handler.postDelayed(this, 1000)
            }
        })
    }
}
