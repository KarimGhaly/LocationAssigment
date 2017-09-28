package com.example.admin.locationassigment;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        switch (getIntent().getStringExtra("Type")) {
            case "Single Address":
                LatLng Selected = new LatLng((getIntent().getDoubleExtra("Lat", 0.0)), getIntent().getDoubleExtra("Long", 0.0));
                mMap.addMarker(new MarkerOptions().position(Selected).title("Address"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Selected, 18));
                break;
            case "List":
                double[] lats = getIntent().getDoubleArrayExtra("Lats");
                double[] longs = getIntent().getDoubleArrayExtra("Longs");
                for (int i = 0; i < lats.length; i++) {
                    LatLng SelectedITEM = new LatLng(lats[i],longs[i]);
                    mMap.addMarker(new MarkerOptions().position(SelectedITEM));
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lats[0],longs[0])));
                break;
        }
    }
}
