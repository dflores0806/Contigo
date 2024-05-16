package com.spilab.monact.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Modelo para la base de datos de la actividad del usuario.
 * POJO básico.
 */
@Entity
public class UserActivity {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private Long timestampStart;

    private Long timestampEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(Long timestampStart) {
        this.timestampStart = timestampStart;
    }

    public Long getTimestampEnd() {
        return timestampEnd;
    }

    public void setTimestampEnd(Long timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    @Override @Ignore
    public String toString(){
        String str = "UserActivity " + id +". Since: " + timestampStart + " To: ";
        if (timestampEnd != null){
            str += timestampEnd;
        }
        else{
            str += "NOW";
        }
        return str;
    }
}
