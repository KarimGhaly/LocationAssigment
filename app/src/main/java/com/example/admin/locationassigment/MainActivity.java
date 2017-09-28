package com.example.admin.locationassigment;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.locationassigment.model.GeocodeResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainTAG";
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    List<GeocodeResponse> geoList = new ArrayList<>();
    GeocodeResponse geocodeResponse;

    TextView txtLat;
    TextView txtLong;
    TextView txtAddress;
    Spinner SpinnerState;
    EditText etStreet;
    EditText etZIPCode;
    RecyclerView RVList;
    private RVAdapter rvAdapter;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetBinders();


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        111);
            }

        }
    }

    private void SetBinders() {
        txtLat = (TextView) findViewById(R.id.txtLat);
        txtLong = (TextView) findViewById(R.id.txtLong);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        SpinnerState= (Spinner) findViewById(R.id.spinnerState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.states_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerState.setAdapter(adapter);
        etStreet = (EditText) findViewById(R.id.etStreet);
        etZIPCode = (EditText) findViewById(R.id.etZip);
        RVList = (RecyclerView) findViewById(R.id.RVList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                Log.d(TAG, "onSuccess: " + currentLocation.toString());
                getAddressforCurrentLocation(currentLocation);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
            ShowRecylerView();
    }

    public void ConvertAddress(View view) {

        String Address = etStreet.getText().toString()+" ,"+SpinnerState.getSelectedItem().toString()+" ,"+etZIPCode.getText().toString();
        Address = Address.replace(' ','+');
        Call<GeocodeResponse> getLat = RetrofitHelper.getByAddress(Address);
        getLat.enqueue(new Callback<GeocodeResponse>() {
            @Override
            public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
                Log.d(TAG, "onResponse: "+response.body().getStatus());
                if(response.body().getStatus().equals("OK")) {
                    geocodeResponse = response.body();
                    ShowDialog();
                }
            }

            @Override
            public void onFailure(Call<GeocodeResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.toString());

            }
        });
    }

    public void ShowAllonMap(View view) {
        double[] lats = new double[geoList.size()];
        double[] longs = new double[geoList.size()];
        for (int i = 0; i < geoList.size(); i++) {
            lats[i] = geoList.get(i).getResults().get(0).getGeometry().getLocation().getLat();
            longs[i] = geoList.get(i).getResults().get(0).getGeometry().getLocation().getLng();
        }
        Intent intent = new Intent(MainActivity.this,MapsActivity.class);
        intent.putExtra("Type","List");
        intent.putExtra("Lats",lats);
        intent.putExtra("Longs",longs);
        startActivity(intent);
    }

    public void getAddressforCurrentLocation(Location loc) {
        if (currentLocation != null) {
            Call<GeocodeResponse> getResult = RetrofitHelper.getByLatLong(loc.getLatitude() + "," + loc.getLongitude());
            getResult.enqueue(new Callback<GeocodeResponse>() {
                @Override
                public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
                    String Address = response.body().getResults().get(0).getFormattedAddress();
                    txtLat.setText("Current Latitude: " + String.valueOf(currentLocation.getLatitude()));
                    txtLong.setText("Current Longitude: " + String.valueOf(currentLocation.getLongitude()));
                    txtAddress.setText("Current Address: " + Address);
                }

                @Override
                public void onFailure(Call<GeocodeResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: " + t.toString());
                }
            });
        }
    }


    public void ShowonMap(View view) {
        Intent intent = new Intent(MainActivity.this,MapsActivity.class);
        intent.putExtra("Type","Single Address");
        intent.putExtra("Lat",geocodeResponse.getResults().get(0).getGeometry().getLocation().getLat());
        intent.putExtra("Long",geocodeResponse.getResults().get(0).getGeometry().getLocation().getLng());
        startActivity(intent);
    }

    public void AddtoList(View view) {
        geoList.add(geocodeResponse);
        rvAdapter.ItemAdded(geoList);
        dialog.dismiss();

    }

    private void ShowDialog()
    {
        Log.d(TAG, "ShowDialog: ");
        dialog = new Dialog(MainActivity.this);
        dialog.setTitle("Converted");
        dialog.setContentView(R.layout.dialog_layout);
        TextView dialgTxtLat = dialog.findViewById(R.id.dlgTxtLat);
        TextView dialgTxtLong = dialog.findViewById(R.id.dlgTxtLong);
        TextView dialgTxtAddress = dialog.findViewById(R.id.dlgTxtAddress);
        dialgTxtLat.setText("Latitude: "+geocodeResponse.getResults().get(0).getGeometry().getLocation().getLat().toString());
        dialgTxtLong.setText("Longitude: "+geocodeResponse.getResults().get(0).getGeometry().getLocation().getLng().toString());
        dialgTxtAddress.setText("Address: "+geocodeResponse.getResults().get(0).getFormattedAddress());
        dialog.show();
    }

    private void ShowRecylerView(){
        rvAdapter = new RVAdapter(MainActivity.this,geoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        RecyclerView.ItemAnimator itemAnimatior = new DefaultItemAnimator();
        RVList.setAdapter(rvAdapter);
        RVList.setLayoutManager(layoutManager);
        RVList.setItemAnimator(itemAnimatior);
    }
}
