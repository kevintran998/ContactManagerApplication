package com.kkt160130.contactmanager;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManger;
    private Geocoder geocoder;
    private List<Address> addressList;
    private String contactAddress;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_fragment);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // retrieve address from intent
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        contactAddress = bundle.getString("address");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        geocoder = new Geocoder(MapsFragment.this);
        addressList = new ArrayList<>();
        Address address;

        // get location of contact using geocoder and add it to the address list
        try {
            addressList = geocoder.getFromLocationName(contactAddress, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if there is an address in the list (there should only be one address max)
        if (addressList.size() > 0)
        {
            // add contact location to map
            address = addressList.get(0); // grab the address
            LatLng latLngContactLocation = new LatLng(address.getLatitude(), address.getLongitude()); // get longitude and latitude
            mMap.addMarker(new MarkerOptions().position(latLngContactLocation).title("Contact Location")); // add a marker to google maps
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngContactLocation)); // move google maps camera to the location

            // get current location of device
            LatLng latLngCurrentLocation = getCurrentLocation();

            // calculate distance between locations
            double distance = calculateDistance(latLngContactLocation, latLngCurrentLocation);

            // display the information about the address
            TextView textView;
            textView = findViewById(R.id.contactAddress);
            textView.setText(this.contactAddress);
            textView = findViewById(R.id.longitude);
            textView.setText(String.valueOf(address.getLongitude()));
            textView = findViewById(R.id.latitude);
            textView.setText(String.valueOf(address.getLatitude()));
            textView = findViewById(R.id.distance);
            textView.setText(String.valueOf(distance + " km"));
        }
    }

    // location listener to retrieve longitude and latitude of current location
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        // functions not used
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    // function finds current location of the device
    private LatLng getCurrentLocation()
    {
        Location loc;

        // check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsFragment.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // grab location
        locationManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        loc = locationManger.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        // add current location to map
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude()); // get longitude and latitude
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location")); // add a marker to google maps
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng)); // move google maps camera to the location

        return latLng;
    }

    // calculates distance between two coordinates
    private double calculateDistance(LatLng contact, LatLng current)
    {
        final double EARTH_RADIUS = 6371;
        double a;
        double c;
        double distance;
        double contactLat;
        double contactLon;
        double currentLat;
        double currentLon;

        // convert values to radians
        contactLat = contact.latitude * Math.PI / 180;
        contactLon = contact.longitude * Math.PI / 180;
        currentLat = current.latitude * Math.PI / 180;
        currentLon = current.longitude * Math.PI / 180;

        // calculate using great circle distance formula
        a = Math.pow(Math.sin((currentLat - contactLat) / 2), 2) + Math.cos(contactLat) * Math.cos(currentLat) *
                Math.pow(Math.sin((currentLon - contactLon) / 2), 2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distance = EARTH_RADIUS * c;

        return distance;
    }
}
