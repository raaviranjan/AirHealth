package com.example.ravi.airhealth;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ravi on 06-Jun-18.
 */

public class Forget extends Fragment{
    Button bSave;
    Fragment fragment = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.forget,viewGroup,false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        SharedPreferences preferences = getActivity().getSharedPreferences("SHAR_PREF_NAME", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("forget", true);
        editor.apply();

        bSave = view.findViewById(R.id.bSaveForget);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new Login();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });
        return view;
    }
}
