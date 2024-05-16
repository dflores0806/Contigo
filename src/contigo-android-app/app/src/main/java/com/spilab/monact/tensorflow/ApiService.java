package com.spilab.monact.tensorflow;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

public interface ApiService {

    @GET("/contigo-api/model")
    Call<ResponseBody> getGlobalModel();

    @Multipart
    @POST("/contigo-api/data")
    Call<ResponseBody> postCheckpoint( @Part MultipartBody.Part file,  // Archivo a subir
                                       @Part("user") RequestBody user );

    @GET("/contigo-api//model-filename")
    Call<ResponseBody> obtenerVersionModelo();



    class ApiClient {
        private static final String BASE_URL = "http://34.250.21.131";

        public static final ApiService apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }
}

