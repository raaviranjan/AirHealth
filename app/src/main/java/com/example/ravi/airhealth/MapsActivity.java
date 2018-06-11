package com.example.ravi.airhealth;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MapsActivity extends Fragment implements OnMapReadyCallback{
    private static final int REQUEST_LOCATION = 1;
    private UiSettings uiSettings;
    LocationManager locationManager;
    LinearLayout spinner1layout,llTVLocMaps,llBLocMaps;
    TextView tvLocMaps;
    RelativeLayout spinner2layout;
    private GoogleMap mMap;
    Spinner spinnerState,spinnerCity,spinnerStation;
    Button bViewChart,bCurrentLocMap,bEnterLocMap;
    Fragment fragment = null;
    String currentLoc = "";
    Bundle bundle = new Bundle();
    LatLng latLng;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);

        SharedPreferences preferences = getActivity().getSharedPreferences("SHAR_PREF_NAME", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("maps", true);
        editor.putBoolean("register", false);
        editor.apply();

        bViewChart = view.findViewById(R.id.bViewChart);
        bCurrentLocMap = view.findViewById(R.id.bCurrentLocMap);
        bEnterLocMap = view.findViewById(R.id.bEnterLocMap);
        spinner1layout = view.findViewById(R.id.spinner1layout);
        spinner2layout = view.findViewById(R.id.spinner2layout);
        llTVLocMaps = view.findViewById(R.id.llTVLocMap);
        llBLocMaps = view.findViewById(R.id.llBLocMaps);
        tvLocMaps = view.findViewById(R.id.tvLocMaps);

        spinnerState = view.findViewById(R.id.spinnerState);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.spinnerState, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(adapter1);

        spinnerCity = view.findViewById(R.id.spinnerCity);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.spinnerCity, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter2);

        spinnerStation = view.findViewById(R.id.spinnerStation);
        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(getActivity(), R.array.spinnerStation, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStation.setAdapter(adapter3);

        bCurrentLocMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    buildAlertMsgNoGPS();
                } else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    getLocation();
                }
                mMap.clear();
                latLng = getLocationFromAddress(getActivity(),currentLoc);
                mMap.addMarker(new MarkerOptions().position(latLng).title("new Loc"));
                float zoomLevel = 14.0f;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
            }
        });

        bEnterLocMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentLoc = "";
                spinner1layout.setVisibility(View.VISIBLE);
                spinner2layout.setVisibility(View.VISIBLE);
                llBLocMaps.setVisibility(View.GONE);
                llTVLocMaps.setVisibility(View.GONE);
                bundle.putString("CurrentLoc",currentLoc);
            }
        });

        bViewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new Chart();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();

            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment)
                        getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        latLng = new LatLng(30.2175278,75.69844569999998);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Sliet"));
        float zoomLevel = 14.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }
    public void getLocation() {
        if(ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null){
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                currentLoc = getCompleteAddressString(lat,lng);
                tvLocMaps.setText(currentLoc);
                bundle.putString("CurrentLoc",currentLoc);
            } else
                Toast.makeText(getActivity(), "Unable to track your location", Toast.LENGTH_SHORT).show();
        }
    }

    public void buildAlertMsgNoGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Turn on GPS")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current location ", strReturnedAddress.toString());
            } else {
                Log.w("My Current location", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location", "Cannot get Address!");
        }
        return strAdd;
    }
    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}
