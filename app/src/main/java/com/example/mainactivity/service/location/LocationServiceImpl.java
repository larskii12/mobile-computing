package com.example.mainactivity.service.location;

import com.example.mainactivity.config.DatabaseHelper;
import com.example.mainactivity.models.location.Location;
import com.example.mainactivity.models.location.LocationType;
import com.example.mainactivity.models.location.library.Library;
import com.example.mainactivity.models.location.restaurant.Restaurant;
import com.example.mainactivity.models.location.study_space.StudySpace;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.List;

public class LocationServiceImpl implements LocationService {

    Connection connector = new DatabaseHelper().getConnector();

    public Object findLocationById(int locationId, LocationType locationType) throws Exception {

        switch (locationType) {
            case LIBRARY:
                return findLibraryById(locationId);
            case RESTAURANT:
                return findRestaurantById(locationId);
            case STUDY_SPACE:
                return findStudySpaceById(locationId);
            default:
                throw new Exception("Invalid location type. Location not found.");
        }
    }

    public Library findLibraryById(int locationId) throws Exception {

        Library library = new Library();

        try {
            String query = "SELECT * FROM mobilecomputing.\"library\" WHERE \"library_id\" = ?";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, locationId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // Ensure there's a row in the result set
                // Set library information
                library.setId(resultSet.getInt("library_id"));
                library.setBuildingId(resultSet.getInt("library_building_id"));
                library.setName(resultSet.getString("library_name"));

                library.setOpenTime(resultSet.getTime("library_open_time"));
                library.setCloseTime(resultSet.getTime("library_close_time"));

                Array daysDb = resultSet.getArray("library_opening_days");
                Integer[] days = (Integer[]) daysDb.getArray();
                library.setOpeningDays(days);

                Array busyDb = resultSet.getArray("library_busy_hours");
                Time[] busyHours = (Time[]) busyDb.getArray();
                library.setBusyHours(busyHours);

                library.setCapacity(resultSet.getInt("capacity"));
                library.setHasQuietZones(resultSet.getBoolean("library_has_quiet_zones"));

                return library;
            }

        } catch(Exception e){ // If exception happens when querying library
            throw new Exception("Some error happened, please contact the IT administrator.");
        }

        return null;
    }

    public Restaurant findRestaurantById(int locationId) throws Exception {

        Restaurant restaurant = new Restaurant();

        try {
            String query = "SELECT * FROM mobilecomputing.\"restaurant\" WHERE \"restaurant_id\" = ?";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, locationId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Set user information
            if (resultSet.next()) { // Ensure there's a row in the result set
                restaurant.setId(resultSet.getInt("restaurant_id"));
                restaurant.setBuildingId(resultSet.getInt("restaurant_building_id"));
                restaurant.setName(resultSet.getString("restaurant_name"));

                restaurant.setOpenTime(resultSet.getTime("restaurant_open_time"));
                restaurant.setCloseTime(resultSet.getTime("restaurant_close_time"));

                Array daysDb = resultSet.getArray("restaurant_opening_days");
                Integer[] days = (Integer[]) daysDb.getArray();
                restaurant.setOpeningDays(days);

                Array busyDb = resultSet.getArray("restaurant_busy_hours");
                Time[] busyHours = (Time[]) busyDb.getArray();
                restaurant.setBusyHours(busyHours);

                restaurant.setFloorLevel(resultSet.getInt("restaurant_floor_level"));
                restaurant.setHasVegetarianOptions(resultSet.getBoolean("restaurant_vegetarian_options"));

                return restaurant;
            }

        } catch(Exception e){ // If exception happens when querying restaurant
            throw new Exception("Some error happened, please contact the IT administrator.");
        }

        return null;
    }

    public StudySpace findStudySpaceById(int locationId) throws Exception {

        StudySpace studySpace = new StudySpace();

        try {
            String query = "SELECT * FROM mobilecomputing.\"study_space\" WHERE \"study_space_id\" = ?";

            PreparedStatement preparedStatement = connector.prepareStatement(query);
            preparedStatement.setInt(1, locationId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // Ensure there's a row in the result set
                // Set study space information
                studySpace.setId(resultSet.getInt("study_space_id"));
                studySpace.setBuildingId(resultSet.getInt("study_space_building_id"));
                studySpace.setName(resultSet.getString("study_space_name"));

                studySpace.setOpenTime(resultSet.getTime("study_space_open_time"));
                studySpace.setCloseTime(resultSet.getTime("study_space_close_time"));

                Array daysDb = resultSet.getArray("study_space_opening_days");
                Integer[] days = (Integer[]) daysDb.getArray();
                studySpace.setOpeningDays(days);

                Array busyDb = resultSet.getArray("study_space_busy_hours");
                Time[] busyHours = (Time[]) busyDb.getArray();
                studySpace.setBusyHours(busyHours);

                studySpace.setLibraryId(resultSet.getInt("study_space_library_id"));
                studySpace.setCapacity(resultSet.getInt("study_space_capacity"));
                studySpace.setFloorLevel(resultSet.getInt("study_space_floor_level"));
                studySpace.setMinimumAccessAQFLevel(resultSet.getInt("study_space_minimum_access_AQF_level"));
                studySpace.setTalkAllowed(resultSet.getBoolean("study_space_talk_allowed"));

                return studySpace;
            }

        } catch(Exception e){ // If exception happens when querying study space
            throw new Exception("Some error happened, please contact the IT administrator.");
        }

        return null;
    }


    public List<Location> findAllLocations(String locationType,
                                           String name,
                                           boolean isAscending) {
        return null;
    }
}
