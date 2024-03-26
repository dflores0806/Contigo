package com.spilab.monact.fragments;

import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spilab.contigo.databinding.FragmentInicioBinding;
import com.spilab.monact.data.repository.SensorRepository;



/**
 * A simple {@link Fragment} subclass.
 */
public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;
    private SharedPreferences sharedPreferences;
    private SensorRepository repo;

    public InicioFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);

        repo = SensorRepository.getInstance(getContext());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        /*Elementos de la vista*/
        binding.textViewNombre.setText(sharedPreferences.getString("nombre", "null"));
        binding.textViewActividad.setText(castToHoursAndMinutes(repo.activityTimeSince((long) 1440)));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        binding.textViewActividad.setText(castToHoursAndMinutes(repo.activityTimeSince((long) 1440)));
        super.onResume();
    }


    String castToHoursAndMinutes(Long activityTime) {
        int hours = 0;
        int minutes = 0;

        if (activityTime >= 60) {
            hours = (int) (activityTime / 60);
            if (activityTime % 60 != 0) {
                minutes = (int) ((activityTime % 60));
                if (hours > 1)
                    return hours + " hours and " + minutes + " minutes";
                else
                    return hours + " hour and " + minutes + " minutes";
            } else {
                if (hours > 1)
                    return hours + " hours";
                else
                    return hours + " hour";
            }

        } else
            return activityTime + " minutes";


    }
}
