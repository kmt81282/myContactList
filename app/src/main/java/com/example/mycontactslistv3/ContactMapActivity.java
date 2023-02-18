package com.example.mycontactslistv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class ContactMapActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener gpsListener;
    final int PERMISSION_REQUEST_LOCATION = 101;
    Location currentBestLocation;
    LocationListener networkListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map);
        initGetLocationButton();
    }

    private void initGetLocationButton() {
        Button locationButton = (Button) findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(ContactMapActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale
                                    (ContactMapActivity.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                                Snackbar.make(findViewById(R.id.activity_contact_map),
                                                "MyContactList requires this permission to locate " +
                                                        "your contacts", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                ActivityCompat.requestPermissions(
                                                        ContactMapActivity.this,
                                                        new String[]{
                                                                Manifest.permission.ACCESS_FINE_LOCATION},
                                                        PERMISSION_REQUEST_LOCATION);
                                            }
                                        })
                                        .show();
                            } else {
                                ActivityCompat.requestPermissions(ContactMapActivity.this, new
                                                String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_LOCATION);
                            }
                        } else {
                            startLocationUpdates();
                        }
                    } else {
                        startLocationUpdates();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Error requesting permission",
                            Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    public void onPause() {
        super.onPause();
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            locationManager.removeUpdates(gpsListener);
            locationManager.removeUpdates(networkListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) return;

        try {
            locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCALE_SERVICE);
            gpsListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    TextView txtLatitude = (TextView) findViewById(R.id.textLatitude);
                    TextView txtLongitude = (TextView) findViewById(R.id.textLongitude);
                    TextView txtAccuracy = (TextView) findViewById(R.id.textAccuracy);
                    txtLatitude.setText(String.valueOf(location.getLatitude()));
                    txtLongitude.setText(String.valueOf(location.getLongitude()));
                    txtAccuracy.setText(String.valueOf(location.getAccuracy()));
                    if (isBetterLocation(location)) {
                        currentBestLocation = location;
                        //Displays in location in TextViews
                    }
                    //no else block... if not better, just ignore
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            networkListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    TextView txtLatitude = (TextView) findViewById(R.id.textLatitude);
                    TextView txtLongitude = (TextView) findViewById(R.id.textLongitude);
                    TextView txtAccuracy = (TextView) findViewById(R.id.textAccuracy);
                    txtLatitude.setText(String.valueOf(location.getLatitude()));
                    txtLongitude.setText(String.valueOf(location.getLongitude()));
                    txtAccuracy.setText(String.valueOf(location.getAccuracy()));
                    if (isBetterLocation(location)) {
                        currentBestLocation = location;
                        //display in location in TextViews
                    }
                    //no else block... if not better, just ignore
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            //The parameters in this message tell the LocationManager to listen to the GPS sensor with no minimum time between updates and no
            // minimum distance between locations and report those changes to the LocationListener assigned to the gpsListener variable.
            // The minimum time and distance parameters are set to zero for demonstration purposes. These values should be set based on how much
            // you expect the device to move during the app’s use. Setting values higher than zero can help conserve the battery. This is especially
            // true of the time value. The minimum time is set in milliseconds (2*60*1,000 = 120,000, or 2 minutes). Minimum distance is set in meters.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, networkListener);
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error, Location not available", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(ContactMapActivity.this,
                            "MyContactList will not locate you contacts",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean isBetterLocation (Location location) {
        boolean isBetter = false;

        // New location better if no existing location
        if (currentBestLocation == null) {
            isBetter = true;
        }
        // Checks if new location has better accuracy
        else if (location.getAccuracy() <= currentBestLocation.getAccuracy()) {
            isBetter = true;
        }
        // Checks if new location is newer by 5 min
        else if (location.getTime() - currentBestLocation.getTime() > 5*60*1000) {
            isBetter = true;
        }
        return isBetter;
    }


}