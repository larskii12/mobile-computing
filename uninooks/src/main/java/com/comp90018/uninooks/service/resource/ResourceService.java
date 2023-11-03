package com.comp90018.uninooks.service.resource;

import com.comp90018.uninooks.models.location.resource.Resource;

import java.util.List;

public interface ResourceService {

    List<Resource> getResourceFromBuilding(int buildingId) throws Exception;
}
