package com.example.ravi.airhealth;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    Boolean stationFound = false;
    Bundle bundle = new Bundle();
    LatLng latLng;
    String[] state = {"State","Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chandigarh","Chhattisgarh",
            "Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala",
            "Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","New Delhi","Odisha",
            "Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand","West Bengal"},
            city,station;
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
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,state);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(adapter1);

        spinnerCity = view.findViewById(R.id.spinnerCity);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getActivity(),R.array.spinnerCity, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter2);

        spinnerStation = view.findViewById(R.id.spinnerStation);
        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(getActivity(),R.array.spinnerStation, android.R.layout.simple_spinner_item);
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

            }
        });

        bEnterLocMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner1layout.setVisibility(View.VISIBLE);
                spinner2layout.setVisibility(View.VISIBLE);
                llBLocMaps.setVisibility(View.GONE);
                llTVLocMaps.setVisibility(View.GONE);
                enterLoc();
            }
        });

        bViewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stationFound)
                {
                    fragment = new Chart();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                } else {
                    Toast.makeText(getActivity(), "No Station Found", Toast.LENGTH_SHORT).show();
                }
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

        int height = 90;
        int width = 55;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.pin);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        // Add a marker in Sydney and move the camera
        latLng = new LatLng(30.2175278,75.69844569999998);

        // create marker
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .title("SLIET Longowal")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        // adding marker
        mMap.addMarker(marker);
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
                getStation(currentLoc);
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
    private void enterLoc() {
        // Tag used to cancel the request
        String cancel_req_tag = "incr_views";
        JsonArrayRequest request = new JsonArrayRequest("http://www.airhealth.info/api/v1/datas/?format=json",
                new Response.Listener<JSONArray>()  {
            @Override
            public void onResponse(final JSONArray response) {
            Log.d("res", "Register Response: " + response.toString());
            int i,j,l=1,jj;
            city = new String[response.length()+1];
            station = new String[response.length()+1];
            city[0]="City";
            station[0]="Station";
            for(i=1;i<=response.length();i++){
                city[i]="";
                station[i]="";
            }

            spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i==23)
                    {
                        cityJson(response,i);
                    }
                    else {
                        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getActivity(),R.array.spinnerCity, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCity.setAdapter(adapter2);
                        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(getActivity(),R.array.spinnerStation, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerStation.setAdapter(adapter3);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long lng) {
                    if(pos==1){
                        stationJson(response,pos);
                    }
                    if(pos == 2)
                        stationJson(response,pos);
                    if(pos == 3)
                        stationJson(response,pos);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            spinnerStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i!=0)
                    {
                        currentLoc = spinnerStation.getSelectedItem().toString() + ", " +
                                spinnerCity.getSelectedItem().toString() + ", " +
                                spinnerState.getSelectedItem().toString();

                        mMap.clear();
                        latLng = getLocationFromAddress(getActivity(),currentLoc);
                        mMap.addMarker(new MarkerOptions().position(latLng).title(currentLoc));
                        float zoomLevel = 14.0f;
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

                        String channel = getChannel(spinnerState.getSelectedItem().toString(),spinnerCity.getSelectedItem().toString(),
                                spinnerStation.getSelectedItem().toString(),response);


                            getNotification();

                        bundle.putString("CurrentLoc",currentLoc);
                        bundle.putString("ChannelID",channel);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // Adding request to request queue
        AppSingleton.getInstance(getActivity()).addToRequestQueue(request, cancel_req_tag);
    }
    public void stationJson(JSONArray response, int pos){
        try{
            int i,k=1;
            for(i = 0; i < response.length(); i++){
                JSONObject jresponse = response.getJSONObject(i);
                String cityName = jresponse.getString("value_9");
                String stationName = jresponse.getString("value_10");
                if(city[pos].equals(cityName))
                {
                    station[k]=stationName;
                    k++;
                }

            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, station);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStation.setAdapter(adapter2);
    }
    public void cityJson(JSONArray response,int pos){
        try{
            int i,k=1;
            for(i = 0; i < response.length(); i++){
                JSONObject jresponse = response.getJSONObject(i);
                String stateName = jresponse.getString("value_8");
                String cityName = jresponse.getString("value_9");
                if(state[pos].equals(stateName))
                {
                    city[k]=cityName;
                    k++;
                }

            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, city);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter1);
    }
    public String getChannel(String stateNM, String cityNM, String stationNM, JSONArray response){
        try{
            int i;
            for(i = 0; i < response.length(); i++){
                JSONObject jresponse = response.getJSONObject(i);
                String stateName = jresponse.getString("value_8");
                String cityName = jresponse.getString("value_9");
                String stationName = jresponse.getString("value_10");
                int channel = jresponse.getInt("channel");
                if(stateName.equals(stateNM) && cityName.equals(cityNM) && stationName.equals(stationNM)){
                    stationFound = true;
                    return String.valueOf(channel);
                } else {
                    stationFound = false;
                    //Toast.makeText(getActivity(), "No Station Found2", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return "no-channel";
    }
    private void getStation(final String cLoc) {
        // Tag used to cancel the request
        String cancel_req_tag = "get_station";
        JsonArrayRequest request1 = new JsonArrayRequest("http://www.airhealth.info/api/v1/datas/?format=json",
                new Response.Listener<JSONArray>()  {
                    @Override
                    public void onResponse(final JSONArray response) {
                        Log.d("res", "Register Response: " + response.toString());
                        try{
                            int flag=0;
                            for(int i = 0; i < response.length(); i++){
                                JSONObject jresponse = response.getJSONObject(i);
                                String stateName = jresponse.getString("value_8");
                                String cityName = jresponse.getString("value_9");
                                String stationName = jresponse.getString("value_10");
                                int channel = jresponse.getInt("channel");
                                if(cLoc.contains(stationName) && cLoc.contains(cityName) && cLoc.contains(stateName)){
                                    mMap.clear();
                                    latLng = getLocationFromAddress(getActivity(),cLoc);
                                    mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location"));
                                    float zoomLevel = 14.0f;
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                                    bundle.putString("ChannelID",String.valueOf(channel));
                                    stationFound = true;
                                    flag = 1;
                                    break;
                                }
                            }
                            if(flag == 0)
                            {
                                stationFound = false;
                                Toast.makeText(getActivity(), "No Station Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // Adding request to request queue
        AppSingleton.getInstance(getActivity()).addToRequestQueue(request1, cancel_req_tag);
    }
    public void getNotification(){
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), "default")
                .setSmallIcon(R.drawable.lgo)
                .setContentTitle("Airhealth")
                .setContentText("This is  first notification")
                .setAutoCancel(true); // clear notification after click

        PendingIntent pi = PendingIntent.getActivity(getActivity(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
