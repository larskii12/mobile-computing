package com.comp90018.uninooks.models.location.restaurant;

import com.comp90018.uninooks.models.location.Location;

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
