package com.example.mainactivity.service.resource;

import com.example.mainactivity.models.location.resource.Resource;

import java.util.List;

public interface ResourceService {

    List<Resource> getResourceFromBuilding(int buildingId) throws Exception;
}
