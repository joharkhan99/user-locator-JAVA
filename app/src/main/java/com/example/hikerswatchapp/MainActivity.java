package com.example.hikerswatchapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

  LocationManager locationManager;
  LocationListener locationListener;

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      startListening();
    }

  }

  public void startListening() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
  }

  public void updateLocationInfo(Location location) {
    System.out.println(location.toString());
    TextView latTextView = (TextView) findViewById(R.id.latTextView);
    TextView lngTextView = (TextView) findViewById(R.id.lngTextView);
    TextView altTextView = (TextView) findViewById(R.id.altitudeTextView);
    TextView accTextView = (TextView) findViewById(R.id.accuracyTextView);

    latTextView.setText("Latitude: " + location.getLatitude());
    lngTextView.setText("Longitude: " + location.getLongitude());
    altTextView.setText("Altitude: " + location.getAltitude());
    accTextView.setText("Accuracy: " + location.getAccuracy());

    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
    try {
      String address = "Address Not Found";
      List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
      if (addressList != null && addressList.size() > 0) {
        address = "Address:\n";
        if (addressList.get(0).getSubThoroughfare() != null)
          address += addressList.get(0).getSubThoroughfare() + " ";
        if (addressList.get(0).getThoroughfare() != null)
          address += addressList.get(0).getThoroughfare() + "\n";
        if (addressList.get(0).getLocality() != null)
          address += addressList.get(0).getLocality() + "\n";
        if (addressList.get(0).getPostalCode() != null)
          address += addressList.get(0).getPostalCode() + "\n";
        if (addressList.get(0).getCountryName() != null)
          address += addressList.get(0).getCountryName() + " ";
      }
      TextView addresstextView = (TextView) findViewById(R.id.addressTextView);
      addresstextView.setText(address);

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    locationListener = new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        updateLocationInfo(location);
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras) {

      }

      @Override
      public void onProviderEnabled(String provider) {

      }

      @Override
      public void onProviderDisabled(String provider) {

      }
    };

    // ask for location permission
    if (Build.VERSION.SDK_INT < 23) {
      startListening();
    } else {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // is permission not granted then ask
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
      } else {
        //else we have permission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        //last known location
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
          updateLocationInfo(location);
        }
      }
    }

  }
}
