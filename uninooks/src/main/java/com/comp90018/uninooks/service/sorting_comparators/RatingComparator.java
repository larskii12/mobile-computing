package com.comp90018.uninooks.service.sorting_comparators;

import com.comp90018.uninooks.models.location.Location;

import java.util.Comparator;
import java.util.HashMap;

public class RatingComparator implements Comparator<Location> {
    HashMap<String, String> ratingsByLocation;

    public RatingComparator(HashMap<String, String> ratingsByLocation) {
        this.ratingsByLocation = ratingsByLocation;
    }

    @Override
    public int compare(Location o1, Location o2) {
        double o1Rating = Double.parseDouble(ratingsByLocation.get(o1.getName()));
        double o2Rating = Double.parseDouble(ratingsByLocation.get(o2.getName()));
        return Double.compare(o1Rating, o2Rating);
    }
}
