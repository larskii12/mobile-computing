package com.comp90018.uninooks.service.resource;

import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.location.resource.Resource;
import com.comp90018.uninooks.models.location.resource.ResourceType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResourceServiceImpl implements ResourceService {

    Connection connector = new DatabaseHelper().getConnector();

    /**
     * Get all resource based from location
     *
     * @param buildingId, as the review id
     * @return List of Resources
     */
    public List<Resource> getResourceFromBuilding(int buildingId) throws Exception {

        List<Resource> resourceList = new ArrayList<>();

        try {

            String query = "SELECT * FROM mobilecomputing.\"resource\" WHERE \"resource_building_id\" = ?";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, buildingId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) { // Iterate all the resulting rows from the query
                Resource resource = new Resource();

                // Set resource information
                resource.setId(resultSet.getInt("resource_id"));
                resource.setName(resultSet.getString("resource_name"));
                resource.setBuildingId(resultSet.getInt("resource_building_id"));

                resource.setType(ResourceType.toType(resultSet.getString("resource_type")));
                resource.setLatitude(resultSet.getDouble("resource_latitude"));
                resource.setLongitude(resultSet.getDouble("resource_longitude"));

                resource.setFloorLevel(resultSet.getInt("resource_floor_level"));

                resourceList.add(resource);
            }

            return resourceList;
        } // If exception happens when querying user
        catch (Exception e) {
            throw new Exception("Some error happened, please contact the IT administrator.");
        }
    }



    public List<Resource> getResourceFromKeyWord(String keyWord) throws Exception {

        List<Resource> resourceList = new ArrayList<>();

        try {

            String query = "SELECT * FROM mobilecomputing.\"resource\" WHERE LOWER(\"resource_name\") LIKE ?";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setString(1, '%' + keyWord.toLowerCase() + '%');

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) { // Iterate all the resulting rows from the query
                Resource resource = new Resource();

                // Set resource information
                resource.setId(resultSet.getInt("resource_id"));
                resource.setName(resultSet.getString("resource_name"));
                resource.setBuildingId(resultSet.getInt("resource_building_id"));

                resource.setType(ResourceType.valueOf(resultSet.getString("resource_type")));
                resource.setLatitude(resultSet.getDouble("resource_latitude"));
                resource.setLongitude(resultSet.getDouble("resource_longitude"));

                resource.setFloorLevel(resultSet.getInt("resource_floor_level"));

                resourceList.add(resource);

                System.out.println("dddddddddddddddddddddddddddd");
            }

            return resourceList;
        } // If exception happens when querying user
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Some error happened, please contact the IT administrator.");
        }
    }
}
