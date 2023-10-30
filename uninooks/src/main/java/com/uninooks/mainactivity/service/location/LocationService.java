package com.example.mainactivity.service.location;

import com.example.mainactivity.models.location.Location;
import com.example.mainactivity.models.location.LocationType;
import com.example.mainactivity.models.location.library.Library;
import com.example.mainactivity.models.location.restaurant.Restaurant;
import com.example.mainactivity.models.location.study_space.StudySpace;

import java.util.List;

public interface LocationService {

    Object findLocationById(int locationId, LocationType locationType) throws Exception;

    Library findLibraryById(int locationId) throws Exception;

    Restaurant findRestaurantById(int locationId) throws Exception;

    StudySpace findStudySpaceById(int locationId) throws Exception;

    List<Location> findAllLocations(String locationType,
                                    String name,
                                    boolean isAscending) throws Exception;

}
