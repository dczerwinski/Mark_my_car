package com.example.markmycar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_LOCATION = 1;
    Button button;
    Button googlemaps;
    Button found;
    LocationManager locationManager;
    String lattitude,longitude;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);


        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        button = (Button)findViewById(R.id.button_location);
        googlemaps = (Button)findViewById(R.id.button);
        found = (Button)findViewById(R.id.button2);



        if(sharedPreferences.getAll() != null){
            button.setVisibility(View.INVISIBLE);
            googlemaps.setVisibility(View.VISIBLE);
            found.setVisibility(View.VISIBLE);
        }
        else{
            button.setVisibility(View.VISIBLE);
            googlemaps.setVisibility(View.INVISIBLE);
            found.setVisibility(View.INVISIBLE);
        }

        button.setOnClickListener(this);

        googlemaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_lattitude,s_longitude;
                s_lattitude = sharedPreferences.getString("lattitude","");
                s_longitude= sharedPreferences.getString("longitude","");
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("lattitude",s_lattitude);
                intent.putExtra("longitude",s_longitude);
                MainActivity.this.startActivity(intent);
            }
        });

        found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().clear();
                Toast.makeText(MainActivity.this,"Yay!",Toast.LENGTH_SHORT).show();
                button.setVisibility(View.VISIBLE);
                googlemaps.setVisibility(View.INVISIBLE);
                found.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void saveLocation(String lattitude, String longitude){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lattitude",lattitude);
        editor.putString("longitude",longitude);
        editor.commit();
        Toast.makeText(this,"Your car location has been saved.",Toast.LENGTH_SHORT).show();
        button.setVisibility(View.INVISIBLE);
        googlemaps.setVisibility(View.VISIBLE);
        found.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                saveLocation(lattitude,longitude);

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
                saveLocation(lattitude,longitude);

            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);
            }else{
                Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}