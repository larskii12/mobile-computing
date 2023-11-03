package com.comp90018.uninooks.service.building;

import com.comp90018.uninooks.config.DatabaseHelper;
import com.comp90018.uninooks.models.location.building.Building;
import com.comp90018.uninooks.models.review.ReviewType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BuildingServiceImpl implements BuildingService {

    Connection connector = new DatabaseHelper().getConnector();

    /**
     * Get the specific building
     *
     * @param buildingId, as the review id
     * @return Building
     */
    public Building getBuilding(int buildingId, ReviewType type) throws Exception {

        Building building = new Building();

        try {

            String query = "SELECT * FROM mobilecomputing.\"building\" WHERE \"building_id\" = ?";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, buildingId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Set user information
            if (resultSet.next()) { // Ensure there's a row in the result set
                // Set review information
                building.setId(resultSet.getInt("building_id"));
                building.setName(resultSet.getString("building_name"));
                building.setFaculty(resultSet.getString("building_faculty"));
                building.setHasAccessibility(resultSet.getBoolean("building_has_accessibility"));
                building.setLatitude(resultSet.getDouble("building_latitude"));
                building.setLatitude(resultSet.getDouble("building_longitude"));
                return building;
            }

        }

        // If exception happens when querying user
        catch (Exception e) {
            throw new Exception("Some error happened, please contact the IT administrator.");
        }
        // Return user information
        return null;
    }
}
