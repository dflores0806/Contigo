package com.spilab.monact.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.spilab.monact.services.MQTTService;
import com.spilab.monact.services.SensorService;

public class Restarted extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service tried to stop");
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();

        switch (intent.getAction()){
            case "mqttService":
                Log.i("RESTARTED:", " MQTT SERVICE.");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, MQTTService.class));
                } else {
                    context.startService(new Intent(context, MQTTService.class));
                }

            case "sensorService":
                Log.i("RESTARTED:", " SENSOR SERVICE.");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, SensorService.class));
                } else {
                    context.startService(new Intent(context, SensorService.class));
                }
        }


    }


}