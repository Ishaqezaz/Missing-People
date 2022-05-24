package com.example.missingpeople3;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    // Uppdaterar mappen under körning
    @Override
    public void onMapReady(GoogleMap googleMap) {

        List<LatLng> latLngs = null;

        try {
            latLngs = readItems(R.raw.cords);
        } catch (JSONException e) {
            Toast.makeText(MapsActivity.this, "läsningen misslyckades.", Toast.LENGTH_LONG).show();
        }

        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .data(latLngs)
                .radius(50)
                .opacity(1)
                .build();

        TileOverlay overlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //går att hämta sin sinaste position med markering
        LatLng indiaLatLng = new LatLng(62.2, 17.1);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(indiaLatLng));
        //enda filen jag kunde hitta med massa koordinater. Centrerar kameran över kandet.
    }


    //kan läsas direkt från databasen eller från en fil med data i realtid från databasen
    private List<LatLng> readItems(@RawRes int resource) throws JSONException {
        List<LatLng> result = new ArrayList<>();
        InputStream inputStream = this.getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lon");
            result.add(new LatLng(lat, lng));
        }
        return result;
    }

}
