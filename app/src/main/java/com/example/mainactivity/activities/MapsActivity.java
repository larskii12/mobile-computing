package com.example.mainactivity.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.mainactivity.R;
import com.example.mainactivity.databinding.ActivityMapsBinding;
import com.example.mainactivity.service.gps.GPSService;
import com.example.mainactivity.service.gps.GPSServiceImpl;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GPSService {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private SearchView searchBar;
    private ImageButton filterButton;
    private BottomNavigationView bottomNav;

    private ImageButton locateMyLocation;

    private FusedLocationProviderClient fusedLocationClient;

    private final int standardCameraZoom = 18;

    private final int maxCameraZoom = 30;

    private final int minCameraZoom = 15;

    GPSServiceImpl gpsService;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint({"SetTextI18n", "HandlerLeak"})
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String info = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());

        // Start GPS continuously updating
        gpsService = new GPSServiceImpl(this, this);
        gpsService.startGPSUpdates();

        setContentView(binding.getRoot());

        searchBar = findViewById(R.id.searchBar);
        filterButton = (ImageButton) findViewById(R.id.filterButton);
        bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.searchNav);

        locateMyLocation = (ImageButton) findViewById(R.id.locate_my_location);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        // for the map (mMap), get the nearby locations and display it as a pointer
        /**
         *
         * on below line we are adding marker to that position::
         * mMap.addMarker(new MarkerOptions().position(latLng).title(location));
         *
         *
         */
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                String searchQuery = searchBar.getQuery().toString();
//                Intent intent = new Intent(MapsActivity.this, SearchResults.class);
//                intent.putExtra("searchQuery", searchQuery);
//                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // launch the filter page
//                Intent intent = new Intent(MapsActivity.this, FilterAdjustmentActivity.class);
//                startActivity(intent);
            }
        });

//            bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @SuppressLint("NonConstantResourceId")
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.homeNav:
//                        // go to home navigation page (Linda's page)
//                        break;
//                    case R.id.focusNav:
//                        // go to focus page
//                        break;
//                    case R.id.accountNav:
//                        // go to account page
//                        break;
//                }
//                return false;
//            }
//        });

        /**
         * Move camera to the current location
         */
        locateMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null && mMap.isMyLocationEnabled()) {
                    Location myLocation = mMap.getMyLocation();
                    if (myLocation != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(myLocation.getLatitude(),
                                        myLocation.getLongitude()), standardCameraZoom)); // Adjust zoom level as needed
                    }

                    showTextMessage("Latest Location " + GPSServiceImpl.getLatestLocation().getLatitude() + " " + GPSServiceImpl.getLatestLocation().getLongitude());
                }
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

        mMap.setMaxZoomPreference(maxCameraZoom);
        mMap.setMinZoomPreference(minCameraZoom);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(standardCameraZoom));

        // Show the user location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Get the latest current position
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, standardCameraZoom));
                    }
                });
    }

    /**
     * Show message text
     *
     * @param text as the showing message
     */
    private void showTextMessage(String text) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = text;
        handler.sendMessage(msg);
    }

    @Override
    public void onGPSUpdate(Location location) {

    }
}