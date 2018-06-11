package com.example.ravi.airhealth;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ravi on 03-Jun-18.
 */

public class Register extends Fragment {
    Spinner spinnerOwner;
    Button bReg;
    Fragment fragment = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.register,viewGroup,false);

        SharedPreferences preferences = getActivity().getSharedPreferences("SHAR_PREF_NAME", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("register", true);
        editor.apply();

        bReg = view.findViewById(R.id.bRegisterReg);

        spinnerOwner = view.findViewById(R.id.spinnerTypeReg);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.spinner, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOwner.setAdapter(adapter1);

        bReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new MapsActivity();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });

        return view;
    }

}
