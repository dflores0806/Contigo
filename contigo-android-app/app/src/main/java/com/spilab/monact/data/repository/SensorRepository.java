package com.spilab.monact.data.repository;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.spilab.monact.data.room.SensorDatabase;
import com.spilab.monact.data.room.dao.UserActivityDAO;
import com.spilab.monact.models.UserActivity;
import com.spilab.monact.threading.ThreadChooser;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;


/**
 * Clase repositorio de la actividad sensorizada. Clase de datos de uso recomendado.
 */
public class SensorRepository {
    /**
     * Objeto DAO de acceso a datos.
     */
    private UserActivityDAO dao;

    /**
     * Objeto para manejar el threading.
     */
    private ThreadChooser threads;

    /**
     * Instancia para singleton.
     */
    private static SensorRepository instance = null;

    /**
     * Etiqueta para logs de la clase.
     */
    private static final String TAG = "SensorRepository";

    /**
     * Constructor, privado por defecto
     * @param context Contexto Android
     */
    private SensorRepository(Context context){
        this.dao = SensorDatabase.getInstance(context).userActivityDAO();
        this.threads = ThreadChooser.getInstance();
        FutureTask<Boolean> blocking = new FutureTask<>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                dao.wipeBroken();
                Log.i(TAG, "Wiped broken tuples");
                return true;
            }
        });
        this.threads.getDiskThread().execute(blocking);
        try{
            blocking.get();
        }
        catch (Exception ex){
            Log.e(TAG, "Exception while wiping broken tuples" + ex.getMessage());
        }
        Log.d(TAG, "Created repository");
    }

    /**
     * Método singleton getInstance. Thread-safe.
     * @param context Contexto Android
     * @return Instancia de SensorRepository
     */
    public static SensorRepository getInstance(Context context){
        synchronized (TAG){ // Garantía thread-safe
            if (instance == null){
                instance = new SensorRepository(context);
            }
        }
        return instance;
    }

    /**
     * Devuelve el timestamp UNIX actual.
     * @return Timestamp UNIX
     */
    private Long getTimestamp(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Instant.now().getEpochSecond();
        }
        else{
            return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        }
    }

    /**
     * Obtener la actividad del usuario que aún no ha terminado.
     * @return Actividad del usuario, o null si está inactivo.
     */
    public UserActivity getActive(){
        UserActivity ua = null;
        try{
            FutureTask<UserActivity> futureTask = new FutureTask<>(new Callable<UserActivity>() {
                @Override
                public UserActivity call(){
                    return dao.getActive();
                }
            });
            threads.getDiskThread().execute(futureTask);
            ua = futureTask.get();
        }
        catch (Exception ex){
            Log.e(TAG, "Exception got while getting active activity! " + ex.getMessage());
        }
        return ua;
    }

    /**
     * Obtener las actividades en los últimos minutos.
     * @param minutesSince Minutos desde los que obtener actividades.
     * @return Actividades desde el momento indicado.
     */
    public List<UserActivity> getSince(Long minutesSince){
        List<UserActivity> uas = new ArrayList<>();
        Long currTimestamp = getTimestamp();
        final Long timestampSince = currTimestamp - minutesSince * 60; // Los timestamps van en segundos
        try{
            FutureTask<List<UserActivity>> futureTask = new FutureTask<>(new Callable<List<UserActivity>>() {
                @Override
                public List<UserActivity> call(){
                    return dao.getSince(timestampSince);
                }
            });
            threads.getDiskThread().execute(futureTask);
            uas = futureTask.get();
        }
        catch (Exception ex){
            Log.e(TAG, "Exception got while getting activities since " + timestampSince +
                    "! " + ex.getMessage());
        }
        return uas;
    }

    /**
     * Obtener los minutos de actividad en los últimos minutos.
     * @param minutesSince Minutos desde los que obtener actividades.
     * @return Tiempo de actividad desde el momento indicado en minutos.
     */
    public Long activityTimeSince(Long minutesSince){
        List<UserActivity> activities = getSince(minutesSince);
        long actSeconds = (long) 0;
        for (UserActivity activity : activities){
            Long tsStart = activity.getTimestampStart();
            Long tsEnd = activity.getTimestampEnd() == null ?
                    getTimestamp() : activity.getTimestampEnd();
            actSeconds += tsEnd - tsStart;
        }

        return TimeUnit.SECONDS.toMinutes(actSeconds);
    }

    /**
     * Obtiene una actividad en base a su ID.
     * @param id ID de la actividad a obtener.
     * @return Actividad con el ID indicado.
     */
    public UserActivity getActivity(final Long id){
        UserActivity ua = null;
        try{
            FutureTask<UserActivity> futureTask = new FutureTask<>(new Callable<UserActivity>() {
                @Override
                public UserActivity call(){
                    return dao.getByID(id);
                }
            });
            threads.getDiskThread().execute(futureTask);
            ua = futureTask.get();
        }
        catch(Exception ex){
            Log.e(TAG, "Exception got while getting activity " + id +
                    "! " + ex.getMessage());
        }
        return ua;
    }

    /**
     * Crea ("abre") una nueva tupla de actividad.
     * @return ID de la actividad.
     */
    public Long open(){
        Long result = (long) -1;
        try {
            FutureTask<Long> futureTask = new FutureTask<>(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    UserActivity ua = new UserActivity();
                    ua.setTimestampStart(getTimestamp());
                    ua.setTimestampEnd(null);
                    return dao.add(ua);
                }
            });
            threads.getDiskThread().execute(futureTask);
            result = futureTask.get();
        }
        catch (Exception ex){
            Log.e(TAG, "Error while adding activity!" + ex.getMessage());
        }
        return result;
    }

    /**
     * Pone el tiempo de fin ("cierra") la tupla de actividad actualmente activa.
     * @return True si se ha podido cerrar, false en caso contrario.
     */
    public boolean close(){
        boolean result = false;
        try {
            FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
                @Override
                public Integer call(){
                    UserActivity ua = getActive();
                    ua.setTimestampEnd(getTimestamp());
                    return dao.update(ua);
                }
            });
            threads.getDiskThread().execute(futureTask);
            Integer update = futureTask.get();
            result = update != -1;
        }
        catch (Exception ex){
            Log.e(TAG, "Error while closing activity!" + ex.getMessage());
        }
        return result;
    }

    /**
     * Elimina las actividades más antiguas de lo indicado.
     * @param minutesOlder Minutos a partir de los que eliminar la actividad.
     */
    public void deleteOlder(final Long minutesOlder){
        try {
            threads.getDiskThread().execute(new Runnable() {
                @Override
                public void run() {
                    Long currTimestamp = getTimestamp();
                    final Long timestampTo = currTimestamp - minutesOlder * 60; // Los timestamps van en segundos
                    dao.wipe(timestampTo);
                }
            });
        }
        catch (Exception ex){
            Log.e(TAG, "Exception while wiping!"+ex.getMessage());
        }
    }
}
