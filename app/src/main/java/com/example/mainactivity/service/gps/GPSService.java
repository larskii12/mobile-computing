package com.example.mainactivity.service.gps;

import android.location.Location;

public interface GPSService {
    void onGPSUpdate(Location location);
}
