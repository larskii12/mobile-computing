package com.comp90018.uninooks.models.location;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;

public class Location {

    /**
     * Location ID if acceptable
     */
    private int id;

    /**
     * Associated Building ID
     */
    private int buildingId;

    /**
     * Location's name
     */
    private String name;

    /**
     * Opening Time
     */
    private Time openTime;

    /**
     * Closing Time
     */
    private Time closeTime;

    /**
     * Is Open Today
     */
    private boolean isOpenToday;

    /**
     * is opening now
     */
    private boolean isOpeningNow;

    /**
     * IS open 24/7
     */
    private boolean isOpen24By7;

    /**
     * List of opening days in the location
     */
    private Integer[] openingDays;

    /**
     * List of Busy Times
     */
    private Time[] busyHours;

    private LatLng location;

    private double distanceFromCurrentPosition;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Time getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Time openTime) {
        this.openTime = openTime;
    }

    public Time getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Time closeTime) {
        this.closeTime = closeTime;
    }

    public Integer[] getOpeningDays() {
        return openingDays;
    }

    public void setOpeningDays(Integer[] openingDays) {
        this.openingDays = openingDays;
    }

    public Time[] getBusyHours() {
        return busyHours;
    }

    public void setBusyHours(Time[] busyHours) {
        this.busyHours = busyHours;
    }

    public double getDistanceFromCurrentPosition() {
        return distanceFromCurrentPosition;
    }

    public void setDistanceFromCurrentPosition(double distanceFromCurrentPosition) {
        this.distanceFromCurrentPosition = distanceFromCurrentPosition;
    }

    public boolean issOpenToday() {
        return isOpenToday;
    }

    public void setIsOpenToday(boolean openToday) {
        isOpenToday = openToday;
    }

    public boolean isOpen24By7() {
        return isOpen24By7;
    }

    public void setOpen24By7(boolean open24By7) {
        isOpen24By7 = open24By7;
    }

    public boolean isOpeningNow() {
        return isOpeningNow;
    }

    public void setIsOpeningNow(boolean openingNow) {
        isOpeningNow = openingNow;
    }

}
