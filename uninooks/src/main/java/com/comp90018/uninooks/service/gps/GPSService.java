package com.comp90018.uninooks.service.gps;

import android.location.Location;

public interface GPSService {
    void onGPSUpdate(Location location);
}
