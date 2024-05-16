package com.spilab.monact.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;


import com.spilab.contigo.R;
import com.spilab.monact.data.repository.SensorRepository;
import com.spilab.monact.utils.Restarted;

public class SensorService extends Service implements SensorEventListener {

    private static final String TAG = "ActivitySensor";
    private SensorRepository repo;


    /**
     * Sensores a monitorizar
     */
    private static final int[] SENSORS = {
            Sensor.TYPE_ACCELEROMETER,
    };


    /**
     * Indica si la pantalla está encendida
     */
    private boolean screenOn;

    /**
     * Indica si el usuario está actualmente activo
     */
    private boolean currentlyActive;

    /**
     * Receptor de señales de la pantalla
     */
    private BroadcastReceiver mScreenReceiver;

    /**
     * Parámetros para el cálculo del valor absoluto del acelerómetro.
     */
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    public SensorService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        screenOn = getScreenStatusSynchronous();
        currentlyActive = false;

        repo = SensorRepository.getInstance(getApplicationContext());
        Log.d(TAG, "Created");

        start();

        if (screenOn && !currentlyActive) {
            currentlyActive = true;
            repo.open();
            Log.i("Sensor: ", "Activiad Iniciada");
        }

        startMyOwnForeground();

        return START_STICKY;

    }


    private void startMyOwnForeground() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        String NOTIFICATION_CHANNEL_ID = "service.background";
        String channelName = "Background Service";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(chan);

        }

        Intent resultIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        resultIntent.setData(Uri.parse("package:" + getPackageName()));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Contigo")
                .setContentText("La aplicación se está ejecutando")
                .setSmallIcon(R.drawable.contigo2_t)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(resultPendingIntent)
                .build();
        startForeground(1, notification);
    }

    /**
     * Toma el estado de la pantalla de manera síncrona. NO RECOMENDADO EN LA MAYORÍA DE CASOS.
     *
     * @return True si la pantalla está encendida, false si está apagada
     */
    private boolean getScreenStatusSynchronous() {
        PowerManager mngr = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean on = false;
        if (mngr != null) {
            on = mngr.isInteractive();
        } else {
            Log.e(TAG, "NULL PowerManager! Something might be wrong!");
        }
        return on;
    }


    /**
     * Inicia el sensor. Debe llamarse a mano si no se pasa un ciclo de vida.
     */
    public void start() {
        registerAllReceivers();
    }

    /**
     * Para el sensor. Debe llamarse a mano si no se pasa un ciclo de vida.
     */
    public void stop() {
        unregisterAllReceivers();
    }


    private void registerAllReceivers() {
        IntentFilter screenFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        if (mScreenReceiver == null) {
            mScreenReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction() == null) {
                        Log.e(TAG, "Got null intent action!!!");
                        return;
                    }
                    if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                        screenOn = true;
                        if (!currentlyActive) {
                            currentlyActive = true;
                            repo.open();
                            Log.i(TAG, "Iniciada actividad");
                        }

                    } else {
                        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                            screenOn = false;
                            if (currentlyActive) {
                                currentlyActive = false;
                                repo.close();
                                Log.i(TAG, "Finalizada actividad");
                            }
                        }
                    }
                }
            };
        }
        getApplicationContext().registerReceiver(mScreenReceiver, screenFilter);
        Log.i(TAG, "Screen listener set");
        SensorManager mngr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        if (mngr == null) {
            Log.e(TAG, "Got null sensor manager!!!!");
            throw new NullPointerException("Null sensor manager on ActivitySensor!");
        }

        Sensor sensor = mngr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mngr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i(TAG, "Sensor listener set");

    }

    /**
     * Elimina los listeners de eventos
     */
    private void unregisterAllReceivers() {

        getApplicationContext().unregisterReceiver(mScreenReceiver);
        SensorManager mngr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        if (mngr == null) {
            Log.e(TAG, "Got null sensor manager!!!!");
            throw new NullPointerException("Null sensor manager on ActivitySensor!");
        }
        Log.i(TAG, "Screen listener unset");
        // for (int sensorId : SENSORS) {
        Sensor sensor = mngr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mngr.unregisterListener(this, sensor);
        Log.i(TAG, "Sensor listener unset");
        //}
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


//    @Override
//    public void onSensorChanged(SensorEvent event) {
//
//        if (event.values[0] > 2 || event.values[0] < -2 )  {
//
//            if (!currentlyActive && !screenOn) {
//                Log.i("SENSOR: ", "Iniciada actividad Sensores");
//                timewait=false;
//                startCountDownTimer(3);
//                currentlyActive = true;
//                repo.open();
//            }
//        }else{
//
//            if (currentlyActive && !screenOn /*&& timewait*/) {
//                currentlyActive = false;
//                repo.close();
//                Log.i(TAG, "Finalizada actividad Sensores");
//            }
//
//        }
//
//    }


    /**
     * Se lanza cuando el acelerómetro monitorizado tiene actividad.
     * Si es necesario, guarda el timestamp UNIX en el que empezó la actividad.
     *
     * @param event Evento de sensorización
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        mGravity = event.values.clone();
        // Shake detection
        float x = mGravity[0];
        float y = mGravity[1];
        float z = mGravity[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
        float delta = Math.abs(mAccelCurrent - mAccelLast);
        mAccel = mAccel * 0.9f + delta;

        // Log.i("Acelerometer Value: ", String.valueOf(Math.abs(mAccel)));
        // Make this higher or lower according to how much
        // motion you want to detect
        if (mAccel > 1 && !currentlyActive && !screenOn) {
            Log.i("SENSOR: ", "Iniciada actividad Sensores");
            currentlyActive = true;
            repo.open();
        }

        if (mAccel < 1 && currentlyActive && !screenOn) {
            currentlyActive = false;
            repo.close();
            Log.i("SENSOR: ", "Finalizada actividad Sensores");
        }


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    @Override
    public void onDestroy() {

        Log.e("ON DESTROY: ", "SENSOR on destroy");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("sensorService");
        broadcastIntent.setClass(this, Restarted.class);
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        if (!isMyServiceRunning(SensorService.class)) {
            Log.e("ON LOW MEMORY: ", "SENSOR on low memory");
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("sensorService");
            broadcastIntent.setClass(this, Restarted.class);
            sendBroadcast(broadcastIntent);
        }
        super.onLowMemory();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (!isMyServiceRunning(SensorService.class)) {
            Log.e("ON TASK REMOVED: ", "SENSOR on task removed");
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("sensorService");
            broadcastIntent.setClass(this, Restarted.class);
            sendBroadcast(broadcastIntent);
        }
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
