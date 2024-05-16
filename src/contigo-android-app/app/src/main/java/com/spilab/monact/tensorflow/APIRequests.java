package com.spilab.monact.tensorflow;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIRequests {



    public static void uploadCheckpoint(Context context, SharedPreferences sharedPreferences){


        ApiService apiService = ApiService.ApiClient.apiService;

        File checkpoint=new File(context.getFilesDir(), "checkpoint.ckpt");
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), checkpoint);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "checkpoint.ckpt", requestBody);


        String uuid=sharedPreferences.getString("uuid", "user123");
        RequestBody user = RequestBody.create(MediaType.parse("multipart/form-data"), uuid);
        Log.i("UUID", uuid);


        Call<ResponseBody> call = apiService.postCheckpoint(filePart,user);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.i("POST CHECKPOINT OK", response.toString());
                } else {

                    Log.e("POST CHECKPOINT FAILED", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("POST CHECKPOINT FAILURED", t.toString());
            }
        });
    }

}
