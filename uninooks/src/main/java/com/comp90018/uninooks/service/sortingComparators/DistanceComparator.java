package com.comp90018.uninooks.service.sortingComparators;
import com.comp90018.uninooks.models.location.Location;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Location> {
    @Override
    public int compare(Location o1, Location o2) {
        return Double.compare(o1.getDistanceFromCurrentPosition(), o2.getDistanceFromCurrentPosition());
    }
}