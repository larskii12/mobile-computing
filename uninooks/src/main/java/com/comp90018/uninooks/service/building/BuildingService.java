package com.comp90018.uninooks.service.building;

import com.comp90018.uninooks.models.location.building.Building;
import com.comp90018.uninooks.models.review.ReviewType;

public interface BuildingService {

    Building getBuilding(int buildingId, ReviewType type) throws Exception;
}
