package com.spilab.monact.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.DecimalFormat;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import com.spilab.contigo.R;
import com.spilab.monact.fragments.InicioFragment;
import com.spilab.monact.services.MQTTService;
import com.spilab.monact.services.SensorService;
import com.spilab.monact.tensorflow.APIRequests;
import com.spilab.monact.tensorflow.ApiService;
import com.spilab.monact.tensorflow.DataProcessor;
import com.spilab.monact.tensorflow.TensorFlowLiteModel;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private SharedPreferences sharedPreferences;

    private Intent mServiceIntent;
    private static final String TAG = "MainActivity";

    private TensorFlowLiteModel tfLiteModel;

    private DataProcessor dataProcessor;

    private Handler handler;
    private Runnable runnableCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //SHARED PREFERENCES SETUP
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        dataProcessor = new DataProcessor(getApplicationContext(), "mobile_dataset_heart.csv");

        //CHECK PERMISSIONS
        checkPermisionsLocation();
        checkBatteryOptimizationPermissions();

        //SERVICES SETUP
        startServiceMQTT();
        startSensorService();

        //TENSORFLOW SETUP
        loadModelTensorflow();

        //WORKER
        executePredictionWorker();

        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new InicioFragment()).commit();

    }


    ////// WORK MANAGER //////


    private void executePredictionWorker(){

        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {


                // Por ejemplo, muestra un mensaje en el Log
                Log.d("Prediction Handler", "Prediction executed");
                makePrediction();

                handler.postDelayed(this, 900000); // Each 15 min
            }
        };


        handler.post(runnableCode);
    }


    ///////// TENSORFLOW UTILS /////////
    private void loadModelTensorflow() {
        tfLiteModel = new TensorFlowLiteModel(this, "model_cloud.tflite");
        Log.i("TensorFlow", "Loading model!");

        tfLiteModel.restoreModelV2("checkpoint-global.ckpt");

        if (new File(getFilesDir(), "checkpoint.ckpt").exists()) {
            tfLiteModel.restoreModel();
            Log.i("TensorFlow", "Restored checkpoint!");
        } else {
            //Esto solo ocurrirá al principio de uso cuando no haya ningún checkpoint generado, para que se genere el checkpoint con los pesos iniciales)
            tfLiteModel.saveModel();
            //Versión 0 - versión base inicial
            sharedPreferences.edit().putInt("lastCheckpoint", 0).apply();
        }

        obtainGlobalModelVersion();
    }


    public void checkVersionModels(int newCheckpointVersion) {

        int lastCheckpointVersion = sharedPreferences.getInt("lastCheckpoint", 0);

        if (lastCheckpointVersion < newCheckpointVersion) {
            downloadGlobalModel();
            //Actualizar sharedPreference
            sharedPreferences.edit().putInt("lastCheckpoint", newCheckpointVersion).apply();

            Log.e("GLOBAL MODEL", "Updated to version: " + newCheckpointVersion);
        }else{
            Log.e("GLOBAL MODEL", "The version is the same or lower");
        }

    }


    private void retrainModel() {
        dataProcessor.processData();

        List<List<Double>> train_X = dataProcessor.getScaledTrainData();

        List<Double> train_y = dataProcessor.getTrainLabels();

        float[][] train_X_input = dataProcessor.convertToFloatFloatArray(train_X);

        float[] train_y_input = dataProcessor.convertToFloatArray(train_y);

        tfLiteModel.retrainModel(train_X_input, train_y_input);
        tfLiteModel.saveModel();
    }


    public String makePrediction() {

        tfLiteModel.restoreModel();

        String result = "";

        List<float[]> trainData = new ArrayList<>();

        float[] testData;


        testData = new float[]{(float) 0.778386, (float) -1.467216, (float) 1.549044, (float) -0.419420, (float) -0.119394, (float) 1.103758}; //0
        trainData.add(testData);

        testData = new float[]{(float) 0.113658, (float) 0.678032, (float) -0.108499, (float) -0.419420, (float) 0.643060, (float) 1.103758};//1
        trainData.add(testData);


        testData = new float[]{(float) -0.772645, (float) 0.678032, (float) -0.108499, (float) 2.371892, (float) 0.092399, (float) 1.103758};//0
        trainData.add(testData);

        float[][] inputArray = trainData.toArray(new float[0][]);
        float[][] outputData = tfLiteModel.makeInfer(inputArray);

        for (int i = 0; i < outputData.length; i++) {
            if (outputData[i][0] > outputData[i][1]) {
                result = result + 0 + " | " + String.format("%.2f", (outputData[i][0]) * 100) + "%" + '\n';
                Log.i("PREDICTION " + i, String.valueOf(0 + " | " + (outputData[i][0] * 100) + "%"));
            } else {
                result = result + 1 + " | " + String.format("%.2f", (outputData[i][1] - outputData[i][0]) * 100) + "%" + '\n';

                Log.i("PREDICTION " + i, String.valueOf(1 + " | " + (outputData[i][1] * 100) + "%"));
            }

        }


        return result;
    }



    public void obtainGlobalModelVersion() {

        ApiService apiService = ApiService.ApiClient.apiService;
        Call<ResponseBody> call = apiService.obtenerVersionModelo();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("GET", "request correctly");
                    try {
                        String filename = response.body().string();
                        int version = extractVersionModel(filename);
                        Log.d("GET", "VERSION OF GLOBAL MODEL: " + version);
                        //Send version obtained
                        checkVersionModels(version);

                    } catch (IOException e) {
                        checkVersionModels(0);
                        throw new RuntimeException(e);

                        //Send version 0

                    }


                } else {
                    Log.d("GET", "server contact failed");
                    checkVersionModels(0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                checkVersionModels(0);
                Log.e("GET", "Error " + t.getMessage());
            }
        });

    }


    public void downloadGlobalModel() {

        ApiService apiService = ApiService.ApiClient.apiService;

        Call<ResponseBody> call = apiService.getGlobalModel();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("GET", "server contacted and has file");
                    Log.d("GET", String.valueOf(extractFileName(response.headers().get("Content-Disposition"))));

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                    Log.d("GET", "file download was a success? " + writtenToDisk);
                    //Restaurar a la nueva versión
                    if (writtenToDisk)
                        tfLiteModel.restoreModel();

                } else {
                    Log.d("GET", "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e("GET", "Error " + t.getMessage());
            }
        });

    }


    private static String extractFileName(String contentDisposition) {
        String[] elements = contentDisposition.split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                String[] fileNamePart = element.split("=");
                if (fileNamePart.length > 1) {
                    return fileNamePart[1].trim().replace("\"", "");
                }
            }
        }
        return "checkpoint-glboal_0.cktp";
    }


    public int extractVersionModel(String checkpointFilename) {

        Log.e("version", checkpointFilename);

        String[] parts = checkpointFilename.split("_");

        parts = parts[1].split("\\.");

        return Integer.parseInt(parts[0]);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {

            File futureStudioIconFile = new File(getFilesDir(), "checkpoint.ckpt");
            Log.i("FILE", getFilesDir() + File.separator + "checkpoint.ckpt");

            InputStream inputStream = null;
            OutputStream outputStream = null;


            try {

                if (futureStudioIconFile.exists()) {
                    Log.d("DELETE MODEL", "File deletion status: " + futureStudioIconFile.delete());
                }


                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();

                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


    ///////// TENSORFLOW UTILS  FINAL /////////

    private void checkPermisionsLocation() {
        if (checkPermissions()) {
            if (!isLocationEnabled()) {
                Toast.makeText(this, "Activate location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void checkBatteryOptimizationPermissions() {

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }

    private void startServiceMQTT() {
        MQTTService service = new MQTTService();
        Intent mqttServiceIntent = new Intent(this, service.getClass());


        boolean run = isMyServiceRunning(service.getClass());
        Log.d(TAG, " - Run1: " + run);
        if (!isMyServiceRunning(service.getClass())) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(mqttServiceIntent);
            } else
                startService(mqttServiceIntent);
        }
        Log.d(TAG, " - Run1: " + run);

    }

    @Override
    protected void onStop() {
        super.onStop();
        startServiceMQTT();
        startSensorService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startServiceMQTT();
        startSensorService();
    }

//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        //CHECK GLOBAL VERSION
//        obtainGlobalModelVersion();
//
//    }


    private void startSensorService() {
        SensorService service = new SensorService();
        mServiceIntent = new Intent(this, service.getClass());


        boolean run = isMyServiceRunning(service.getClass());
        Log.d(TAG, " - Run2: " + run);
        if (!isMyServiceRunning(service.getClass())) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(mServiceIntent);
            } else
                startService(mServiceIntent);

        }
        Log.d(TAG, " - Run2: " + run);

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


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                if (!isLocationEnabled()) {

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, AjustesActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.info) {
            Intent intent2 = new Intent(this, InfoActivity.class);
            startActivity(intent2);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
