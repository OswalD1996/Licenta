package com.example.oswald96.applicenta.Activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oswald96.applicenta.GetRealAdressWithLatLng;
import com.example.oswald96.applicenta.R;
import com.example.oswald96.applicenta.UserRelatedClasses.UserData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private  static final String TAG = "SettingsActivity";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = true;
    private Double latitude, longitude;
    private String currentAddress;
    String username;
    UserData dateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        username = getIntent().getStringExtra("Username");
        dateUser = UserData.getUserData(username);

        final EditText alarmtext = (EditText) findViewById(R.id.editTextUser4);
        Button btnSetData = (Button) findViewById(R.id.buttonSetData);
        final EditText locationtext=(EditText) findViewById(R.id.editTextUser5);

        btnSetData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                String alarmtxt, locationtxt;
                if(alarmtext.getText().toString().matches("")) {
                    alarmtxt = dateUser.getAlarmInterval().toString() ;
                }
                else{
                    Integer alarmint = Integer.parseInt(alarmtext.getText().toString());
                    alarmint = alarmint*60;
                    alarmtxt = alarmint.toString();
                }
                if(locationtext.getText().toString().matches(""))
                {
                    locationtxt = dateUser.getDefaultLat()+", "+dateUser.getDefaultLong();
                    dateUser.setUserData(alarmtxt, locationtxt);
                }
                else{

                    if(getLocationadress(locationtext.getText().toString()) != null )
                    {
                        locationtxt = getLocationadress(locationtext.getText().toString());
                        dateUser.setUserData(alarmtxt, locationtxt);
                    }

                }
            }
        });
        Button btnGetLocation = (Button) findViewById(R.id.buttonGetLocation);
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                    getDeviceLocation(locationtext);
                    //locationtext.setText(currentAddress);
                }
        });

    }

    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.putExtra("Username", username.toString());
        startActivity(intent);
        finish();
    }

    public String getLocationadress(String adress)
    {
        Geocoder coder = new Geocoder(SettingsActivity.this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(adress, 1);
            if (address == null) {
                Toast.makeText(this, "Address is incorrect", Toast.LENGTH_SHORT).show();
                return null;

            }
            Address location = address.get(0);
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            return lat + "," + lng;
        } catch (Exception e) {
            Toast.makeText(this, "Address is incorrect", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void getDeviceLocation(final EditText locationtext)
    {
        Log.d(TAG, "getDeviceLocation: getting the current location");

        try{
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            try {
                                Log.d(TAG, "OnComplete: found location");
                                Location currentLocation = (Location) task.getResult();
                                currentLocation.getLongitude();
                                currentAddress = GetRealAdressWithLatLng.getAddress(currentLocation.getLatitude(), currentLocation.getLongitude(), SettingsActivity.this);
                                Toast.makeText(SettingsActivity.this, "Address is " + currentAddress, Toast.LENGTH_SHORT).show();
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();
                                locationtext.setText(currentAddress);
                            }catch (Exception e){
                                Log.e(TAG, "getDeviceLocation:" + e.getMessage() );
                                Toast.makeText(SettingsActivity.this, "Location is not enabled", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Log.d(TAG, "OnComplete: couldn't find location");
                            Toast.makeText(SettingsActivity.this, "unable to find location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation:" + e.getMessage() );
        }
    }

}
