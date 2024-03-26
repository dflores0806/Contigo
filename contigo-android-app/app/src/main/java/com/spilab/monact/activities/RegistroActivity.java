package com.spilab.monact.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Toast;

import com.spilab.contigo.databinding.ActivityRegistroBinding;


public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // binding = ActivityAjustesBinding.inflate(inflater,container, false);
        binding.textTerminos.setMovementMethod(LinkMovementMethod.getInstance());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);



        binding.buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(binding.editNombre.getText()) || TextUtils.isEmpty(binding.editDireccion.getText()) || TextUtils.isEmpty(binding.editCodigoPostal.getText())) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Name, address and postcode are mandatory", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nombre", String.valueOf(binding.editNombre.getText())).apply();

                    editor.putString("edad", String.valueOf(binding.editTextEdad.getText())).apply();


                    if(binding.radioButtonMale.isChecked())
                        //Male
                        editor.putInt("genero", 0).apply();
                    else{
                        //Female
                        editor.putInt("genero", 1).apply();
                    }

                    editor.putString("direccion", String.valueOf(binding.editDireccion.getText())).apply();
                    editor.putString("codigoPostal", String.valueOf(binding.editCodigoPostal.getText())).apply();

                    if (!TextUtils.isEmpty(binding.editTelefono.getText()))
                        editor.putString("telefono", String.valueOf(binding.editTelefono.getText())).apply();

                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new InicioFragment()).addToBackStack(null).commit();
                }
            }
        });
    }
}
