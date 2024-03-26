package com.spilab.monact.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


import com.spilab.contigo.R;

import java.util.Map;


public class AjustesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    private EditTextPreference editTextPrefNombre;

    private EditTextPreference editTextPrefEdad;

    private EditTextPreference editTextPrefDireccion;
    private EditTextPreference editTextPrefTelefono;
    private EditTextPreference editTextPrefCP;

    public AjustesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        editTextPrefNombre = findPreference("nombre");
        editTextPrefNombre.setTitle(sharedPreferences.getString("nombre", "null"));

        editTextPrefNombre.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().trim().equals("")) {

                    Toast.makeText(getActivity(), "You must put a name",
                            Toast.LENGTH_LONG).show();

                    return false;
                }
                return true;
            }
        });

        editTextPrefEdad = findPreference("edad");
        editTextPrefEdad.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.selectAll();
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            }
        });
        editTextPrefEdad.setTitle(sharedPreferences.getString("edad", "null"));
        


        editTextPrefDireccion = findPreference("direccion");
        editTextPrefDireccion.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().trim().equals("")) {

                    Toast.makeText(getActivity(), "You must enter an address",
                            Toast.LENGTH_LONG).show();

                    return false;
                }
                return true;
            }
        });
        editTextPrefDireccion.setTitle(sharedPreferences.getString("direccion", "null"));


        editTextPrefTelefono = findPreference("telefono");
        editTextPrefTelefono.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.selectAll();
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
            }
        });

        editTextPrefTelefono.setTitle(sharedPreferences.getString("telefono", "null"));



        editTextPrefCP = findPreference("codigoPostal");
        editTextPrefCP.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.selectAll();
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            }
        });
        editTextPrefCP.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().trim().equals("")) {

                    Toast.makeText(getActivity(), "You must enter a postcode",
                            Toast.LENGTH_LONG).show();

                    return false;
                }
                return true;
            }
        });

        editTextPrefCP.setTitle(sharedPreferences.getString("codigoPostal", "null"));





    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            if(etp.getText().isEmpty()) {
                etp.setText("Pulse para editar");
            }
            pref.setTitle(etp.getText());
        }
    }

}
