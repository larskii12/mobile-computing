package com.example.mainactivity.models.location.library;

import com.example.mainactivity.models.location.Location;

public class Library extends Location {

    private boolean hasQuietZones;

    private Integer capacity;

    public boolean isHasQuietZones() {
        return hasQuietZones;
    }

    public void setHasQuietZones(boolean hasQuietZones) {
        this.hasQuietZones = hasQuietZones;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
