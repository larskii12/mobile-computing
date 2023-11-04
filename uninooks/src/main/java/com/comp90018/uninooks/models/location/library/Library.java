package com.comp90018.uninooks.models.location.library;

import com.comp90018.uninooks.models.location.Location;

public class Library extends Location {

    private boolean hasQuietZones;

    private double average_rating;

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

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(double average_rating) {
        this.average_rating = average_rating;
    }
}
