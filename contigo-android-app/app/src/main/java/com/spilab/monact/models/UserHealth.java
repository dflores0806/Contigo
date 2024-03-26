package com.spilab.monact.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;

/**
 * Modelo para la base de datos de la actividad del usuario.
 * POJO básico.
 */
@ApiModel(description = "")
public class UserHealth {

/*    1) age - Edad
2) sex - Sexo
3) blood pressure - Presión arterial
4) fasting blood sugar > 120 mg/dl - Azúcar en sange
5) maximum heart rate achieved - Máximo ritmo cardiaco
6) thal: 0 = normal; 1 = fixed defect; 2 = reversable defect - Riesgo de padecer la patología
7) target:*/

//    57,1,150,1,173,3,1
//            39,0,138,0,152,2,1
//            54,0,110,0,158,2,1
//            65,1,138,1,174,2,0
//            39,0,94,0,179,2,1
//            65,0,155,0,148,2,1


    // @PrimaryKey(autoGenerate = true)


    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("trestbps")
    private int bloodPressure;
    @SerializedName("fbs")
    private int bloodSugar;
    @SerializedName("thalach")
    private int maximumHeart;
    @SerializedName("thal")
    private int thal;
    @SerializedName("target")
    private int target;

    @SerializedName("precision")
    private int precision;

    public UserHealth( int bloodPressure, int bloodSugar, int maximumHeart, int thal, int target, int precision, String timestamp) {
        this.bloodPressure = bloodPressure;
        this.bloodSugar = bloodSugar;
        this.maximumHeart = maximumHeart;
        this.thal = thal;
        this.target = target;
        this.precision=precision;
        this.timestamp=timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getPrecision() {
        return precision;
    }


    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(int bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public int getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(int bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public int getMaximumHeart() {
        return maximumHeart;
    }

    public void setMaximumHeart(int maximumHeart) {
        this.maximumHeart = maximumHeart;
    }

    public int getThal() {
        return thal;
    }

    public void setThal(int thal) {
        this.thal = thal;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}



