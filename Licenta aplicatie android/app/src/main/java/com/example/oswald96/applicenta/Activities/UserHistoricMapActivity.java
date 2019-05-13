package com.example.oswald96.applicenta.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.oswald96.applicenta.StructureClasses.DataFromWAQI;
import com.example.oswald96.applicenta.R;
import com.example.oswald96.applicenta.UserRelatedClasses.UserData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class UserHistoricMapActivity extends AppCompatActivity implements OnMapReadyCallback{
    String username;
    private  static final String TAG = "UserHistoricMapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap2;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    int counter = 0;

    DataFromWAQI[] dateCurente= new DataFromWAQI[10000];
    MarkerOptions[] options = new MarkerOptions[10000];

    Spinner drp_listaoptiuni;
    List<String> listdrop = new ArrayList<String>();
    UserData dateUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_historic_map);
        username = getIntent().getStringExtra("Username");
        //get user specific data
        dateUser = UserData.getUserData(username);
        //initializare dropdown
        drp_listaoptiuni = (Spinner) findViewById(R.id.dropdown);
        listdrop.add("IndexAQI");
        listdrop.add("PM10");
        listdrop.add("O3");
        listdrop.add("SO2");
        listdrop.add("CO");
        listdrop.add("NO2");
        listdrop.add("PM25");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(UserHistoricMapActivity.this, android.R.layout.simple_spinner_item, listdrop);
        drp_listaoptiuni.setAdapter(dataAdapter);

        try {
            dateCurente[9999] = new DataFromWAQI("http://exactonly.ro:13000/quality_indexes/"+username, this);
            dateCurente[9999].getDataPerUserFromMyAPI("http://exactonly.ro:13000/quality_indexes/"+username);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Button btnMap = (Button) findViewById(R.id.buttonMap2);
        btnMap.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View view){
                                          AddSeparateDataFromAPI();
                                      }
                                  }
        );
        getLocationPermission();

    }
    private void getDeviceLocation()
    {
        Log.d(TAG, "getDeviceLocation: getting the current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "OnComplete: found location");
                            Location currentLocation= (Location)task.getResult();
                            mMap2.setMinZoomPreference(10.0f);
                            mMap2.setMaxZoomPreference(16.5f);
                            moveCamera(new LatLng(Float.parseFloat(dateUser.getDefaultLat()), Float.parseFloat(dateUser.getDefaultLong())), 13.0f);
                        }
                        else{
                            Log.d(TAG, "OnComplete: couldn't find location");
                            Toast.makeText(UserHistoricMapActivity.this, "unable to find location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation:" + e.getMessage() );
        }
    }
    private void moveCamera(LatLng latLng, float zoom) {
        mMap2.clear();
        Log.d(TAG, "moveCamera: moving the camera to lat: " +latLng.latitude+"and longitude: "+latLng.longitude);
        mMap2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        AddSeparateDataFromAPI();
    }
    private void AddSeparateDataFromAPI() {
        counter = 0;
        mMap2.clear();
        List<WeightedLatLng> wDat = new ArrayList<>();
        List<LatLng> list = new ArrayList<LatLng>();
        ArrayList<DataFromWAQI> dataFromMyAPI = dateCurente[9999].datefinaleperUser;
        for(DataFromWAQI elements:dataFromMyAPI)
        {
            dateCurente[counter] = elements;
            counter++;
        }
        if(counter == 0)
        {
            Toast.makeText(UserHistoricMapActivity.this, username+" has historical no data", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int j = 0; j < counter; j++) {
                if (drp_listaoptiuni.getSelectedItem().toString() == "IndexAQI") {
                    options[j] = new MarkerOptions()
                            .position(new LatLng(Double.valueOf(dateCurente[j].getLat()), Double.valueOf(dateCurente[j].getLng())))
                            .title(dateCurente[j].getNameLocation())
                            .snippet("Indicele AQI = "+ dateCurente[j].calculateaqi() +
                                    ". Updatat acum: " + dateCurente[j].timePassed());
                    mMap2.addMarker(options[j]);
                    wDat.add(new WeightedLatLng(new LatLng(Double.valueOf(dateCurente[j].getLat()), Double.valueOf(dateCurente[j].getLng())), dateCurente[j].calculateaqi()));
                } else {
                    Method getterMethod = null;
                    try {
                        getterMethod = dateCurente[j].getClass().getMethod("get" + drp_listaoptiuni.getSelectedItem().toString());
                        String pollutionValue = getterMethod.invoke(dateCurente[j]).toString();

                        if (Float.parseFloat(pollutionValue) == 0) {
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.common_google_signin_btn_icon_dark);
                            options[j] = new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(dateCurente[j].getLat()), Double.valueOf(dateCurente[j].getLng())))
                                    .title(dateCurente[j].getNameLocation())
                                    .snippet("Valoarea pentru " + drp_listaoptiuni.getSelectedItem().toString() + " este necunoscuta " +
                                            ". Updatat acum: " + dateCurente[j].timePassed());

                            mMap2.addMarker(options[j]);
                            wDat.add(new WeightedLatLng(new LatLng(Double.valueOf(dateCurente[j].getLat()), Double.valueOf(dateCurente[j].getLng())), Double.parseDouble(pollutionValue)));
                        } else {
                            options[j] = new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(dateCurente[j].getLat()), Double.valueOf(dateCurente[j].getLng())))
                                    .title(dateCurente[j].getNameLocation())
                                    .snippet("Valoarea pentru " + drp_listaoptiuni.getSelectedItem().toString() +
                                            " = " + getterMethod.invoke(dateCurente[j]) + ". Updatat acum: " + dateCurente[j].timePassed());
                            mMap2.addMarker(options[j]);
                            wDat.add(new WeightedLatLng(new LatLng(Double.valueOf(dateCurente[j].getLat()), Double.valueOf(dateCurente[j].getLng())), Double.parseDouble(pollutionValue)));
                        }

                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            list.add(new LatLng(45.733516, 21.241824));
            list.add(new LatLng(45.733516, 21.243090));

            int[] colors = {
                    Color.GREEN,    // green(0-50)
                    Color.YELLOW,    // yellow(51-100)
                    Color.rgb(244, 131, 66), //Orange(101-150)
                    Color.RED,              //red(151-200)
            };

            float[] startPoints = {
                    0.1F, 0.5F, 0.8F, 1.0F
            };

            Gradient gradient1 = new Gradient(colors, startPoints);

            if(wDat.isEmpty() == false) {
                HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                        .weightedData(wDat)
                        .gradient(gradient1)
                        .build();
                mProvider.setRadius(100);
                TileOverlay mOverlay = mMap2.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }

        }







    }
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment2 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment2.getMapAsync((OnMapReadyCallback) UserHistoricMapActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0 ){
                    for(int i = 0; i<grantResults.length;i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission accepted");
                    mLocationPermissionsGranted = true;
                    // init map
                    initMap();
                }
            }
        }
    }
    public void onBackPressed() {

        Intent intent = new Intent(UserHistoricMapActivity.this, MainActivity.class);
        intent.putExtra("Username", username.toString());
        startActivity(intent);
        finish();
    }
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap2 = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap2.setMyLocationEnabled(true);
        }
    }

}
