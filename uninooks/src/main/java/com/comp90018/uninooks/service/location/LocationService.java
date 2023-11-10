package com.comp90018.uninooks.service.location;

import com.comp90018.uninooks.models.location.Location;
import com.comp90018.uninooks.models.location.LocationType;
import com.comp90018.uninooks.models.location.library.Library;
import com.comp90018.uninooks.models.location.restaurant.Restaurant;
import com.comp90018.uninooks.models.location.study_space.StudySpace;

import java.util.List;

public interface LocationService {

    Object findLocationById(int locationId, LocationType locationType) throws Exception;

    Library findLibraryById(int locationId) throws Exception;

    Restaurant findRestaurantById(int locationId) throws Exception;

    StudySpace findStudySpaceById(int locationId) throws Exception;

    List<Location> findAllLocations(String locationType, String name, boolean isAscending) throws Exception;

}
