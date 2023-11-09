package com.comp90018.uninooks.activities;

import static java.util.Objects.nonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.databinding.ActivityNavigationBinding;
import com.comp90018.uninooks.models.navigation.Navigation;
import com.comp90018.uninooks.service.gps.GPSService;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback, GPSService {

    private GoogleMap mMap;
    private @NonNull ActivityNavigationBinding binding;

    private ImageButton locateMyLocation;

    private FusedLocationProviderClient fusedLocationClient;

    private final int standardCameraZoom = 18;

    GPSServiceImpl gpsService;

    private int userId;

    private String userEmail;

    private String userName;

    private List<Navigation> navigationList;

    private LatLng location;

    private String locationName;

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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID_EXTRA", 0);
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        userName = intent.getStringExtra("USER_NAME_EXTRA");
        location = new LatLng(intent.getDoubleExtra("LATITUDE", 0), intent.getDoubleExtra("LONGITUDE", 0));
        locationName = intent.getStringExtra("LOCATION_NAME");

        gpsService = new GPSServiceImpl(this, this, GPSServiceImpl.getGPSHistory());

        locateMyLocation = (ImageButton) findViewById(R.id.locate_my_location);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ImageButton backButton = findViewById(R.id.imageButton);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        /**
         * Move camera to the current location
         */
        locateMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mMap != null && mMap.isMyLocationEnabled()) {
                        Location myLocation = mMap.getMyLocation();

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), standardCameraZoom)); // Adjust zoom level as needed
                    }
                } catch (Exception e) {
                    System.out.println("Location update fails, please try again.");
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onStart() {
        super.onStart();
        // Check GPS permission
        if (!GPSServiceImpl.getGPSPermission()) {
            showTextMessage("Location error, please enable location permission to use this function.");
        }

        // Start GPS continuously updating
        else {
            gpsService.startGPSUpdates();
        }
    }

    public void onRestart() {
        super.onRestart();
    }

    // When back button pressed
    public void onBackPressed() {
        super.onBackPressed();
        gpsService.stopGPSUpdates();
    }

    public void onPause() {
        super.onPause();
        gpsService.stopGPSUpdates();
    }

    public void onResume() {
        super.onResume();
        gpsService.startGPSUpdates();
    }

    public void onStop() {
        super.onStop();
        ;
        gpsService.stopGPSUpdates();
    }

    public void onDestroy() {
        super.onDestroy();
        ;
        gpsService.stopGPSUpdates();
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

        LatLng original = GPSServiceImpl.getCurrentLocation();
        LatLng goal = location;

        int minCameraZoom = 15;
        mMap.setMinZoomPreference(minCameraZoom);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(standardCameraZoom));

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Show the user location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Get the latest current position
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, standardCameraZoom));
                            }
                        }
                );

        new Thread() {
            public void run() {

                try {

                    findWalkingPath(original, goal);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            double highestLatitude = -99999;
                            double lowestLatitude = 99999;
                            double highestLongitude = -99999;
                            double lowestLongitude = 99999;

                            for (Navigation navigation : navigationList) {
                                LatLng pointA = navigation.getStartLocation();
                                LatLng pointB = navigation.getEndLocation();

                                highestLatitude = Math.max(pointA.latitude, highestLatitude);
                                lowestLatitude = Math.min(pointA.latitude, lowestLatitude);
                                highestLongitude = Math.max(pointB.longitude, highestLongitude);
                                lowestLongitude = Math.min(pointB.longitude, lowestLongitude);

                                highestLatitude = Math.max(pointA.latitude, highestLatitude);
                                lowestLatitude = Math.min(pointA.latitude, lowestLatitude);
                                highestLongitude = Math.max(pointB.longitude, highestLongitude);
                                lowestLongitude = Math.min(pointB.longitude, lowestLongitude);


                                Polyline line = mMap.addPolyline(new PolylineOptions()
                                        .add(pointA, pointB)
                                        .width(10)
                                        .color(Color.BLUE));
                            }

                            LatLng southwest = new LatLng(lowestLatitude, lowestLongitude);
                            LatLng northeast = new LatLng(highestLatitude, highestLongitude);
                            LatLngBounds bounds = new LatLngBounds(southwest, northeast);

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 120);
                            mMap.addMarker(new MarkerOptions().position(location).title(locationName));
                            mMap.animateCamera(cameraUpdate);
                        }

                    });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    public void showFullRoute(LatLng pointA, LatLng pointB){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (pointA != null && pointB != null){
            builder.include(pointA);
            builder.include(pointB);
            LatLngBounds bounds = builder.build();

            int padding = 50;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cameraUpdate);
        }
    }

    private void navigationRouteAnimation(){

    }

    private void findWalkingPath(LatLng currentPosition, LatLng destination) throws IOException {

                try {
                    String origin = currentPosition.latitude + "," + currentPosition.longitude;
                    String goal = destination.latitude + "," + destination.longitude;

                    InputStream inputStream = MainActivity.getAppContext().getResources().openRawResource(R.raw.config);
                    Properties properties = new Properties();
                    properties.load(inputStream);

                    String requestURL = "https://maps.googleapis.com/maps/api/directions/json?" +
                            "origin=" + origin + "&" +
                            "destination=" + goal + "&" +
                            "mode=" + "walking" + "&" +
                            "key=" + properties.getProperty("API_KEY");


                    URL url = new URL(requestURL);
                    HttpURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    StringBuffer response = null;
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        in.close();
                    }

                    ObjectMapper mapper = new ObjectMapper();

                    navigationList = new ArrayList<>();
                    System.out.println(response.toString());

                    if (nonNull(response)) {
                        JSONObject directionsResponse = new JSONObject(response.toString());

                        JSONObject routesResponse = directionsResponse.getJSONArray("routes").getJSONObject(0);

                        JSONObject legsResponse = routesResponse.getJSONArray("legs").getJSONObject(0);

                        JSONArray stepsResponse = legsResponse.getJSONArray("steps");


                        for (int i = 0; i < stepsResponse.length(); i++) {
                            JSONObject step = stepsResponse.getJSONObject(i);
                            Navigation navigation = new Navigation();

                            JSONObject startLocationResponse = step.getJSONObject("start_location");

                            double startLat = startLocationResponse.getDouble("lat");
                            double startLng = startLocationResponse.getDouble("lng");

                            LatLng startLocation = new LatLng(startLat, startLng);
                            navigation.setStartLocation(startLocation);

                            JSONObject endLocationResponse = step.getJSONObject("end_location");

                            double endLat = endLocationResponse.getDouble("lat");
                            double endLng = endLocationResponse.getDouble("lng");

                            LatLng endLocation = new LatLng(endLat, endLng);
                            navigation.setEndLocation(endLocation);

                            System.out.println("Start Location: " + startLat + " " + startLng);
                            System.out.println("End Location: " + endLat + " " + endLng);

                            navigationList.add(navigation);
                        }
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

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