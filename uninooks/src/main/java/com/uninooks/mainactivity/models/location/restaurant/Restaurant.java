package com.example.mainactivity.models.location.restaurant;

import com.example.mainactivity.models.location.Location;

import java.sql.Time;

public class Restaurant extends Location {

    /**
     * If restaurant can do vegan options
     */
    Boolean hasVegetarianOptions;

    /**
     * Floor level of where the restaurant is located
     */
    int floorLevel;

    public Boolean getHasVegetarianOptions() {
        return hasVegetarianOptions;
    }

    public void setHasVegetarianOptions(Boolean hasVegetarianOptions) {
        this.hasVegetarianOptions = hasVegetarianOptions;
    }

    public int getFloorLevel() {
        return floorLevel;
    }

    public void setFloorLevel(int floorLevel) {
        this.floorLevel = floorLevel;
    }
}
