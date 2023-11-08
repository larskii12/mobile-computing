package com.comp90018.uninooks.models.navigation;

import com.google.android.gms.maps.model.LatLng;

public class Navigation {

    private LatLng startLocation;

    private LatLng endLocation;

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }
}
