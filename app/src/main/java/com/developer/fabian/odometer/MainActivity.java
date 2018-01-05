package com.developer.fabian.odometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.developer.fabian.odometer.service.OdometerService;

public class MainActivity extends AppCompatActivity {

    private OdometerService odometer;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeDistance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (connected) {
            unbindService(connection);
            connected = false;
        }
    }

    private void changeDistance() {
        final TextView distanceView = findViewById(R.id.txtDistance);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;

                if (odometer != null)
                    distance = odometer.getDistance();

                distanceView.setText(getString(R.string.distance_value, distance));
                handler.postDelayed(this, 1000);
            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) iBinder;
            odometer = odometerBinder.giveOdometer();
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            connected = false;
        }
    };
}
