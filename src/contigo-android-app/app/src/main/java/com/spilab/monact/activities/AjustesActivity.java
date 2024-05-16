package com.spilab.monact.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;


import com.spilab.contigo.R;
import com.spilab.monact.fragments.AjustesFragment;

import java.util.Map;


public class AjustesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        Toolbar toolbar= findViewById(R.id.toolbar_ajustes);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor_ajustes, new AjustesFragment())
                .commit();
    }


}
