package com.comp90018.uninooks.models.location.library;

import static java.util.Objects.nonNull;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.comp90018.uninooks.models.location.Location;

public class Library extends Location {

    private boolean hasQuietZones;

    private double average_rating;

    private Integer capacity;

    public Library() {}

    public Library(Parcel in) {
        super(in);
        capacity = in.readInt();
        average_rating = in.readDouble();
        hasQuietZones = in.readByte() != 0;
    }

    public static final Creator<Library> CREATOR = new Creator<Library>() {
        @Override
        public Library createFromParcel(Parcel in) {
            return new Library(in);
        }

        @Override
        public Library[] newArray(int size) {
            return new Library[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        if(nonNull(capacity)) {
            dest.writeInt(capacity);
        }
        dest.writeDouble(average_rating);

        byte isOpenTodayByte = (byte) (hasQuietZones ? 1 : 0);
        dest.writeByte(isOpenTodayByte);
    }
}
