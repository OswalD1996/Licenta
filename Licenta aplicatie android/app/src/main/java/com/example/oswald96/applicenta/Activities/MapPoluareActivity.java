package com.example.oswald96.applicenta.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

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
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MapPoluareActivity extends AppCompatActivity implements OnMapReadyCallback{

    private  static final String TAG = "MapPoluareActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    String username;

    String urls[] = new String[] {/*"http://api.waqi.info/feed/romania/timis/timisoara/piata-unirii/?token=a7361ecabe730975750af2eccd0baf0c5317551d&fbclid=IwAR2gcUhss4VU-FH8mEcjK2Ixzf_eh4fBXvTQCWUl_8x4MWU_PSuKEtMLgjk",
            "http://api.waqi.info/feed/romania/timis/timisoara/str.-oituz/?token=a7361ecabe730975750af2eccd0baf0c5317551d&fbclid=IwAR2gcUhss4VU-FH8mEcjK2Ixzf_eh4fBXvTQCWUl_8x4MWU_PSuKEtMLgjk",
            "http://api.waqi.info/feed/romania/timisoara/moravita/str.-principala/?token=a7361ecabe730975750af2eccd0baf0c5317551d&fbclid=IwAR2gcUhss4VU-FH8mEcjK2Ixzf_eh4fBXvTQCWUl_8x4MWU_PSuKEtMLgjk",
            "http://api.waqi.info/feed/romania/timis/timisoara/str.-i-bulbuca/?token=a7361ecabe730975750af2eccd0baf0c5317551d&fbclid=IwAR2gcUhss4VU-FH8mEcjK2Ixzf_eh4fBXvTQCWUl_8x4MWU_PSuKEtMLgjk",
            "http://api.waqi.info/feed/hungary/pecs/szabadsag-ut/?token=a7361ecabe730975750af2eccd0baf0c5317551d&fbclid=IwAR2gcUhss4VU-FH8mEcjK2Ixzf_eh4fBXvTQCWUl_8x4MWU_PSuKEtMLgjk",
            "http://api.waqi.info/feed/romania/arad/zona-pasaj-micalaca/?token=a7361ecabe730975750af2eccd0baf0c5317551d&fbclid=IwAR2gcUhss4VU-FH8mEcjK2Ixzf_eh4fBXvTQCWUl_8x4MWU_PSuKEtMLgjk"*/
            };
    DataFromWAQI[] dateCurente= new DataFromWAQI[10000];
    MarkerOptions[] options = new MarkerOptions[10000];
    Spinner drp_listaoptiuni;
    List<String> listdrop = new ArrayList<String>();
    UserData dateUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_poluare);
        username = getIntent().getStringExtra("Username");
//get user specific data
        dateUser = UserData.getUserData(username);
//initializare dropdown
        drp_listaoptiuni = (Spinner) findViewById(R.id.dropdown1);
        listdrop.add("IndexAQI");
        listdrop.add("PM10");
        listdrop.add("O3");
        listdrop.add("SO2");
        listdrop.add("CO");
        listdrop.add("NO2");
        listdrop.add("PM25");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MapPoluareActivity.this, android.R.layout.simple_spinner_item, listdrop);
        drp_listaoptiuni.setAdapter(dataAdapter);

        try {
            dateCurente[9999] = new DataFromWAQI("http://exactonly.ro:13000/aqi_last_data", MapPoluareActivity.this) ;
            dateCurente[9999].getLatestDataFromMyAPI("http://exactonly.ro:13000/aqi_last_data");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0;i<urls.length; i++)
        {
            dateCurente[i] = new DataFromWAQI(urls[i], MapPoluareActivity.this);
            dateCurente[i].getDataFromOfficialAPI();
        }

        Button btnMap = (Button) findViewById(R.id.buttonMap);
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
                            mMap.setMinZoomPreference(5.0f);
                            mMap.setMaxZoomPreference(16.5f);
                            moveCamera(new LatLng(Float.parseFloat(dateUser.getDefaultLat()), Float.parseFloat(dateUser.getDefaultLong())), 13.0f);

                        }
                        else{
                            Log.d(TAG, "OnComplete: couldn't find location");
                            Toast.makeText(MapPoluareActivity.this, "unable to find location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation:" + e.getMessage() );
        }
    }
    private void moveCamera(LatLng latLng, float zoom) {
        mMap.clear();
        Log.d(TAG, "moveCamera: moving the camera to lat: " +latLng.latitude+"and longitude: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        //AddSeparateDataFromAPI(); adaug dupa ce refac metoda de get data de la API oficial
    }

    private void AddSeparateDataFromAPI()
    {
        mMap.clear();
        int counter=0;
        ArrayList<DataFromWAQI>dataFromMyAPI = dateCurente[9999].datefinale;

        for(DataFromWAQI elements:dataFromMyAPI)
        {
            dateCurente[urls.length+counter] = elements;
            counter++;
        }

        List<WeightedLatLng> wDat = new ArrayList<>();
        List<LatLng> list = new ArrayList<LatLng>();
        for(int i = 0; i<urls.length+counter;i++) {
            if (drp_listaoptiuni.getSelectedItem().toString() == "IndexAQI") {
                options[i] = new MarkerOptions()
                        .position(new LatLng(Double.valueOf(dateCurente[i].getLat()), Double.valueOf(dateCurente[i].getLng())))
                        .title(dateCurente[i].getNameLocation())
                        .snippet("Indicele AQI = "+ dateCurente[i].calculateaqi() +
                                ". Updatat acum: " + dateCurente[i].timePassed());
                mMap.addMarker(options[i]);
                wDat.add(new WeightedLatLng(new LatLng(Double.valueOf(dateCurente[i].getLat()), Double.valueOf(dateCurente[i].getLng())), dateCurente[i].calculateaqi()));
            }
            else {
                try {
                    Method getterMethod = dateCurente[i].getClass().getMethod("get" + drp_listaoptiuni.getSelectedItem().toString());
                    String pollutionValue = getterMethod.invoke(dateCurente[i]).toString();
                    if (Float.parseFloat(pollutionValue) == 0) {
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.common_google_signin_btn_icon_dark);
                        options[i] = new MarkerOptions()
                                .position(new LatLng(Double.valueOf(dateCurente[i].getLat()), Double.valueOf(dateCurente[i].getLng())))
                                .title(dateCurente[i].getNameLocation())
                                .snippet("Valoarea pentru " + drp_listaoptiuni.getSelectedItem().toString() + " este necunoscuta " +
                                        ". Updatat acum: " + dateCurente[i].timePassed());

                        mMap.addMarker(options[i]);
                        wDat.add(new WeightedLatLng(new LatLng(Double.valueOf(dateCurente[i].getLat()), Double.valueOf(dateCurente[i].getLng())), Double.parseDouble(pollutionValue)));
                    } else {
                        options[i] = new MarkerOptions()
                                .position(new LatLng(Double.valueOf(dateCurente[i].getLat()), Double.valueOf(dateCurente[i].getLng())))
                                .title(dateCurente[i].getNameLocation())
                                .snippet("Valoarea pentru " + drp_listaoptiuni.getSelectedItem().toString() +
                                        " = " + getterMethod.invoke(dateCurente[i]) + ". Updatat acum: " + dateCurente[i].timePassed());
                        mMap.addMarker(options[i]);
                        wDat.add(new WeightedLatLng(new LatLng(Double.valueOf(dateCurente[i].getLat()), Double.valueOf(dateCurente[i].getLng())), Double.parseDouble(pollutionValue)));
                    }
                } catch (Exception e) {
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

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(wDat)
                .gradient(gradient1)
                .build();
        mProvider.setRadius(100);
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }


    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) MapPoluareActivity.this);
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
    /*public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
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
    }*/
    public void onBackPressed() {
        Intent intent = new Intent(MapPoluareActivity.this, MainActivity.class);
        intent.putExtra("Username", username.toString());
        startActivity(intent);
        finish();
    }
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }
}
