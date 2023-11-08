package com.comp90018.uninooks.service.gps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.comp90018.uninooks.service.emulator.EmulatorServiceImpl;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class GPSServiceImpl {

    private final FusedLocationProviderClient fusedLocationProviderClient;

    private LocationCallback locationCallback;

    private final LocationRequest locationRequestConfig;

    private static ArrayList<Location> locationsHistory;
    private final GPSService gpsService;

    private static boolean GPSPermissionStatus;

    public GPSServiceImpl(Context context, GPSService gpsService){

        locationsHistory = new ArrayList<>();

        this.gpsService = gpsService;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        // location request configuration default
        locationRequestConfig = new LocationRequest();
        locationRequestConfig.setInterval(3 * 1000);
        locationRequestConfig.setFastestInterval(3 * 1000);
        locationRequestConfig.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        reportGPSUpdateLoopBack();

    }

    /**
     * Start updating GPS data with given configuration
     */
    @SuppressLint("MissingPermission")
    public void startGPSUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequestConfig, locationCallback, Looper.myLooper());
    }

    /**
     * Stop updating GPS data
     */
    public void stopGPSUpdates() {
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    /**
     * Get the GPS last location, static call without creating object crossing the app
     * @return latest GPS location
     */
    public static LatLng getCurrentLocation(){
        if (EmulatorServiceImpl.isEmulator() || locationsHistory.size() == 0){

            // Return a default location - Melbourne Connect
//            LatLng melbourneConnect = new LatLng(38.17277920371164, -81.33774147106061);
            LatLng melbourneConnect = new LatLng(-37.8000, 144.9643);
            return melbourneConnect;
        }

        return new LatLng(locationsHistory.get(locationsHistory.size() - 1).getLatitude(), locationsHistory.get(locationsHistory.size() - 1).getLongitude());
    }

    /**
     * Get the GPS location history, static call without creating object crossing the app
     * @return GPS location history
     */
    public static ArrayList<Location> getGPSHistory(){

        if (locationsHistory.size() == 0){

            // Return a default location - Melbourne Connect
            Location melbourneConnect = new Location("");
            melbourneConnect.setLatitude(-37.8000);
            melbourneConnect.setLongitude(144.9643);

            ArrayList<Location> defaultHistory = new ArrayList<Location>();

            defaultHistory.add(melbourneConnect);
            return defaultHistory;
        }

        return locationsHistory;
    }

    /**
     * Update location and add to the location list
     */
    private void reportGPSUpdateLoopBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        // Add the updated location to location list
                        locationsHistory.add(location);
                        gpsService.onGPSUpdate(location);
                    }
                }
            }
        };
    }

    public static void setGPSPermissionStatus(boolean status){
        GPSPermissionStatus = status;
    }

    public static boolean getGPSPermission(){
        return GPSPermissionStatus;
    }
}