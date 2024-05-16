package com.spilab.monact.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spilab.contigo.R;
import com.spilab.monact.resources.AlertResource;
import com.spilab.monact.resources.UserResource;
import com.spilab.monact.responses.AlertResponse;
import com.spilab.monact.responses.UserResponse;
import com.spilab.monact.tensorflow.APIRequests;
import com.spilab.monact.tensorflow.ApiService;
import com.spilab.monact.tensorflow.DataProcessor;
import com.spilab.monact.tensorflow.TensorFlowLiteModel;
import com.spilab.monact.utils.MqttClient;
import com.spilab.monact.utils.Restarted;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MQTTService extends Service {

    private static final String TAG = "MQTT";
    private MqttClient mqttClient;
    private static MqttAndroidClient mqttAndroidClient;
    public static Boolean subscribed = false;
    private static Boolean connectionLost = false;

    SharedPreferences sharedPreferences;

    //Client ID
    private AdvertisingIdClient.Info mInfo;

    Gson gson = new GsonBuilder().create();

    public MQTTService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onCreate");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mInfo = null;
        new GetAdvertisingID().execute();

        return START_STICKY;
    }

    public static MqttAndroidClient getClient() {
        return mqttAndroidClient;
    }

    private void connectMQTT() {
        mqttAndroidClient = mqttClient.getMqttClient(getApplicationContext(), MQTTConfiguration.MQTT_BROKER_URL, sharedPreferences.getString("uuid", "dev"), MQTTConfiguration.USER, MQTTConfiguration.PASSWORD);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.d(TAG, "Service connect complete");

                if (!subscribed || connectionLost) {
                    subscribeTopic(getApplicationContext(), "monact/device");
                    subscribeTopic(getApplicationContext(), sharedPreferences.getString("uuid", "dev"));
                    connectionLost = false;
                    Log.d(TAG, "Subscribed to request");




                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.d(TAG, "Service connection lost");
                connectionLost = true;
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                Log.d(TAG, " - Message!!");

                // Parse message
                String msg = new String(mqttMessage.getPayload());
                JSONObject json = new JSONObject(msg);

                Log.i("Msg received by MQTT: ", json.toString());
                executeAPI(s, json);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }


    private void configureMQTT(String info) {
        startMyOwnForeground();
        mqttClient = new MqttClient();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.i("UUID ", info);
        editor.putString("uuid", info.split("-")[0]).apply();
        editor.commit();


        connectMQTT();
    }

    private void executeAPI(String topic, JSONObject data) throws JSONException {

        switch (data.getString("resource")) {

            case "Alert":
                if (topic.equals(sharedPreferences.getString("uuid", "dev"))) {
                    try {
                        AlertResponse alertresponse = gson.fromJson(String.valueOf(data), AlertResponse.class);
                        Exception error = new AlertResource(getApplicationContext()).executeMethod(alertresponse);

                        Toast.makeText(getApplicationContext(),"Alert received", Toast.LENGTH_LONG).show();

                        if (error != null) {
                            Log.e("Error", error.toString());
                            break;
                        }

                    } catch (Exception e) {
                        Log.e("Err AlertResponse", e.getMessage());
                    }
                } else
                    Log.i("topic ", topic);
                break;

            case "User":
                try {

                    UserResponse userresponse = gson.fromJson(String.valueOf(data), UserResponse.class);
                    Exception error = new UserResource(getApplication()).executeMethod(userresponse);

                    if (error != null) {
                        Log.e("Error", error.toString());
                        break;
                    }


                } catch (Exception e) {
                    Log.e("Err UserResponse", e.getMessage());
                    e.printStackTrace();
                }
                break;

            case "Retrain":
                //Call Retrain
                if (topic.equals(sharedPreferences.getString("uuid", "dev"))) {

                    Toast.makeText(getApplicationContext(),"Re-training request received", Toast.LENGTH_LONG).show();
                    /// RETRAIN ///
                    retrainModel();
                    //////////////

                }


                break;
        }
    }


    private void retrainModel() {

        /////// RETRAIN MODEL /////////
        DataProcessor dataProcessor = new DataProcessor(getApplicationContext(), "mobile_dataset_heart.csv");
        dataProcessor.processData();

        List<List<Double>> train_X = dataProcessor.getScaledTrainData();

        List<Double> train_y = dataProcessor.getTrainLabels();

        float[][] train_X_input = dataProcessor.convertToFloatFloatArray(train_X);

        float[] train_y_input = dataProcessor.convertToFloatArray(train_y);

        TensorFlowLiteModel tfLiteModel = new TensorFlowLiteModel(this, "model_cloud.tflite");
        tfLiteModel.restoreModel();
        tfLiteModel.retrainModel(train_X_input, train_y_input);
        tfLiteModel.saveModel();

        /////// UPLOAD CHECKPOINT /////////

        APIRequests.uploadCheckpoint(getApplicationContext(),sharedPreferences);
    }




    private class GetAdvertisingID extends AsyncTask<Void, Void, AdvertisingIdClient.Info> {

        @Override
        protected AdvertisingIdClient.Info doInBackground(Void... voids) {
            AdvertisingIdClient.Info info = null;
            try {
                info = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }
            return info;
        }

        @Override
        protected void onPostExecute(AdvertisingIdClient.Info info) {
            Log.i(TAG, "UUID generated");
            mInfo = info;



            configureMQTT(info.getId());
        }

    }


    private void startMyOwnForeground() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;

        String NOTIFICATION_CHANNEL_ID = "service.background";
        String channelName = "Background Service";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
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
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.contigo2_t)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();


        startForeground(1, notification);
    }


    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void subscribeTopic(Context ctx, String topic) {
        if (!topic.isEmpty()) {
            try {
                mqttClient.subscribe(mqttAndroidClient, topic, 1);

                //Toast.makeText(ctx, "Subscribed to: " + topic, Toast.LENGTH_SHORT).show();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void unsubscribeTopic(Context ctx, String topic) {

        if (!topic.isEmpty()) {
            try {
                mqttClient.unSubscribe(mqttAndroidClient, topic);

                //Toast.makeText(ctx, "Subscribed to: " + topic, Toast.LENGTH_SHORT).show();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {

        Log.e("ON DESTROY: ", "MQTT on destroy");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("mqttService");
        broadcastIntent.setClass(this, Restarted.class);
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

//    @Override
//    public void onLowMemory() {
//        if(!isMyServiceRunning(MQTTService.class)) {
//            Log.e("ON LOW MEMORY: ", "MQTT on low memory");
//            Intent broadcastIntent = new Intent();
//            broadcastIntent.setAction("mqttService");
//            broadcastIntent.setClass(this, Restarted.class);
//            sendBroadcast(broadcastIntent);
//        }
//        super.onLowMemory();
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent)
//    {
//        if(!isMyServiceRunning(MQTTService.class)) {
//            Log.e("ON TASK REMOVED: ", "MQTT on task removed");
//            Intent broadcastIntent = new Intent();
//            broadcastIntent.setAction("mqttService");
//            broadcastIntent.setClass(this, Restarted.class);
//            sendBroadcast(broadcastIntent);
//        }
//        super.onDestroy();
//    }

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
}