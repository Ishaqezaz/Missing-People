package com.example.missingpeople3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class userLocation extends AppCompatActivity implements LocationListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReferenceFromUrl("https://missingpeople3-92f63-default-rtdb.firebaseio.com/");
    LocationManager locationManager;

    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    final static int PERMISSION_ALL = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //startActivity(new Intent(userLocation.this, MapsActivity.class));



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(PERMISSIONS, PERMISSION_ALL );
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLocation();
                    handler.postDelayed(this, 1000 * 10); //delayMil = 1000(mil_sec)*30(sec)*0(min) Gives location updater every 3 min
                }
            },1000);

        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("mylog", "Get location:" + location.getLongitude() + "," + location.getLatitude());


        String lang = Double.toString(location.getLongitude());
        String lat = Double.toString(location.getLatitude());

        myRef.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("lat").setValue(lat);
        myRef.child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("lang").setValue(lang);

        locationManager.removeUpdates(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(userLocation.this, MapsActivity.class));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLocation();
                    handler.postDelayed(this,1000 * 10);
                }
            },1000);

        }
    }

    public void requestLocation(){
        if(locationManager == null){
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,10000, this);
            }
        }
    }

}