package com.example.mrami014.chapter10maps;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent originalIntent = getIntent();
        city = originalIntent.getStringExtra(Cities.CITY_KEY);
        if (city == null){
            city = Cities.DEFAULT_CITY;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button buttonSearch = (Button) findViewById(R.id.buttonStartMapSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLink(v);
            }
        });
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


        double latitude = Cities.DEFAULT_LATITUDE;
        double longitude = Cities.DEFAULT_LONGITUDE;

        Cities cities = MainActivity.cities;

        String attraction = cities.getAttraction(city);

        Geocoder geocoder = new Geocoder(this);
        try {
            String address = attraction + ", " + city;
            List<Address> addresses = geocoder.getFromLocationName(address, 5);
            if (addresses !=null) {
                latitude = addresses.get(0).getLatitude();
                longitude = addresses.get(0).getLongitude();
            } else {
                city = Cities.DEFAULT_CITY;
            }
        }catch ( IOException ioe) {
            city = Cities.DEFAULT_CITY;
        }

        LatLng cityLocation = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cityLocation, 15.5f);
        mMap.moveCamera(cameraUpdate);

        MarkerOptions options = new MarkerOptions();
        options.position(cityLocation);
        options.title(attraction);
        options.snippet(Cities.MESSAGE);
        mMap.addMarker(options);


        CircleOptions circleOptions = new CircleOptions().center(cityLocation).radius(500)
                .strokeWidth(10.0f).strokeColor(0xFFFF0000);
        mMap.addCircle(circleOptions);

    }

    public void searchLink(View v){
        EditText editTextLocationSearch = (EditText) findViewById(R.id.editTextMapSearch);
        String locationToSearch = editTextLocationSearch.getText().toString();
        List<Address> addressList = null;

        if (locationToSearch != null || !locationToSearch.equals("")){
            Geocoder geocoder = new Geocoder(this);

            try{
                addressList = geocoder.getFromLocationName(locationToSearch, 1);
            }catch (IOException ioe){
                ioe.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Your Searched Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            //Hides keyboard after searching 
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

    }
}
