package com.example.mainactivity.service.building;

import com.example.mainactivity.models.location.building.Building;
import com.example.mainactivity.models.review.ReviewType;

public interface BuildingService {

    Building getBuilding(int buildingId, ReviewType type) throws Exception;
}
