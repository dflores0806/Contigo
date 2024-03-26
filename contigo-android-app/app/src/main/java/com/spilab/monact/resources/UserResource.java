/**
 * MonACT
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 * <p>
 * OpenAPI spec version: 1.0
 * Contact: info@spilab.es
 * <p>
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.spilab.monact.resources;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spilab.monact.data.repository.SensorRepository;
import com.spilab.monact.models.User;
import com.spilab.monact.models.UserHealth;
import com.spilab.monact.responses.UserResponse;
import com.spilab.monact.services.MQTTService;
import com.spilab.monact.utils.MqttClient;
import com.spilab.monact.utils.LocationUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class UserResource {

    private Context context;
    private MqttClient client;
    private UserResponse userResponse;
    private LatLng posicion;
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;

    public UserResource(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        client = new MqttClient();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Exception executeMethod(UserResponse response) throws MqttException, UnsupportedEncodingException {
        userResponse = response;

        switch (response.getMethod()) {
            case "getStatus":
                getStatus(response.getParams().getlatitude(), response.getParams().getlongitude(), response.getParams().getradius(), response.getParams().getminActivityTime(), response.getParams().getrange());
                break;
            default:
                client.publishMessage(MQTTService.getClient(), "Error: Not Found Method", 1, userResponse.getSender());
                return new Exception("Not found method.");
        }

        return null;
    }


    /**
     * Obtiene la posición actual y las horas de actividad desde 'x' minutos(range),
     * compara si se supera o no el minActivityTime y devuelve los datos usuarios
     * junto con los datos obtenidos.
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @param minActivityTime : actividad mínima deseada
     * @param range           : rango desde el que se quiere obtener los minutos de actividad.
     * @return User
     */
    public void getStatus(final Double latitude, final Double longitude, final Double radius, final Double minActivityTime, final Long range) /*throws MqttException, UnsupportedEncodingException*/ {

        final long startTime = System.currentTimeMillis();

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    posicion = new LatLng(location.getLatitude(), location.getLongitude());
                    //TODO: Process the information, etc.
                    if (LocationUtils.insideArea(latitude, longitude, radius, posicion)) {

                        //  Toast.makeText(context,"DENTRO DEL AREA",Toast.LENGTH_LONG).show();
                        /*Get activity since range*/
                        final SensorRepository repo = SensorRepository.getInstance(context);
                        Long activityTime = repo.activityTimeSince(range * 60);//hours to minute

                        /*Get User Information*/
                        User userReply = getUserInfo();

                        userReply.setLocation(posicion);

                        if (activityTime >= (minActivityTime * 60)) { //hours to minute
                            userReply.setActivityTime(activityTime);
                            userReply.setState(true);
                        } else {
                            userReply.setActivityTime(activityTime);
                            userReply.setState(false);
                        }

                        //Simulation for use case - Obtain health data from IoT Device
                        ArrayList<UserHealth> userHealths = new ArrayList<>();
                        userHealths.add(new UserHealth(108,0,156,3,1, 70, "06-03-2024 14:48:22"));
                        userHealths.add(new UserHealth(110,0,158,2,1,85,"07-03-2024 14:55:22"));
                        userHealths.add(new UserHealth(132,1,159,2,1,92,"08-03-2024 14:59:22"));

                        userReply.setHeathParams(userHealths);


                        long finish = System.currentTimeMillis() - startTime;

                        Log.i("MS COMPUTING: ", String.valueOf(finish));

                        /*To Json and repl*/
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

                        try {
                            client.publishMessage(MQTTService.getClient(), gson.toJson(userReply), 2, userResponse.getSender());
                            Log.i("Reply: ", gson.toJson(userReply));

                            Toast.makeText(context,"Request received by health staff", Toast.LENGTH_LONG).show();

                        } catch (MqttException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    Log.e("Location: ", " NULL");
            }
        });

    }

    public User getUserInfo() {
        User userReply = new User();
        userReply.setUuid(sharedPreferences.getString("uuid", "null"));
        userReply.setName(sharedPreferences.getString("nombre", "null"));
        userReply.setAge(Integer.parseInt(sharedPreferences.getString("edad", "null")));
        userReply.setGenre(sharedPreferences.getInt("genero", 99));
        userReply.setAddress(sharedPreferences.getString("direccion", "null"));
        userReply.setPostalAddress(sharedPreferences.getString("codigoPostal", "null"));
        userReply.setIdRequest(userResponse.getIdRequest());

        Log.i("telefono,", sharedPreferences.getString("telefono", "0"));
        if (!sharedPreferences.getString("telefono", "0").equals("Pulse para editar"))
            userReply.setPhone(Integer.valueOf(sharedPreferences.getString("telefono", "0")));

        return userReply;
    }


}
