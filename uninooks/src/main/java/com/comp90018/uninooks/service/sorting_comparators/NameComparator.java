package com.comp90018.uninooks.service.sorting_comparators;

import com.comp90018.uninooks.models.location.Location;

import java.util.Comparator;

public class NameComparator implements Comparator<Location> {
    @Override
    public int compare(Location o1, Location o2) {
        return o1.getName().compareTo(o2.getName());
    }
}

