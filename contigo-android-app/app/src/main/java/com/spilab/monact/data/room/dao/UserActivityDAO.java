package com.spilab.monact.data.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.spilab.monact.models.UserActivity;

import java.util.List;


/**
 * DAO de la base de datos. No se recomienda su uso de manera directa, salvo para tareas concretas.
 */
@Dao
public interface UserActivityDAO {

    /**
     * Obtener la actividad del usuario que aún no ha terminado.
     * @return Actividad actual, si aún está activo.
     */
    @Query("SELECT * FROM UserActivity WHERE timestampEnd IS NULL")
    UserActivity getActive();

    /**
     * Obtiene la actividad del usuario desde un punto del tiempo concreto.
     * @param timestampSince Punto del tiempo a partir del que queremos obtener la actividad.
     * @return Actividades del usuario desde el punto indicado.
     */
    @Query("SELECT * FROM UserActivity WHERE timestampStart >= :timestampSince")
    List<UserActivity> getSince(Long timestampSince);

    /**
     * Obtiene una actividad de la base de datos en base a su ID.
     * @param id ID de la actividad a obtener.
     * @return Actividad con el ID indicado.
     */
    @Query("SELECT * FROM UserActivity WHERE id = :id")
    UserActivity getByID(Long id);

    /**
     * Añade una actividad a la base de datos.
     * @param ua Actividad a añadir.
     * @return ID de la actividad.
     */
    @Insert
    Long add(UserActivity ua);

    /**
     * Actualiza una actividad en la base de datos.
     * @param ua Actividad a actualizar.
     * @return Filas afectadas.
     */
    @Update
    int update(UserActivity ua);

    /**
     * Elimina toda la actividad a partir de un punto en el tiempo.
     * @param lastTimestamp Punto a partir del que eliminar la actividad.
     */
    @Query("DELETE FROM UserActivity WHERE timestampEnd <= :lastTimestamp")
    void wipe(Long lastTimestamp);

    /**
     * Elimina todas las tuplas abiertas que no deberían existir (por ejemplo, por una excepción)
     */
    @Query("DELETE FROM UserActivity WHERE timestampEnd IS NULL")
    void wipeBroken();
}
