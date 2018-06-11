package com.example.ravi.airhealth;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ravi on 03-Jun-18.
 */

public class Login extends Fragment {
    Button bReg,bLogin,bForgot,bSkip;
    Fragment fragment = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.login,viewGroup,false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        SharedPreferences preferences = getActivity().getSharedPreferences("SHAR_PREF_NAME", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("login", true);
        editor.apply();

        bReg = view.findViewById(R.id.bReg);
        bLogin = view.findViewById(R.id.bLogin);
        bForgot = view.findViewById(R.id.bForgot);
        bSkip = view.findViewById(R.id.bSkipLogin);

        bReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new Register();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });
        bForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new Forget();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });
        bSkip.setOnClickListener(new View.OnClickListener() {
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
