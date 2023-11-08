package com.comp90018.uninooks.models.location;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;

public class Location implements Parcelable {

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

    private int distanceFromCurrentPosition;

    /**
     * Type of location
     */
    private String type;

    private double average_rating;

    public Location() {}
    protected Location(Parcel in) {
        id = in.readInt();
        buildingId = in.readInt();
        name = in.readString();
        isOpenToday = in.readByte() != 0;
        isOpeningNow = in.readByte() != 0;
        isOpen24By7 = in.readByte() != 0;
        location = in.readParcelable(LatLng.class.getClassLoader());
        distanceFromCurrentPosition = in.readInt();
        type = in.readString();
        average_rating = in.readDouble();
        openTime = new Time(in.readLong());
        closeTime = new Time(in.readLong());
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

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

    public int getDistanceFromCurrentPosition() {
        return distanceFromCurrentPosition;
    }

    public void setDistanceFromCurrentPosition(int distanceFromCurrentPosition) {
        this.distanceFromCurrentPosition = distanceFromCurrentPosition;
    }

    public boolean isOpenToday() {
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

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(double rating) {
        average_rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(buildingId);
        dest.writeString(name);

        byte isOpenTodayByte = (byte)(isOpenToday?1:0);
        dest.writeByte(isOpenTodayByte);

        byte isOpeningNowByte = (byte)(isOpeningNow?1:0);
        dest.writeByte(isOpeningNowByte);

        byte isOpen24By7Byte = (byte)(isOpen24By7?1:0);
        dest.writeByte(isOpen24By7Byte);

        dest.writeParcelable(location, flags);
        dest.writeInt(distanceFromCurrentPosition);
        dest.writeString(type);

        dest.writeDouble(average_rating);

        long openTimeInMillis = openTime.getTime();
        dest.writeLong(openTimeInMillis);

        long closeTimeInMillis = closeTime.getTime();
        dest.writeLong(closeTimeInMillis);
    }
}
