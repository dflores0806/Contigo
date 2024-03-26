package com.spilab.monact.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.spilab.monact.data.room.dao.UserActivityDAO;
import com.spilab.monact.models.UserActivity;


/**
 * Clase para gestionar la base de datos de la aplicación.
 */
@Database(entities = {UserActivity.class}, version = 1, exportSchema = false)
public abstract class SensorDatabase extends RoomDatabase {

    /**
     * Nombre de la base de datos.
     */
    private static final String DATABASE_NAME = "Sensorization";

    /**
     * Instancia para singleton.
     */
    private static SensorDatabase instance = null;

    /**
     * Método singleton getInstance. Thread-safe.
     * @param context Contexto Android
     * @return Instancia de SensorDatabase
     */
    public static SensorDatabase getInstance(Context context){
        synchronized (DATABASE_NAME){ // Garantiza que el método es thread-safe.
            if (instance == null){
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        SensorDatabase.class, DATABASE_NAME).build();
            }
        }
        return instance;
    }

    public abstract UserActivityDAO userActivityDAO();
}
